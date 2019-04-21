package Model.MapDrawStyle;

import Model.Model;
import Model.OSM.WayType;

import java.awt.*;
import java.io.Serializable;

/**
 * Contains all information needed to draw an element on the canvas
 */
public class DrawStyle implements Serializable {
    private boolean isDualColor;
    private boolean isArea;
    private WayType type;
    private ColorHex bodyHex;
    private ColorHex borderHex;
    private LineType lineType;
    private int lineWeight;
    private int zoomLevel;
    private MapIcon icon;

    /**
     * Constructs a draw style for a single element
     * @param type The type of the element
     * @param bodyHex The main color of the element
     * @param borderHex  The secondary/outline color of the element
     * @param zoomLevel The zoomlevel at which or lower than which the element should be drawn
     * @param lineType The linetype to be used for drawing
     * @param isArea If the element is an area or not
     * @param isDualColor If both bodyHex and borderHex should be used or just bodyHex
     * @param lineWeight The weight of the line, higher number = heavier line
     * @param icon The icon id for the icon to be used
     */
    public DrawStyle(WayType type, ColorHex bodyHex, ColorHex borderHex, int zoomLevel, LineType lineType, boolean isArea, boolean isDualColor, int lineWeight, MapIcon icon) {
        this.type = type;
        this.bodyHex = bodyHex;
        this.borderHex = borderHex;
        this.lineType = lineType;
        this.isArea = isArea;
        this.isDualColor = isDualColor;
        this.lineWeight = lineWeight;
        this.zoomLevel = zoomLevel;
        this.icon = icon;
    }

    /**
     * Sets the different styles from the class to the graphic object given in the methods parameter
     * @param g the graphics object
     * @return a Graphics2D object with the styles set
     */
    public Graphics2D setGraphicToStyle(Graphics2D g) {
        g.setColor(bodyHex.getColor());
        g.setStroke(lineType.strokeStyle.createBasicStroke(lineWeight));
        return g;
    }

    /**
     * Sets the different styles from the class to the graphic object given in the methods parameter considering the zoomlevel
     * @param g the graphics object
     * @param zoomLevel the zoomlevel to be considered
     * @return  a Graphics2D object with the styles set
     */
    public Graphics2D setGraphicToStyle(Graphics2D g, int zoomLevel) {
        g.setColor(bodyHex.getColor());
        g.setStroke(lineType.strokeStyle.createBasicStroke(lineWeight, zoomLevel));
        return g;
    }

    public boolean isDualColor() {
        return isDualColor;
    }

    public boolean isArea() {
        return isArea;
    }

    public WayType getType() {
        return type;
    }

    public ColorHex getBodyHex() {
        return bodyHex;
    }

    public ColorHex getBorderHex() {
        return borderHex;
    }

    public LineType getLineType() {
        return lineType;
    }

    public int getLineWeight() {
        return lineWeight;
    }

    public int getZoomLevel(){return zoomLevel;}

    public MapIcon getIcon() {return icon;}
}
