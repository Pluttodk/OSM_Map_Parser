package Model.OSM;

import java.awt.geom.Point2D;

/**
 * A class to represent a point
 * not a vizual representation
 */
public class SimpleOSMNode extends Point2D.Double {
    /**
     * A constructor to create an OSMNode
     * @param x the x location
     * @param y the y location
     */
    public SimpleOSMNode(double x, double y) {
        super(x, y);
    }
}
