package Controller.ActionListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.*;

import Model.*;
import Model.MapDrawStyle.ColorHex;
import View.MenuElements.MenuPanel;

import javax.swing.*;

/**
 * Canvas context menu action listeners
 */
public class CanvasContextMenuActionListener {
    /**
     * ActionListener for adding a map marker
     */
   public static class AddMapMarkerActionListener implements ActionListener {
        private Model model;
        private Point2D coord;

        /**
         * Constructor for custom listener
         * @param model Model
         * @param coord Coordinates for the marker
         */
        public AddMapMarkerActionListener(Model model, Point2D coord) {
            this.model = model;
            this.coord = coord;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Point2D modelCoords = model.transform.inverseTransform(coord, null);
                Object nameDialog = JOptionPane.showInputDialog(new JFrame(), Model.getString("select-name-for-poi"), "Point of Interest", JOptionPane.INFORMATION_MESSAGE, null, null, "Ny point of interest");
                if (nameDialog != null) {
                    JColorChooser jcc = new JColorChooser();
                    JDialog colorDialog = JColorChooser.createDialog(new JFrame(), Model.getString("select-color-for-poi"), false, jcc, (ActionEvent ae) -> {
                        ColorHex color = new ColorHex(String.format("#%02x%02x%02x", jcc.getColor().getRed(), jcc.getColor().getGreen(), jcc.getColor().getBlue()));
                        model.addPointOfInterest(new PointOfInterest(modelCoords, (String) nameDialog, color));
                    }, null);
                    colorDialog.setVisible(true);
                }
            } catch (NoninvertibleTransformException ex) {
                //Do nothing
            }
        }
    }

    /**
     * ActionListener for changing map markers name
     */
    public static class ChangeMapMarkerNameActionListener implements ActionListener {
        private Model model;
        private int mapMarkerIndex;

        /**
         * Constructor for custom action listener
         * @param model Model
         * @param mapMarkerIndex index of the mapmarker that needs to have the name changed
         */
        public ChangeMapMarkerNameActionListener(Model model, int mapMarkerIndex) {
            this.model = model;
            this.mapMarkerIndex = mapMarkerIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<PointOfInterest> pois = model.getPointsOfInterest();
            Object nameDialog = JOptionPane.showInputDialog(new JFrame(), Model.getString("select-name-for-poi"), "Point of Interest", JOptionPane.INFORMATION_MESSAGE, null, null, pois.get(mapMarkerIndex).getName());
            if (nameDialog != null) {
                model.setPointOfInterestName(mapMarkerIndex, (String) nameDialog);
            }
        }
    }

    /**
     * ActionListener for changing map markers color
     */
    public static class ChangeMapMarkerColorActionListener implements ActionListener {
        private Model model;
        private int mapMarkerIndex;

        /**
         * Constructor for custom action listener to change the color of a marker
         * @param model Model
         * @param mapMarkerIndex index of the marker to be changed
         */
        public ChangeMapMarkerColorActionListener(Model model, int mapMarkerIndex) {
            this.model = model;
            this.mapMarkerIndex = mapMarkerIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<PointOfInterest> pois = model.getPointsOfInterest();
            JColorChooser jcc = new JColorChooser(pois.get(mapMarkerIndex).getColorHex().getColor());
            JDialog colorDialog = JColorChooser.createDialog(new JFrame(), "Vælg farve til point of interest", false, jcc, (ActionEvent ae) -> {
                ColorHex color = new ColorHex(String.format("#%02x%02x%02x", jcc.getColor().getRed(), jcc.getColor().getGreen(), jcc.getColor().getBlue()));
                model.setPointOfInterestColor(mapMarkerIndex, color);
            }, null);
            colorDialog.setVisible(true);

        }
    }

    /**
     * ActionListener for deleting a mapmarker
     * */
    public static class DeleteMapMarkerActionListener implements ActionListener {
        private Model model;
        private int mapMarkerIndex;

        /**
         * Custom action listener constructor for deleting chosen mapmarker
         * @param model Model
         * @param mapMarkerIndex index of the marker that needs to be removed
         */
        public DeleteMapMarkerActionListener(Model model, int mapMarkerIndex) {
            this.model = model;
            this.mapMarkerIndex = mapMarkerIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.deletePointOfInterest(mapMarkerIndex);
        }
    }

    /**
     * ActionListener to set departure marker
     */
    public static class SetDepartureActionListener implements ActionListener {
        private final Model model;
        private MenuPanel panel;
        private final Point2D coords;
        private boolean isModelCoords;

        /**
         * Adds a custom action listener for setting a new departure marker
         * @param model Model
         * @param panel Panel
         * @param coords coordinates for the marker
         * @param isModelCoords coordinates to model or to screen
         */
        public SetDepartureActionListener(Model model, MenuPanel panel, Point2D coords, boolean isModelCoords) {
            this.model = model;
            this.panel = panel;
            this.coords = coords;
            this.isModelCoords = isModelCoords;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            panel.setRouteMode(true);
            panel.getDepartureInput().setText("MapMarker:Departure");
            model.setDepartureMapMarker(isModelCoords? coords :model.toModelCoords(coords));
            model.getGraph().clearPath();
        }
    }

    /**
     * ActionListener for setting destination marker
     */
    public static class SetDestinationActionListener implements ActionListener {
        private final Model model;
        private MenuPanel panel;
        private final Point2D coord;
        private boolean isModelCoords;

        /**
         * Setting action listener for adding a new destination map marker
         * @param model Model
         * @param panel Panel
         * @param coord coordinates for the marker
         * @param isModelCoords coordinates to model or to screen
         */
        public SetDestinationActionListener(Model model, MenuPanel panel, Point2D coord, boolean isModelCoords) {
            this.model = model;
            this.panel = panel;
            this.coord = coord;
            this.isModelCoords = isModelCoords;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            panel.setRouteMode(true);
            panel.getDestinationInput().setText("MapMarker:Destination");
            model.setDestinationMarker(isModelCoords?coord:model.toModelCoords(coord));
            model.getGraph().clearPath();
        }
    }
}
