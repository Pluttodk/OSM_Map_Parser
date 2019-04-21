package View.MenuElements;

import javax.swing.*;
import java.awt.*;
/**
 * A class that extends JTextField for address inputs
 */
public class MenuPanelTextField extends JTextField implements AutoCompleterInputBox {
    private String defaultMessage;

    /**
     * Creates new instance of the MenuPanelTextField
     * @param defaultMessage The default defaultMessage to be shown in the textfield
     */
    public MenuPanelTextField(String defaultMessage) {
        super(defaultMessage);
        this.defaultMessage = defaultMessage;
        setFont(new Font("Verdana", Font.ITALIC, 16));
        setPreferredSize(new Dimension(100,100));
        setEditable(true);
    }

    /**
     * Gets the default message from the textfield
     * @return The message as a string
     */
    public String getDefaultMessage(){
        return defaultMessage;
    }

    /**
     * Insert a specific string after the caret
     * @param s the string to be inserted
     */
    public void insertAfterCaret(String s) {
        int oldCaretPos = getCaretPosition();
        setText(getText().substring(0, oldCaretPos) + s + getText().substring(oldCaretPos));
        setCaretPosition(oldCaretPos);
    }

    /**
     * Selects n characters after the caret
     * @param columns number of columns
     */
    public void selectNColumnsAfterCaret(int columns) {
        select(getCaretPosition(), getCaretPosition() + columns);
    }

    /**
     * Moves the caret to the end of the text selection
     */
    public void moveCaretToEndOfSelection() {
        try {
            setCaretPosition(getSelectionEnd());
        } catch (NullPointerException e) {
            //Do nothing
        }
    }

    /**
     * Move caret to start of selection
     */
    public void moveCaretToStartOfSelection() {
        try {
            setCaretPosition(getSelectionStart());
        } catch (NullPointerException e) {
            //Do nothing
        }
    }
}
