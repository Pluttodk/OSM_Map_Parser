package Model.MapDrawStyle;

import java.awt.*;

public class StrokeStyle {
    private int capStyle;
    private int joinStyle;
    private float[] dash;

    /**
     * Constructor, which takes capStyle, joinStyle and dash
     * To be used to create a BasicStroke using createBasicStroke
     * @param capStyle
     * @param joinStyle
     * @param dash
     */
    public StrokeStyle(int capStyle, int joinStyle, float[] dash) {
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        this.dash = dash;
    }

    /**
     * Create a basicStroke based on width
     * @param width the given width
     * @return the basicStroke created
     */

    public BasicStroke createBasicStroke(int width) {
        float floatWidth = width / (float) 1000000;
        return new BasicStroke(floatWidth, capStyle, joinStyle, 10.0f, dash, 0.0f);
    }

    /**
     * Create a basicStroke based on width and zoomLevel
     * @param width
     * @param zoomLevel
     * @return
     */

    public BasicStroke createBasicStroke(int width, int zoomLevel) {
        float floatWidth = 0;

        switch (zoomLevel) {
            case 8:
                floatWidth = width / (float) 80000;
                break;
            case 7:
                floatWidth = width / (float) 200000;
                break;
            case 6:
                floatWidth = width / (float) 500000;
                break;
            case 5:
                floatWidth = width / (float) 1000000;
                break;
            case 4:
                floatWidth = width / (float) 1300000;
                break;
            case 3:
                floatWidth = width / (float) 1700000;
                break;
            case 2:
                floatWidth = width / (float) 2100000;
                break;
            case 1:
                floatWidth = width / (float) 2600000;
                break;
        }

        return new BasicStroke(floatWidth, capStyle, joinStyle, 10.0f, dash, 0.0f);
    }


}
