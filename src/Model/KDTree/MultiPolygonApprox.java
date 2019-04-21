package Model.KDTree;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.*;

public class MultiPolygonApprox extends PolygonApprox {
	byte[] pointtypes;
	Map<Point2D, List<Point2D>> ways;
	
	public MultiPolygonApprox(List<? extends List<? extends Point2D>> rel) {
		ways = new HashMap<>();
		List<List<Point2D>> mergedRel = new ArrayList<>();
		mergeWays(rel);
		ways.forEach((key, way) -> {
			if (key == way.get(0)) {
				mergedRel.add(way);
			}
		});
		int npoints = 0;
		for (List<?> l : mergedRel) npoints += l.size();
		coords = new float[npoints << 1];
		pointtypes = new byte[npoints];
		Arrays.fill(pointtypes, (byte) PathIterator.SEG_LINETO);
		int coord = 0;
		int point = 0;
		for (List<? extends Point2D> l : mergedRel) {
			pointtypes[point] = (byte) PathIterator.SEG_MOVETO;
			point += l.size();
			for (Point2D p : l) {
				coords[coord++] = (float) p.getX();
				coords[coord++] = (float) p.getY();
			}
		}
		init();
	}
	
	public double distTo(Point2D p) {
		double dist = Double.MAX_VALUE;
		double px = p.getX();
		double py = p.getY();
		for (int i = 2 ; i < coords.length ; i += 2) {
			if (pointtypes[i/2] != PathIterator.SEG_MOVETO) {
				dist = Math.min(dist, Line2D.ptSegDist(coords[i - 2], coords[i - 1], coords[i], coords[i + 1], px, py));
			}
		}
		return dist;
	}

	public PathIterator getPathIterator(AffineTransform at, float pixelsq) {
		return new MultiPolygonApproxIterator(at, pixelsq);
	}
	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new MultiPolygonApproxIterator(at, (float) (flatness * flatness));
	}

	private void mergeWays(List<? extends List<? extends Point2D>> list) {
		for (List<? extends Point2D> way : list) {
			if (way != null) {
				ArrayList<Point2D> before = (ArrayList<Point2D>) ways.remove(way.get(0));
				ArrayList<Point2D> after = (ArrayList<Point2D>) ways.remove(way.get(way.size()-1));
				ArrayList<Point2D> merged = new ArrayList<>();
				if (before != null) {
					List<Point2D> reversedBefore = (List<Point2D>) before.clone();
					Collections.reverse(reversedBefore);
					if (reversedBefore.equals(after)) before = null;
				}
				if (before != null) {
					Collections.reverse(before);
					merged.addAll(before);
				}
				merged.addAll(way);
				if (after != null) {
					merged.addAll(after);
				}
				ways.put(merged.get(0), merged);
				List<Point2D> reversedMerged = (List<Point2D>)merged.clone();
				Collections.reverse(reversedMerged);
				ways.put(reversedMerged.get(0), reversedMerged);
			}
		}
	}
	
	class MultiPolygonApproxIterator extends PolygonApproxIterator {
		public MultiPolygonApproxIterator(AffineTransform _at, float _pixelsq) {
			super(_at, _pixelsq);
		}

		public void next() {
			float fx = coords[index];
			float fy = coords[index+1];
			index += 2;
			while (index < coords.length - 2 && pointtypes[(index >> 1) + 1] == PathIterator.SEG_LINETO &&
				distSq(fx, fy, coords[index], coords[index+1]) < approx) index += 2;
		}

		public int currentSegment(float[] c) {
			if (isDone()) {
	            throw new NoSuchElementException("poly approx iterator out of bounds");
	        }
	        c[0] = coords[index];
	        c[1] = coords[index+1];
	        if (at != null) {
	            at.transform(c, 0, c, 0, 1);
	        }
	        return pointtypes[index >> 1];
		}

		public int currentSegment(double[] coords) {
			throw new UnsupportedOperationException("Unexpected call to PolygonApprox.contains(Rectangle2D)");
		}
	}
}
