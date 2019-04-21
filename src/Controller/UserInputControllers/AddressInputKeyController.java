package Controller.UserInputControllers;

import Model.*;
import Model.Address.AddressParser;
import View.MenuElements.AddressInputBox;
import lib.TST;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A constructor for the AddressInputBox
 */
public class AddressInputKeyController extends KeyAdapter {
    private final Model model;
    private final AddressInputBox inputBox;
    private final static int BACKSPACE = 8;
    private final static int TAB = 9;
    private final static int ENTER = 13;
    private final static int SPACE = 32;
    private final static int DELETE = 127;

    /**
     * A constructor that makes a constructor for the addressinput
     * @param model
     * @param inputBox
     */
    public AddressInputKeyController(Model model, AddressInputBox inputBox) {
        this.model = model;
        this.inputBox = inputBox;
    }

    /**
     * A method that binds a action based on which key is released
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if ((keyChar >= 'a' && keyChar <='z')||(keyChar >= 'A' && keyChar <= 'Z')||keyChar == 'æ' || keyChar == 'ø' || keyChar == 'å' || keyChar == 'Æ' || keyChar == 'Ø' || keyChar == 'Å' ||(keyChar >= '0' && keyChar <= '9')  || keyChar == SPACE) {
            String input = inputBox.getText();
            String stringToBeExamined = input.substring(0, inputBox.getCaretPosition());
            String[] words = stringToBeExamined.split(" ");
            if (input.endsWith(" ")) words[words.length-1]+=" ";

            TST<String> data;

            if(AddressParser.matchesZipCodeRegex(input)) {
                data = model.getPostcodes();
            } else if(AddressParser.matchesCityRegex(input)) {
                data = model.getCities();
            } else {
                data = model.getStreetNames();
            }

            final AutoCompleter addressAutoCompleter = new AutoCompleter(data);
            AutoCompleter.SuggestedWord[] suggestedWords = addressAutoCompleter.getSuggestedWords(words, 4);

            inputBox.hidePopup();
            inputBox.removeAllItems(); //In case no suggested words found
            if (suggestedWords.length > 0) {
                inputBox.setItems(suggestedWords);
                inputBox.showPopup();
            }
        } else if(keyChar == TAB ||keyChar == ENTER) {
            inputBox.moveCaretToEndOfSelection();
            inputBox.hidePopup();
        } else if(keyChar == DELETE ||keyChar == BACKSPACE) {
            inputBox.removeAllItems();
            inputBox.hidePopup();
        }
    }
}
