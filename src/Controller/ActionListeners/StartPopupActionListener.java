package Controller.ActionListeners;

import Controller.UserInputControllers.WindowKeyController;
import Model.Model;
import View.Popups.StartPopup;
import View.Window;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StartPopupActionListener {
    /**
     * ActionListener for clicking on the button "Indlæs Danmark"
     */
    public static class LoadNewMapButtonActionListener implements ActionListener {
        private StartPopup startPopup;

        /**
         *  Constructing Actionlistener
         * @param startPopup The popup to be listened for
         */
        public LoadNewMapButtonActionListener(StartPopup startPopup) {
            this.startPopup = startPopup;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(Model.getString("supported-file-extensions"), "osm", "bin", "zip");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
                startPopup.dispose();
                initiateModel(fileChooser.getSelectedFile().getAbsolutePath(), true);
            }
        }
    }

    /**
     * ActionListener for clicking "indlæs Danmark"
     */
    public static class LoadPredefinedMapButtonActionListener implements ActionListener {
        private static final String PREDEFINED_MAP_PATH = "denmark.bin";
        private StartPopup startPopup;

        /**
         *  Constructing Actionlistener
         * @param startPopup The popup to be listened for
         */
        public LoadPredefinedMapButtonActionListener(StartPopup startPopup) {
            this.startPopup = startPopup;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            startPopup.dispose();
            initiateModel(PREDEFINED_MAP_PATH, false);
        }
    }

    /**
     * Initiate model with a map file
     * @param filePath Path to file
     * @param isAbsolute If the path is absolute
     */
    private static void initiateModel(String filePath, boolean isAbsolute) {
        try {
            Model model = new Model(filePath, isAbsolute);
            Window window = new Window(model);
            new WindowKeyController(window, model);

            window.pack();
            window.setVisible(true);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
