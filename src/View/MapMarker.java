package View;

import java.awt.geom.*;

/**
 * MapMarker t show points on map
 */
public class MapMarker extends Area {
    private final double x;
    private final double y;
    private final double height;
    private final double width;

    /**
     * Create a mapmarker
     * @param point Where to place it on the map
     * @param dim The dimensions of the mapmarker
     */
    public MapMarker(Point2D point, Point2D dim) {
        x = point.getX();
        y = point.getY();
        height = dim.getY();
        width = dim.getX();

        Path2D triangle = new Path2D.Double();
        double triangleWidth = 2*(width /2)*Math.sin(Math.toRadians(120/2)), triangleHeight = height /2;
        Double triangleXCoord[] = { x-triangleWidth/2, x+triangleWidth/2, x}, triangleYCoord[] = { y-triangleHeight, y-triangleHeight, y };
        triangle.moveTo(triangleXCoord[0], triangleYCoord[0]);
        for(int i = 1; i < triangleXCoord.length; ++i) {
            triangle.lineTo(triangleXCoord[i], triangleYCoord[i]);
        }
        triangle.closePath();

        Path2D rectangleToHalfCircle = new Path2D.Double();
        rectangleToHalfCircle.append(new Rectangle2D.Double(x- width /2, y- height /2, width, height /2),false);

        Path2D circle = new Path2D.Double();
        circle.append(new Ellipse2D.Double(x- width /2, y- height, width, height *0.6666666), false);

        Path2D pinHole = new Path2D.Double();
        pinHole.append(new Ellipse2D.Double(x- width /4, y- height *0.85, width /2, height *0.3333333), false);

        this.add(new Area(circle));
        this.subtract(new Area(rectangleToHalfCircle));
        this.add(new Area(triangle));
        this.subtract(new Area(pinHole));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}