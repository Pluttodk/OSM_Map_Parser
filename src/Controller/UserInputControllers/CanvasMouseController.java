package Controller.UserInputControllers;

import Model.*;
import Model.KDTree.KDFriendlyShape;
import Model.KDTree.KDTree;
import View.*;
import View.Canvas;
import View.ContextMenus.CanvasContextMenu;
import View.ContextMenus.MapMarkerContextMenu;
import View.MenuElements.MenuPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * A controller for the mouse used on the canvas
 */
public class CanvasMouseController extends MouseAdapter {
    private static final int RIGHT_CLICK = 3;
    private Model model;
    private Canvas canvas;
    private Point2D lastMousePosition;
    private MenuPanel panel;

    /**
     * A controller that connects canvas to a mouse controller
     * @param canvas
     * @param model
     * @param panel
     */
    public CanvasMouseController(Canvas canvas, Model model, MenuPanel panel) {
        this.model = model;
        this.canvas = canvas;
        this.panel = panel;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition = e.getPoint();
        List<PointOfInterest> pointsOfInterest = model.getPointsOfInterest();
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            PointOfInterest poi = pointsOfInterest.get(i);
            MapMarker mm = new MapMarker(model.transform.transform(poi.getCoord(), null), new Point2D.Double(60, 90));
            if (mm.contains(lastMousePosition) && e.getButton() == RIGHT_CLICK) {
                MapMarkerContextMenu mmcm = new MapMarkerContextMenu(model, panel, i);
                mmcm.show(e.getComponent(), e.getX(), e.getY());
                return;
            }
        }

        for (Point2D point : new Point2D[]{model.getCurrentMapMarker(), model.getDestinationMarker(), model.getDepartureMapMarker()}) {
            if (point != null) {
                Point2D transformedPoint = model.transform.transform(point, null);
                MapMarker mm = new MapMarker(transformedPoint, new Point2D.Double(60, 90));
                if (mm.contains(lastMousePosition) && e.getButton() == RIGHT_CLICK) {
                    MapMarkerContextMenu mmcm = new MapMarkerContextMenu(model, transformedPoint);
                    mmcm.show(e.getComponent(), e.getX(), e.getY());
                    return;
                }
            }
        }
        if(e.getButton() == RIGHT_CLICK) {
            CanvasContextMenu ccm = new CanvasContextMenu(model, panel, lastMousePosition);
            ccm.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor = Math.pow(0.9, e.getWheelRotation());
        Point2D currentMousePosition = e.getPoint();
        double dx = currentMousePosition.getX();
        double dy = currentMousePosition.getY();
        canvas.pan(-dx, -dy);
        canvas.zoom(factor);
        canvas.pan(dx, dy);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D currentMousePosition = e.getPoint();
        double dx = currentMousePosition.getX() - lastMousePosition.getX();
        double dy = currentMousePosition.getY() - lastMousePosition.getY();
        canvas.pan(dx, dy);
        lastMousePosition = currentMousePosition;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        Point2D currentMousePosition = e.getPoint();
        currentMousePosition = model.toModelCoords(currentMousePosition);
        KDFriendlyShape nearestWay = KDTree.getNearestWayWithName(currentMousePosition, model.getKdTree());
        if (nearestWay != null) {
            canvas.setToolTipText(nearestWay.getName());
        }
    }
}
