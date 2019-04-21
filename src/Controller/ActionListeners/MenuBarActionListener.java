package Controller.ActionListeners;

import Controller.PrintController;
import Model.Graph.TravelType;
import Model.Strings.Language;
import View.ContextMenus.MapMarkerContextMenu;
import View.MenuBar;
import View.MenuElements.DirectionsPanel;
import View.MenuElements.MenuPanel;
import Model.Model;
import View.Popups.SavePopup;
import View.Window;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Matt on 10-05-2017.
 */
public class MenuBarActionListener {
    public static class PrintItemActionListener implements ActionListener {
        private MenuPanel panel;

        public PrintItemActionListener(MenuPanel panel) {
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PrintController.printPanel(panel.getDirectionsPanel());
        }
    }

    public static class OpenItemActionListener implements ActionListener {
        private Model model;

        public OpenItemActionListener(Model model) {
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser load = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(Model.getString("supported-file-extensions"), "osm", "bin", "zip");
            load.setFileFilter(filter);
            if (load.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
                model.load(load.getSelectedFile().getAbsolutePath(), true);
            }
        }
    }

    public static class SaveItemActionListener implements ActionListener {
        private Model model;

        public SaveItemActionListener(Model model) {
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser save = new JFileChooser();
            if (save.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
                SavePopup sp = new SavePopup();
                String savedFile = save.getSelectedFile().getAbsolutePath();
                if(!savedFile.endsWith(".bin")) savedFile += ".bin";
                model.save(savedFile);
                sp.close();
            }
        }
    }

    public static class ZoomInItemActionListener implements ActionListener {
        private Window window;

        public ZoomInItemActionListener(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.zoom(-2);
        }
    }

    public static class ZoomOutItemActionListener implements ActionListener {
        private Window window;

        public ZoomOutItemActionListener(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.zoom(2);
        }
    }

    public static class UpdateLanguageActionListener implements ActionListener{
        private MenuPanel panel;
        private Model model;
        private Language lang;
        private MenuBar menuBar;

        public UpdateLanguageActionListener(MenuPanel panel, Model model, Language lang, MenuBar menuBar) {
            this.panel = panel;
            this.model = model;
            this.lang = lang;
            this.menuBar = menuBar;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Model.setLanguage(lang.getName());

            menuBar.updateText();
            panel.updateText();
            TravelType.updateName();



        }
    }
}
