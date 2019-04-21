package Model.KDTree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class contains two corner points for a Rectangle used as a range query when searching through the KDTree
 */
public class KDSearchRect {
    private Point2D botLeft, topRight;

    public KDSearchRect(Point2D botLeft, Point2D topRight) {
        this.botLeft = botLeft;
        this.topRight = topRight;
    }

    public KDSearchRect(Rectangle2D rectangle) {
        botLeft = new Point2D.Double(rectangle.getMinX(), rectangle.getMinY());
        topRight = new Point2D.Double(rectangle.getMaxX(), rectangle.getMaxY());
    }

    public Point2D getBotLeft() {
        return botLeft;
    }

    public Point2D getTopRight() {
        return topRight;
    }
}
