package Controller.ActionListeners;

import Controller.Main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Marek Kisiel on 15/05/2017.
 */
public class WindowCloseActionListener extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        Main.terminate();
    }
}
