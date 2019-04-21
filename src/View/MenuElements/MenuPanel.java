package View.MenuElements;

import Controller.ActionListeners.MenuActionListener;
import Controller.UserInputControllers.AddressInputFocusListener;
import Model.MapDrawStyle.MapDrawStyle;
import Model.Model;
import Model.Graph.TravelType;
import View.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

/**
 * A class that extends JPanel
 */
public class MenuPanel extends JPanel {
    private final String HEADER_IMAGE_PATH = "resources/header.png";
    private final String SEARCH_IMAGE_PATH = "resources/search.png";
    private JLabel addressWarningLabel;
    private final String ROUTE_IMAGE_PATH = "resources/route.png";
    private final String PRINT_IMAGE_PATH = "resources/print.png";
    private Model model;
    private Window window;
    private AddressInputBox searchInput, departureInput, destinationInput;
    private ImageButton searchButton;
    private ImageButton routeButton;
    private MenuPanelComboBox travelMethod, drawStyleDropdown;
    private JScrollPane scrollPane;
    private JLabel tripDetail;
    private double zoom;
    private DirectionsPanel directionsPanel;

    /**
     * The menu panel constructor takes a string picture as input to use as the header image
     */
    public MenuPanel(Model model, Window window) {
        this.model = model;
        this.window = window;

        setBackground(new Color(211, 211, 211));
        final JComponent headerImage = new ImageButton(HEADER_IMAGE_PATH);
        headerImage.setPreferredSize(new Dimension(216, 50));
        add(headerImage);

        initializePanelItems();
        addressWarningLabel.setPreferredSize(new Dimension(200,30));
        addressWarningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addressWarningLabel.setForeground(Color.RED);

        destinationInput.setVisible(false);
        departureInput.setVisible(false);
        travelMethod.setVisible(false);
        addressWarningLabel.setVisible(false);

        addActionListeners();

        addAddressInput(searchInput);
        addAddressInput(departureInput);
        addAddressInput(destinationInput);
        add(addressWarningLabel);
        addComboBox(travelMethod);
        addImageButton(searchButton);
        addImageButton(routeButton);
        addComboBox(drawStyleDropdown);
        addDirectionsPanel(new DirectionsPanel());

    }

    /**
     * Adds the various actionslisteners to the menupanels items
     */
    private void addActionListeners() {
        searchButton.addActionListener(new MenuActionListener.SearchButtonActionListener(model, this));
        routeButton.addActionListener(new MenuActionListener.RouteButtonActionListener(window));
        drawStyleDropdown.addActionListener(new MenuActionListener.DrawStyleDropdownActionListener(model, this));
    }

    /**
     * Initialize the menupanelitems
     */
    private void initializePanelItems() {
        tripDetail = new JLabel("");
        searchInput = new AddressInputBox(Model.getString("search"), model);
        searchButton = new ImageButton(SEARCH_IMAGE_PATH);
        routeButton = new ImageButton(ROUTE_IMAGE_PATH);
        routeButton.setIsCheckbox(true);
        departureInput = new AddressInputBox(Model.getString("from"), model);
        destinationInput = new AddressInputBox(Model.getString("to"), model);
        travelMethod = new MenuPanelComboBox(TravelType.class.getEnumConstants());
        drawStyleDropdown = new MenuPanelComboBox();
        addressWarningLabel = new JLabel(Model.getString("address-not-valid"));
        for (MapDrawStyle mds : model.getMapDrawStyles()) {
            drawStyleDropdown.addItem(mds);
        }
    }

    /**
     * Updates text on runtime, so that the language can be changed
     */
    public void updateText(){
        departureInput.setDefaultMessage(Model.getString("from"));
        destinationInput.setDefaultMessage(Model.getString("to"));
        searchInput.setDefaultMessage(Model.getString("search"));
        departureInput.setText(Model.getString("from"));
        destinationInput.setText(Model.getString("to"));
        searchInput.setText(Model.getString("search"));
        addressWarningLabel.setText(Model.getString("address-not-valid"));
        updateDirectionsPanel();
        tripDetail.setText(Model.getString("need-to-drive") + new DecimalFormat("##.##").format(directionsPanel.getRouteLength()) + Model.getString("dist-and-take") + directionsPanel.getRouteTime());
    }

    private void updateDirectionsPanel() {
        DirectionsPanel.Directions.updateDirections();
        removeDirectionsPanel();
        addDirectionsPanel(directionsPanel);
    }

    /**
     * Covvert directionspanel to scrollpanel and then adds the scrollpanel to menupanel
     * @param directionsPanel to be converted to scrollpanel
     */
    public void addDirectionsPanel(DirectionsPanel directionsPanel) {
        this.directionsPanel = directionsPanel;
        directionsPanel.setPreferredSize(new Dimension(directionsPanel.getMaxTextWidth()+20, directionsPanel.getMaxTextHeight()));
        scrollPane = new JScrollPane(directionsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(200, 350));
        add(scrollPane);
        tripDetail.setText(Model.getString("need-to-drive") + new DecimalFormat("##.##").format(directionsPanel.getRouteLength()) + Model.getString("dist-and-take") + directionsPanel.getRouteTime());
        add(tripDetail);
    }

    public void removeDirectionsPanel() {remove(scrollPane);}


    /**
     * Adds an image button to our menupanel
     * @param button ImageButton to be added
     */
    private void addImageButton(ImageButton button) {
        button.setPreferredSize(new Dimension(64,64));
        add(button);
    }

    /**
     * Adds dropdown menu to our menupanel
     * @param comboBox Combobox to be added
     */
    private void addComboBox(MenuPanelComboBox comboBox) {
        comboBox.setPreferredSize(new Dimension(200,30));
        add(comboBox);
    }

    public AddressInputBox getSearchInput() {
        return searchInput;
    }

    public AddressInputBox getDepartureInput() {
        return departureInput;
    }

    public AddressInputBox getDestinationInput() {
        return destinationInput;
    }

    public MenuPanelComboBox getTravelMethod() {
        return travelMethod;
    }

    public ImageButton getRouteButton() {
        return routeButton;
    }

    public MenuPanelComboBox getDrawStyleDropdown() {
        return drawStyleDropdown;
    }

    public DirectionsPanel getDirectionsPanel(){return directionsPanel;}

    public void toggleRouteMode() {
        setRouteMode(!routeButton.isChecked());
    }

    /**
     * Change which textfields and inputboxes that should be shown
     * @param b if true then then it will show it for route
     */
    public void setRouteMode(boolean b) {
        routeButton.setChecked(b);
        departureInput.setVisible(b);
        destinationInput.setVisible(b);
        searchInput.setVisible(!b);
        travelMethod.setVisible(b);
        if(b) {
            if(!searchInput.getText().equals(searchInput.getDefaultMessage())) {
                departureInput.setText(searchInput.getText());
            }
        } else {
            if(!departureInput.getText().equals(departureInput.getDefaultMessage()))  {
                searchInput.setText(departureInput.getText());
            }
        }
    }

    /**
     * Adds a addressinput to the menupanel
     * @param inputBox to be added
     */
    public void addAddressInput(AddressInputBox inputBox) {
        inputBox.setPreferredSize(new Dimension(200,30));
        add(inputBox);
        inputBox.getTextField().addFocusListener(new AddressInputFocusListener(inputBox));
    }

    public JLabel getAddressWarningLabel() {
        return addressWarningLabel;
    }
}
