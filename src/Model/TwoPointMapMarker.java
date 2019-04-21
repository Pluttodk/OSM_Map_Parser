package Model;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class TwoPointMapMarker implements Serializable {
    private Point2D departurePoint, destinationPoint;
    private boolean needsToMove;

    public TwoPointMapMarker() {
    }

    public Point2D getCenterPoint() {
        double x = ((departurePoint.getX()-destinationPoint.getX())/2) + destinationPoint.getX();
        double y = ((departurePoint.getY()-destinationPoint.getY())/2) + destinationPoint.getY();
        return new Point2D.Double(x,y);
    }

    public double getZoomFactor() {
        return 100/(Math.abs(departurePoint.distance(destinationPoint)))*6;
    }

    public boolean isNeedsToMove() {
        return needsToMove;
    }

    public void needsToMove() {
        needsToMove = true;
    }

    public void hasMoved() {
        needsToMove = false;
    }

    public void setDeparturePoint(Point2D departurePoint) {
        this.departurePoint = departurePoint;
    }

    public void setDestinationPoint(Point2D destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Point2D getDeparturePoint() {
        return departurePoint;
    }

    public Point2D getDestinationPoint() {
        return destinationPoint;
    }
}