package Controller;

import Model.Exceptions.IllegalLanguageException;
import Model.Strings.StringHandler;
import View.Popups.StartPopup;
import Model.Model;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.IOException;


public class Main {
    private static final String STRING_DIRECTORY = "resources/languages";

    /**
     * En main metode som bruges til at teste
     * @param args
     */
    public static void main(String[] args) throws IOException, SAXException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ignored) {
            //Do nothing
        }
        try {
            Model.setLanguageList(StringHandler.getLanguageList(STRING_DIRECTORY));
        } catch (SAXException | IOException | IllegalLanguageException ignore) {
            //Do nothing
        }

        StartPopup sp = new StartPopup();
    }

    public static void terminate() {
        System.exit(0);
    }
}
