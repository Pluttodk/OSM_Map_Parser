package View.MenuElements.ZoomPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Creates a special zoombutton
 */
public class ZoomButton extends JButton{
    private String symbol;

    /**
     * Creates a new button
     * @param symbol Symbol that is on the button
     */
    public ZoomButton(String symbol) {
        this.symbol = symbol;
    }

    public void paint(Graphics g) {
        g.setColor(new Color(115, 115, 115));
        g.fillRect(0,0,70,50);
        g.setColor(new Color (255, 255, 255));
        Font f = new Font("Verdana", Font.PLAIN, 40);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        g.drawString(symbol, 35 - fm.stringWidth(symbol)/2,fm.getAscent());
    }
}
