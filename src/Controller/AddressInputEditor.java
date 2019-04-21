package Controller;

import Model.AutoCompleter;
import View.MenuElements.MenuPanelTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * An editor to replace the standard combobox editor to add own functionality
 */
public class AddressInputEditor implements ComboBoxEditor{
    private MenuPanelTextField textField = new MenuPanelTextField("");
    private AutoCompleter.SuggestedWord currentSuggestion;

    @Override
    public Component getEditorComponent() {
        return textField;
    }

    /**
     * Inserts the suggested word into the editor
     * @param o the object (must be able to casted AutoCompleter.SuggestedWord)
     */
    @Override
    public void setItem(Object o) {
        AutoCompleter.SuggestedWord suggestedWord = (AutoCompleter.SuggestedWord) o;
        if (suggestedWord != null) {
            currentSuggestion = suggestedWord;
            textField.replaceSelection("");
            String completion = suggestedWord.getSuggestedCompletion();
            textField.insertAfterCaret(completion);
            textField.selectNColumnsAfterCaret(completion.length());
        } else {
            currentSuggestion = null;
        }
    }

    @Override
    public Object getItem() {
        return currentSuggestion;
    }

    @Override
    public void selectAll() {}

    @Override
    public void addActionListener(ActionListener l) {
        textField.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        textField.removeActionListener(l);
    }

}
