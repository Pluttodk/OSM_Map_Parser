package Controller.UserInputControllers;

import Model.*;
import View.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A controller for the keys
 */
public class WindowKeyController extends KeyAdapter{
    Model model;
    Window window;

    /**
     * A controller that adds a controller to a window
     * @param window
     * @param model
     */
    public WindowKeyController(Window window, Model model) {
        window.addKeyListener(this);
        this.window = window;
        this.model = model;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'x':
                window.toggleAA();
                break;
            case 's':
                model.save("savegame.bin");
                break;
            case 'l':
                model.load("savegame.bin", false);
                break;
        }
    }
}
