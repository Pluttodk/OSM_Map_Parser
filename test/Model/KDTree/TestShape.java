package Model.KDTree;

import Model.OSM.SimpleOSMNode;
import Model.OSM.WayType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TestShape extends SimpleOSMNode implements KDFriendlyShape {
    WayType type;

    public TestShape(double x, double y) {
        super(x, y);
    }

    @Override
    public double getCenter(boolean isX) {
        if (isX) return getX();
        return getY();
    }

    @Override
    public Point2D getCenterPoint() {
        return this;
    }

    @Override
    public double distTo(Point2D point) {
        return this.distance(point);
    }

    @Override
    public void setWayType(WayType type) {
        this.type = type;
    }

    @Override
    public WayType getWayType() {
        return type;
    }

    @Override
    public Point2D nearestPoint(Point2D p) {
        return this;
    }

    @Override
    public List<SimpleOSMNode> getPoints() {
        List<SimpleOSMNode> points = new ArrayList<>();
        points.add(this);
        return points;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(getX(), getY(), 0, 0);
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    @Override
    public boolean contains(Point2D p) {
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return false;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return null;
    }
}
