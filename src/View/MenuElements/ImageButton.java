package View.MenuElements;

import Model.AutoFileReader;
import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * An button that holds an image
 */
public class ImageButton extends JButton {
    private ImageIcon image;
    private boolean isCheckbox, checked;

    /**
     * Creates a new instance of the ImageButton from a relative path to the image
     * @param path The relative path to the image
     */
    public ImageButton(@NotNull String path) {
        if(path == null) throw new IllegalArgumentException("Arguments cannot be null");
        try {
            image = new AutoFileReader.ImageReader(path, true).getImageIcon();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to draw the View.MenuElements.ImageButton object. Should not be called manually.
     * @param g The graphics object to be drawn on
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        if(isCheckbox && checked && image.getIconHeight() < 100) {
            g.setColor(new Color(154, 154, 154));
            g.fillRect(0, 0, 70, 70);
        } else if (image.getIconHeight() < 100){
            g.setColor(new Color(211, 211, 211));
            g.fillRect(0,0, image.getIconWidth(), image.getIconHeight());
        }
        g.drawImage(image.getImage(), 0, 0, this);

    }

    /**
     * Sets whether the component should appear as checked
     * @param b Should it be checked? true = checked, false = unchecked
     */
    public void setIsCheckbox(boolean b) {
        isCheckbox = b;
        repaint();
    }

    /**
     * Inverts whether the component is checked or not.
     */
    public void setChecked(boolean b) {
        checked = b;
        repaint();
    }

    public boolean isChecked() {
        return checked;
    }

}
