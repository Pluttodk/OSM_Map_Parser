package View.Popups;

import Controller.ActionListeners.StartPopupActionListener;
import Controller.ActionListeners.WindowCloseActionListener;
import Model.Model;
import View.MenuElements.ImageButton;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Matt on 08-05-2017.
 */
public class StartPopup extends JFrame {
    private final String LOGO_PATH = "resources/header.png";

    /**
     * Popup to be used when selecting map on program start up
     */
    public StartPopup() {
        final JComponent headerImage = new ImageButton(LOGO_PATH);
        JButton loadNewMapButton = new JButton(Model.getString("read-map"));
        JButton loadPredefinedMapButton = new JButton(Model.getString("read-dk"));

        headerImage.setPreferredSize(new Dimension(216, 50));
        loadNewMapButton.setPreferredSize(new Dimension(100,50));
        loadPredefinedMapButton.setPreferredSize(new Dimension(100,50));

        loadNewMapButton.addActionListener(new StartPopupActionListener.LoadNewMapButtonActionListener(this));
        loadPredefinedMapButton.addActionListener(new StartPopupActionListener.LoadPredefinedMapButtonActionListener(this));

        add(headerImage, BorderLayout.NORTH);
        add(loadNewMapButton, BorderLayout.CENTER);
        add(loadPredefinedMapButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowCloseActionListener());
        setResizable(false);
        pack();
        centerWindow();
        setVisible(true);
    }


    /**
     * Centers window
     */
    private void centerWindow() {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2;
        setLocation(x,y);
    }


}
