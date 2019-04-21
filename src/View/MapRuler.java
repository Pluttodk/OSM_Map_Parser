package View;

import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Mapruler to represent distances on map
 */
public class MapRuler extends JPanel {
    private Model model;
    private Window window;
    private static int LENGTH = 50;
    private static final int HEIGHT = 50;
    private static final int HORIZONTAL_PADDING = 5;

    /**
     * Constructing mapruler
     * @param model Model to represent
     * @param window Window to draw on
     */
    public MapRuler(Model model, Window window) {
        this.model = model;
        this.window = window;
        setPreferredSize(new Dimension(LENGTH+HORIZONTAL_PADDING, HEIGHT));
        setLocation(0, window.getHeight());
    }

    @Override
    public void paint(Graphics g_) {
        Graphics2D g = (Graphics2D) g_;
        Point2D leftModelCoords;
        Point2D rightModelCoords;
        LENGTH = 50;
        try {
            leftModelCoords = model.transform.inverseTransform(new Point2D.Float(0, 0), null);
            rightModelCoords = model.transform.inverseTransform(new Point2D.Float(LENGTH, 0), null);
            double dist = EuclideanDistance.dist(model, leftModelCoords, rightModelCoords);

            String s;
            if (dist <= 0.1) {
                dist = dist*1000;
                s = String.valueOf(roundToPowerOfTen(dist, 1)) + "m";
                LENGTH *= 1-(dist-roundToPowerOfTen(dist, 1))/dist;
            } else if (dist <= 1) {
                dist = dist*1000;
                s = String.valueOf(roundToPowerOfTen(dist, 2)) + "m";
                LENGTH *= 1-(dist-roundToPowerOfTen(dist, 2))/dist;
            } else if (dist <= 10) {
                s = String.valueOf(roundToPowerOfTen(dist, 0)) + "km";
                LENGTH *= 1-(dist-roundToPowerOfTen(dist, 0))/dist;
            } else {
                s = String.valueOf(roundToPowerOfTen(dist, 1)) + "km";
                LENGTH *= 1-(dist-roundToPowerOfTen(dist, 1))/dist;
            }

            g.setStroke(new BasicStroke(2));
            g.setColor(Color.BLACK);
            g.drawLine(HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+25, LENGTH+HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+25);
            g.drawLine(HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+20, HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+30);
            g.drawLine(LENGTH+HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+20, LENGTH+HORIZONTAL_PADDING, window.getHeight()-HEIGHT*2+30);

            FontMetrics fm =  g.getFontMetrics(g.getFont());
            g.drawString(s, ((LENGTH+HORIZONTAL_PADDING*2)/2) - fm.stringWidth(s)/2, window.getHeight()-HEIGHT*2+10);

        } catch (NoninvertibleTransformException e) {
            //Do nothing
        }
    }

    /**
     * Method to round a double to the closes power of ten
     * @param number the number to be rounded
     * @param power the power of ten which the number should be rounded to
     * @return the rounded number as an int
     */
    public int roundToPowerOfTen(double number, int power) {
        if (power < 0) throw new RuntimeException("Power cannot be less than 0");
        return (int)(Math.round(number) - Math.round(number)%Math.pow(10, power));
    }
}
