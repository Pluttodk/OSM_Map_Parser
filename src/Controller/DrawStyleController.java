package Controller;

import Model.MapDrawStyle.DrawStyle;
import Model.MapDrawStyle.MapDrawStyle;
import Model.MapDrawStyle.MapDrawStyleList;
import Model.OSM.WayType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

/**
 * Controller for drawstyles
 */
public class DrawStyleController implements Serializable {

    private MapDrawStyle currentMapDrawStyle;
    private MapDrawStyleList mapDrawStyles;

    /**
     * Creates a new DrawStyleController
     * @param mapDrawStyles
     */
    public DrawStyleController(MapDrawStyleList mapDrawStyles) {
        this.mapDrawStyles = mapDrawStyles;
        currentMapDrawStyle = mapDrawStyles.get(0);
    }

    public MapDrawStyle getCurrentMapDrawStyle() {
        return currentMapDrawStyle;
    }

    public DrawStyle getDrawStyle(WayType type) {
        return currentMapDrawStyle.getDrawStyles().get(type);
    }

    public void setCurrentMapDrawStyle(MapDrawStyle currentMapDrawStyle) {
        this.currentMapDrawStyle = currentMapDrawStyle;
    }

    /**
     * This method call the drawStyle parameter to set a graphic object to the stroke and color given that drawStyle
     * @param g is the graphic object
     * @param drawStyle is the current drawStyle
     * @return a graphic2D object customized to the current drawStyle
     */
    public Graphics2D setGraphicBasedOnDrawStyle(Graphics2D g, DrawStyle drawStyle) {
        return drawStyle.setGraphicToStyle(g);
    }

    public Graphics2D setGraphicForHighways(Graphics2D g, DrawStyle drawStyle, int zoomLevel) {
        return drawStyle.setGraphicToStyle(g, zoomLevel);
    }

    public int getZoomLevel(DrawStyle drawStyle) {
        return drawStyle.getZoomLevel();
    }

    public boolean checkIfArea(DrawStyle drawStyle) {
        return drawStyle.isArea();
    }

    /**
     * This method calculates the current zoom level which decides which shapes are to be shown. The current zoom level
     * is based upon the AffineTransform
     * @param at is the current AffineTransform
     * @return it returns the current zoom level based upon the AffineTransform parameter
     */
    public int getCurrentZoomLevel(AffineTransform at) {
        double determinant = Math.sqrt(at.getDeterminant());
        int zoomlevel;
        if(determinant < 500.0){
            zoomlevel = 8;
        } else if (determinant > 500.0 && determinant < 800.0){
            zoomlevel = 7;
        } else if (determinant > 800.0 && determinant < 1500) {
            zoomlevel = 6;
        } else if (determinant > 1500 && determinant < 8000.0){
            zoomlevel = 5;
        } else if (determinant > 8000.0 && determinant < 14000.0){
            zoomlevel = 4;
        } else if (determinant > 14000.0 && determinant < 30000.0){
            zoomlevel = 3;
        } else if (determinant > 30000.0 && determinant < 81875.0){
            zoomlevel = 2;
        } else {
            zoomlevel = 1;
        }
        return zoomlevel;
    }
}
