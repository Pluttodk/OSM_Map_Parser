package View.MenuElements;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * A class that wraps JButton
 */
public class MenuPanelButton extends JButton{
    private String text;

    /**
     * The MenuPanelButton Constructor takes a string text as input
     * It calls the super() to extend JButton
     * @param text The text to be shown in the button
     */
    public MenuPanelButton(@NotNull String text) {
        super();
        if(text == null) throw new IllegalArgumentException("Arguments cannot be null");
        this.text = text;
    }

    /**
     * This method is used to paint the MenuPanelButton object. Should not be called manually
     * @param g The graphics object to be drawn on
     */
    public void paint(Graphics g) {
        g.setColor(new Color(115, 115, 115));
        g.fillRect(0,0,240,50);
        g.setColor(new Color (255, 255, 255));
        Font f = new Font("Verdana", Font.PLAIN, 16);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        g.drawString(text, 120 - fm.stringWidth(text)/2,25-20/2+fm.getAscent());

    }
}
