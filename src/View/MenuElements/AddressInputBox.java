package View.MenuElements;

import Controller.AddressInputEditor;
import Controller.UserInputControllers.AddressInputKeyController;
import Model.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

/**
 * A combobox especially suited for inputting addresses
 */
public class AddressInputBox extends JComboBox implements AutoCompleterInputBox {
    private String defaultMessage;

    /**
     * Initialising the inputbox
     * @param defaultMessage The default message to be displayed when there's no input
     * @param model model for being passed to another class
     */
    public AddressInputBox(String defaultMessage, Model model) {
        this.defaultMessage = defaultMessage;
        setFont(new Font("Verdana", Font.ITALIC, 16));
        setPreferredSize(new Dimension(100,100));
        setEditable(true);
        setEditor(new AddressInputEditor());
        setText(defaultMessage);
        getTextField().setFocusTraversalKeysEnabled(false);
        getTextField().addKeyListener(new AddressInputKeyController(model, this));

        //Removing the arrow button
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
    }

    /**
     * Method for updating/replacing the items of the dropdown
     * @param objects The objects to be shown on the dropdown
     */
    public void setItems(Object[] objects) {
        removeAllItems();
        for (Object o : objects) {
            addItem(o);
        }
    }

    public int getCaretPosition() {
        return getTextField().getCaretPosition();
    }

    public void setText(String s) {
        getTextField().setText(s);
    }

    public String getText() {
        return getTextField().getText();
    }

    public JTextField getTextField() {
        return (JTextField) getEditor().getEditorComponent();
    }

    /**
     * Inserts string after the caret
     * @param s the string to be inserted
     */
    public void insertAfterCaret(String s) {
        int oldCaretPos = getTextField().getCaretPosition();
        getTextField().setText(getTextField().getText().substring(0, oldCaretPos) + s + getTextField().getText().substring(oldCaretPos));
        getTextField().setCaretPosition(oldCaretPos);
    }

    /**
     * Selects n characters after the caret
     * @param columns number of columns
     */
    public void selectNColumnsAfterCaret(int columns) {
        getTextField().select(getTextField().getCaretPosition(), getTextField().getCaretPosition() + columns);
    }

    /**
     * Moves the caret to the end of the text selection
     */
    public void moveCaretToEndOfSelection() {
        try {
            getTextField().setCaretPosition(getTextField().getSelectionEnd());
        } catch (NullPointerException e) {
            //Do nothing
        }
    }

    /**
     * Moves the caret to the start of the text selection
     */
    public void moveCaretToStartOfSelection() {
        try {
            getTextField().setCaretPosition(getTextField().getSelectionStart());
        } catch (NullPointerException e) {
            //Do nothing
        }
    }

    public String getDefaultMessage(){
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
