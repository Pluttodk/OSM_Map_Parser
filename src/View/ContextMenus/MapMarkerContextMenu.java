package View.ContextMenus;

import Controller.ActionListeners.CanvasContextMenuActionListener;

import javax.swing.*;
import View.MenuElements.MenuPanel;
import Model.Model;
import java.awt.geom.Point2D;


public class MapMarkerContextMenu extends JPopupMenu {
    /**
     * Context for menu for custom mapmarker
     * @param model Model
     * @param panel Panel
     * @param mapMarkerIndex mapMarker index
     */
    public MapMarkerContextMenu(Model model, MenuPanel panel, int mapMarkerIndex) {
        JMenuItem changeNameItem = new JMenuItem(Model.getString("change-poi-name"));
        add(changeNameItem);
        changeNameItem.addActionListener(new CanvasContextMenuActionListener.ChangeMapMarkerNameActionListener(model, mapMarkerIndex));

        JMenuItem changeColorItem = new JMenuItem(Model.getString("change-poi-color"));
        add(changeColorItem);
        changeColorItem.addActionListener(new CanvasContextMenuActionListener.ChangeMapMarkerColorActionListener(model, mapMarkerIndex));

        JMenuItem deleteItem = new JMenuItem(Model.getString("delete-poi"));
        add(deleteItem);
        deleteItem.addActionListener(new CanvasContextMenuActionListener.DeleteMapMarkerActionListener(model, mapMarkerIndex));

        JMenuItem setDepartureItem = new JMenuItem(Model.getString("route-from-poi"));
        add(setDepartureItem);
        setDepartureItem.addActionListener(new CanvasContextMenuActionListener.SetDepartureActionListener(model, panel, model.getPointsOfInterest().get(mapMarkerIndex).getCoord(), true));

        JMenuItem setDestinationItem = new JMenuItem(Model.getString("route-to-poi"));
        add(setDestinationItem);
        setDestinationItem.addActionListener(new CanvasContextMenuActionListener.SetDestinationActionListener(model, panel, model.getPointsOfInterest().get(mapMarkerIndex).getCoord(), true));

    }

    /**
     * Context menu to destion/arrival marker to create a custom marker
     * @param model
     * @param point
     */
    public MapMarkerContextMenu(Model model, Point2D point) {
        JMenuItem saveAsPOIItem = new JMenuItem(Model.getString("save-as-poi"));
        add(saveAsPOIItem);
        saveAsPOIItem.addActionListener(new CanvasContextMenuActionListener.AddMapMarkerActionListener(model, point));
    }

}
