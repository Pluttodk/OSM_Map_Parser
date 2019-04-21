package Model;

import java.awt.geom.Point2D;

/**
 * This class represents a mapMarker and the current setting when the view needs to be moved towards the point
 */
public class OnePointMapMarker extends Point2D.Float {
    private boolean needsToMove;
    public OnePointMapMarker(float x, float y) {
        super(x, y);
    }

    public double getZoomFactor() {
        return 20000;
    }

    public boolean isNeedsToMove() {
        return needsToMove;
    }

    public void needsToMove() {
        needsToMove = true;
    }

    public void hasMoved(){
        needsToMove = false;
    }
}
