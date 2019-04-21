package Controller.ActionListeners;

import Model.*;
import Model.Address.Address;
import Model.Address.AddressParser;
import Model.Exceptions.RouteNotPossibleException;
import Model.Graph.TravelType;
import Model.KDTree.KDFriendlyShape;
import Model.KDTree.KDSearchRect;
import Model.KDTree.KDTree;
import Model.MapDrawStyle.MapDrawStyle;
import View.MenuElements.MenuPanel;
import View.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * Menupanels action listeners
 */

public class MenuActionListener {
    /**
     * Action listener for the search button
     */
    public static class SearchButtonActionListener implements ActionListener {
        private MenuPanel menupanel;
        private Model model;


        /**
         * Constructor for a new actionlistener to searchbutton
         * @param model Model
         * @param menupanel Menupanel
         */
        public SearchButtonActionListener(Model model, MenuPanel menupanel) {
            this.menupanel = menupanel;
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!menupanel.getRouteButton().isChecked()) {
                    String AddressString = menupanel.getSearchInput().getText().toLowerCase();
                    Address address = AddressParser.parse(AddressString);
                    OnePointMapMarker coords = model.getAddressCoords(address);
                    coords.needsToMove();
                    model.setCurrentMapMarker(coords);
                    model.clearRouteMapMarkers();
                    model.getGraph().clearPath();
                } else {
                    Point2D departureCoords;
                    String departureAddressString = menupanel.getDepartureInput().getText().toLowerCase();
                    if (departureAddressString.equals("mapmarker:departure") && model.getDepartureMapMarker() != null) {
                        departureCoords = model.getDepartureMapMarker();
                    } else {
                        Address departureAddress = AddressParser.parse(departureAddressString);
                        departureCoords = model.getAddressCoords(departureAddress);
                        model.setDepartureMapMarker(departureCoords);
                    }

                    Point2D destinationCoords;
                    String destinationAddressString = menupanel.getDestinationInput().getText().toLowerCase();
                    if (destinationAddressString.equals("mapmarker:destination") && model.getDestinationMarker() != null) {
                        destinationCoords = model.getDestinationMarker();
                    } else {
                        Address destinationAddress = AddressParser.parse(destinationAddressString);
                        destinationCoords = model.getAddressCoords(destinationAddress);
                        model.setDestinationMarker(destinationCoords);
                    }

                    Point2D point;
                    KDFriendlyShape way;
                    int from = -1;
                    int to = -1;
                    TravelType travelType = (TravelType)menupanel.getTravelMethod().getSelectedItem();
                    try {
                        point = KDTree.getNearestWayAsPoint(departureCoords, model.getKdTree(), travelType);
                        way = KDTree.getNearestWay(departureCoords, new KDSearchRect(departureCoords, departureCoords), model.getKdTree(), travelType);
                        from = model.getGraph().addVertex(point, way);
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(new JFrame(), Model.getString("exception-not-found-point-departure"), Model.getString("error"), JOptionPane.WARNING_MESSAGE);
                    }

                    try {
                        point = KDTree.getNearestWayAsPoint(destinationCoords, model.getKdTree(), travelType);
                        way = KDTree.getNearestWay(destinationCoords, new KDSearchRect(destinationCoords, destinationCoords), model.getKdTree(), travelType);
                        to = model.getGraph().addVertex(point, way);
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(new JFrame(), Model.getString("exception-not-found-point-destination"), Model.getString("error"), JOptionPane.WARNING_MESSAGE);
                    }
                    if (to > 0 && from > 0) {
                        model.getGraph().findPath(from, to, travelType);
                        menupanel.removeDirectionsPanel();
                        menupanel.addDirectionsPanel(model.getGraph().getDirectionsPanel());
                        model.setCurrentMapMarker(null);
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), Model.getString("exception-no-route-calc"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }

                }
                menupanel.getAddressWarningLabel().setVisible(false);
            } catch(IllegalArgumentException | NullPointerException ex) {
                menupanel.getAddressWarningLabel().setVisible(true);
            } catch (RouteNotPossibleException ex) {
                JOptionPane.showMessageDialog(new JFrame(), Model.getString("exception-no-route"), Model.getString("error"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * ActionListener for finding route button
     */
    public static class RouteButtonActionListener implements ActionListener {
        private Window window;

        /**
         * Custom Action Listener for rout button
         * @param window window
         */
        public RouteButtonActionListener(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.toggleRouteMode();
            window.setMinimumSize(window.getSize());
            window.pack();
            window.setMinimumSize(null);
        }
    }

    /**
     * ActionListener for plus button
     */
    public static class PlusButtonActionListener implements ActionListener {
        private Window window;

        public PlusButtonActionListener(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.zoom(-2);
        }
    }

    /**
     * ActionListener for minus button
     */
    public static class MinusButtonActionListener implements ActionListener {
        private Window window;

        public MinusButtonActionListener(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.zoom(2);
        }
    }

    /**
     * ActionListener for drawstyledropdown
     */
    public static class DrawStyleDropdownActionListener implements ActionListener {
        private Model model;
        private MenuPanel menupanel;

        public DrawStyleDropdownActionListener(Model model, MenuPanel menupanel) {
            this.model = model;
            this.menupanel = menupanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setCurrentMapDrawStyle((MapDrawStyle) menupanel.getDrawStyleDropdown().getSelectedItem());
        }
    }
}
