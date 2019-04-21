package Model.OSM;

import Model.KDTree.KDFriendlyShape;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * A class to represent a simple point on the map
 */
public class OSMNode extends Point2D.Double implements KDFriendlyShape, Serializable {
    private WayType wayType;
    private String name;

    /**
     * A consrtuctor to initialize its fields
     * @param coords the coords of the given point
     * @param wayType which wayType the node is
     * @param name the name of the road
     */
    public OSMNode(Point2D coords, WayType wayType, String name) {
        super(coords.getX(), coords.getY());
        this.wayType = wayType;
        this.name = name;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point((int) getX(), (int) getY()), new Dimension(0,0));
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(getX(), getY(), 0, 0);
    }

    @Override
    public boolean contains(double x, double y) {
        return getX() == x && getY() == y;
    }

    @Override
    public boolean contains(Point2D p) {
        return equals(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return contains(x, y);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return contains(r.getX(), r.getY());
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return contains(x, y);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new Path2D.Double().getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new Path2D.Double().getPathIterator(at, flatness);
    }

    @Override
    public double getCenter(boolean isX) {
        return isX ? getX() : getY();
    }

    @Override
    public Point2D getCenterPoint() {
        return this;
    }

    @Override
    public double distTo(Point2D point) {
        return Math.sqrt(Math.pow(point.getX()-getX(), point.getY()-getY()));
    }

    @Override
    public WayType getWayType() {
        return wayType;
    }

    @Override
    public Point2D nearestPoint(Point2D p) {
        return this;
    }

    @Override
    public List<SimpleOSMNode> getPoints() {
        List<SimpleOSMNode> points = new ArrayList<>();
        points.add(new SimpleOSMNode(getX(), getY()));
        return points;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setWayType(WayType type) {
        this.wayType = type;
    }
}