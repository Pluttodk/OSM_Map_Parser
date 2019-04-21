package View;

import Controller.ActionListeners.MenuBarActionListener;
import Controller.PrintController;
import Model.Graph.TravelType;
import Model.Model;
import Model.Strings.Language;
import View.MenuElements.DirectionsPanel;
import View.MenuElements.MenuPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by mrkis on 26-04-2017.
 */
public class MenuBar extends JMenuBar{

    private Model model;
    private MenuPanel panel;
    private Window window;

    private JMenu menu;
    private JMenuItem printItem, saveItem, openItem;

    private KeyStroke ctrlO, ctrlS, ctrlP, ctrlPlus, ctrlMinus;

    private JMenu editMenu, langMenu;
    private JMenuItem editZoomOut, editZoomIn;

    /**
     * Creates menubar
     * @param model model to use
     * @param panel panel to use
     * @param window window to use
     */
    public MenuBar(Model model, MenuPanel panel, Window window) {
        this.model = model;
        this.panel = panel;
        this.window = window;

        initializeShortcuts();
        initializeFileMenu();
        initializeEditMenu();

        add(menu);


        add(editMenu);

        langMenu = new JMenu(Model.getString("language"));

        for(Language lang : model.getLanguageList()){
            JMenuItem langItem = new JMenuItem(lang.getName());
            langItem.addActionListener(new MenuBarActionListener.UpdateLanguageActionListener(panel,model,lang,this));
            langMenu.add(langItem);
        }

        add(langMenu);
    }

    /**
     * Updates labels and button text, so language can be changed on runtime
     */
    public void updateText(){
        //File Menu update
        menu.setText(Model.getString("file"));
        printItem.setText(Model.getString("print"));
        saveItem.setText(Model.getString("save"));
        openItem.setText(Model.getString("load"));

        //Edit Menu update
        editMenu.setText(Model.getString("edit"));
        editZoomIn.setText(Model.getString("zoom-in"));
        editZoomOut.setText(Model.getString("zoom-out"));

        //Language
        langMenu.setText(Model.getString("language"));

    }

    /**
     * Initializing shortcuts
     */
    private void initializeShortcuts() {
        ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        ctrlPlus = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        ctrlMinus = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

    }

    /**
     * Initializing File Menu
     */
    private void initializeFileMenu(){
        menu = new JMenu(Model.getString("file"));
        printItem = new JMenuItem(Model.getString("print"));
        printItem.setAccelerator(ctrlP);
        printItem.addActionListener(new MenuBarActionListener.PrintItemActionListener(panel));

        openItem = new JMenuItem("Open");
        openItem.setAccelerator(ctrlO);
        openItem.addActionListener(new MenuBarActionListener.OpenItemActionListener(model));

        saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(ctrlS);
        saveItem.addActionListener(new MenuBarActionListener.SaveItemActionListener(model));

        menu.add(printItem);
        menu.add(openItem);
        menu.add(saveItem);
    }

    /**
     * Initializing the Edit Menu
     */
    private void initializeEditMenu(){
        editMenu = new JMenu(Model.getString("edit"));
        editZoomOut = new JMenuItem(Model.getString("zoom-out"));
        editZoomIn = new JMenuItem(Model.getString("zoom-in"));

        editZoomIn.setAccelerator(ctrlPlus);
        editZoomIn.addActionListener(new MenuBarActionListener.ZoomInItemActionListener(window));

        editZoomOut.setAccelerator(ctrlMinus);
        editZoomOut.addActionListener(new MenuBarActionListener.ZoomOutItemActionListener(window));

        editMenu.add(editZoomIn);
        editMenu.add(editZoomOut);
    }

}
