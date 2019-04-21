package Model.KDTree;

import Model.OSM.SimpleOSMNode;
import Model.OSM.WayType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Interface for a shape that supports implementations used in the KDTree
 */
public interface KDFriendlyShape extends Shape {
    double getCenter(boolean isX);

    Point2D getCenterPoint();

    double distTo(Point2D point);

    void setWayType(WayType type);

    WayType getWayType();

    Point2D nearestPoint(Point2D p);

    List<SimpleOSMNode> getPoints();

    String getName();
}
