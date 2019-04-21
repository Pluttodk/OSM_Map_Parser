package View.ContextMenus;

import Controller.ActionListeners.CanvasContextMenuActionListener;

import javax.swing.*;
import Model.*;
import View.MenuElements.MenuPanel;

import java.awt.geom.Point2D;

/**
 * Context setter for Menu on canvas
 */
public class CanvasContextMenu extends JPopupMenu {
    /**
     * Sets context of the menu
     * @param model Model
     * @param panel Panel
     * @param screenCoords Coordinates on screen
     */
    public CanvasContextMenu(Model model, MenuPanel panel, Point2D screenCoords) {
        JMenuItem addMapMarkerItem = new JMenuItem(Model.getString("add-poi"));
        add(addMapMarkerItem);
        addMapMarkerItem.addActionListener(new CanvasContextMenuActionListener.AddMapMarkerActionListener(model, screenCoords));

        JMenuItem setDepartureItem = new JMenuItem(Model.getString("route-from-poi"));
        add(setDepartureItem);
        setDepartureItem.addActionListener(new CanvasContextMenuActionListener.SetDepartureActionListener(model, panel, screenCoords, false));

        JMenuItem setDestinationItem = new JMenuItem(Model.getString("route-to-poi"));
        add(setDestinationItem);
        setDestinationItem.addActionListener(new CanvasContextMenuActionListener.SetDestinationActionListener(model, panel, screenCoords, false));
    }
}
