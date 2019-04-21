package Model;

import Model.MapDrawStyle.ColorHex;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Represents (user defined) a point of interest in the program
 */
public class PointOfInterest implements Serializable {
    private Point2D coord;
    private String name;
    private ColorHex color;

    public PointOfInterest(Point2D coord, String name, ColorHex color) {
        this.coord = coord;
        this.name = name;
        this.color = color;
    }

    public Point2D getCoord() {
        return coord;
    }

    public String getName() {
        return name;
    }

    public ColorHex getColorHex() {
        return color;
    }

    public void setName(String s) {
        name = s;
    }

    public void setColor(ColorHex color) {
        this.color = color;
    }
}