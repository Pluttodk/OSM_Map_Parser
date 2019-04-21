package View.MenuElements.ZoomPanel;

import View.MenuElements.MenuPanelButton;

import javax.swing.*;
import java.awt.*;

/**
 * Zoompanel
 */
public class ZoomPanel extends JPanel {
    private ZoomButton plus, minus;

    /**
     * Creates the zoompanel
     */
    public ZoomPanel() {
        setOpaque(false);
        plus = new ZoomButton("+");
        minus = new ZoomButton("-");
        addZoomButton(plus);
        addZoomButton(minus);
    }

    /**
     * Add zoombutton to zoompanel
     * @param button
     */
    private void addZoomButton(ZoomButton button){
        button.setPreferredSize(new Dimension(70,50));
        add(button, BorderLayout.EAST);
    }

    public ZoomButton getPlusButton() {return plus;}

    public ZoomButton getMinusButton() {return minus;}
}
