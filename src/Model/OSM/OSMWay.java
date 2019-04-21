package Model.OSM;

import Model.KDTree.PolygonApprox;
import Model.Model;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * A class to represent a way
 */
public class OSMWay extends PolygonApprox {
    /**
     * A constructor to create a way containing a list of points2d
     * @param points the points that define a way
     */
    public OSMWay(List<? extends Point2D> points) {
        super(points);
    }
}
