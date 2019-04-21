package View.MenuElements;

import Model.Graph.TravelType;

import javax.swing.*;

/**
 * A class that wraps JComboBox
 */
 public class MenuPanelComboBox extends JComboBox{
    /**
     * Creates new instance of the MenuPanelComboBox
     * @param travelTypes An array of TravelTypes that appears in the comboBox
     */
    MenuPanelComboBox(TravelType[] travelTypes) {
        for(TravelType travelType: travelTypes) {
            addItem(travelType);
        }
    }

    /**
     * Creates new instance of the MenuPanelComboBox without any preset strings
     */
    MenuPanelComboBox() {
        setEditable(false);
    }
}
