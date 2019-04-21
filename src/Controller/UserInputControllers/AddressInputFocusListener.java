package Controller.UserInputControllers;

import View.MenuElements.AddressInputBox;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A class that adds a focus listener to a addressInputBox
 */
public class AddressInputFocusListener implements FocusListener {
    private AddressInputBox inputBox;

    public AddressInputFocusListener(AddressInputBox inputBox) {
        this.inputBox = inputBox;
    }
    @Override
    public void focusGained(FocusEvent e) {
        Font prev = inputBox.getFont();
        Font plain = prev.deriveFont(Font.PLAIN);
        inputBox.setFont(plain);
        if (inputBox.getText().equals(inputBox.getDefaultMessage())) inputBox.setText("");
    }

    @Override
    public void focusLost(FocusEvent e) {
        Font prev = inputBox.getFont();
        Font italic = prev.deriveFont(Font.ITALIC);
        inputBox.setFont(italic);
        if (inputBox.getText().equals("")) inputBox.setText(inputBox.getDefaultMessage());
    }

}
