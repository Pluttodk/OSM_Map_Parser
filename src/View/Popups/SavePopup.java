package View.Popups;

import Model.Model;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Matt on 11-05-2017.
 */
public class SavePopup extends IOPopup {
    /**
     * Constructing save popup
     */
    public SavePopup() {
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        JLabel saveLabel = new JLabel(Model.getString("wait-whilst-saving"), SwingConstants.CENTER);
        saveLabel.setPreferredSize(new Dimension(300,100));
        panel.add(saveLabel, BorderLayout.SOUTH);
        publish();
    }
}
