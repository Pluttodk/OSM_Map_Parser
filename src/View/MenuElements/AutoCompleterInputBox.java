package View.MenuElements;

/**
 * An interface to insure that the input has the necessary methods
 */
public interface AutoCompleterInputBox {
    void insertAfterCaret(String s);

    void selectNColumnsAfterCaret(int columns);

    void moveCaretToEndOfSelection();

    void moveCaretToStartOfSelection();

    String getDefaultMessage();

    String getText();

    void setText(String s);
}
