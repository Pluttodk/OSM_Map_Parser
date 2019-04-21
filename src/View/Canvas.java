package View;

import Controller.DrawStyleController;
import Model.*;
import Model.Graph.RoadGraph;
import Model.KDTree.KDFriendlyShape;
import Model.KDTree.KDSearchRect;
import Model.KDTree.KDTree;
import Model.KDTree.KDTreeNode;
import Model.MapDrawStyle.DrawStyle;
import Model.MapDrawStyle.LineType;
import Model.MapDrawStyle.MapIcon;
import Model.MapDrawStyle.StrokeStyle;
import Model.OSM.WayType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.*;

public class Canvas extends JComponent implements Observer {
    private static final Point2D MAP_MARKER_STANDARD_DIMENSIONS = new Point2D.Double(60, 90);
    private Model model;
    private DrawStyleController dsc;
    private Graphics2D g;
    private boolean antiAlias;
    private EnumMap<WayType, List<KDFriendlyShape>> wayTypeToShapeMap;
    private KDTreeNode kdRoot;
    private HashMap<KDFriendlyShape, Integer> shapeCounter;
    private static final float ICON_ZOOM_FACTOR = 0.00001f;
    private double startZoomLevel,currentZoomLevel;
    private BufferedImage bufferedImage;
    private Point2D botLeft,topRight;
    private KDSearchRect searchRect;
    private Rectangle2D waterBounds;
    private double minimumZoomLevel = 200000.0;

    public Canvas(Model model) {
        this.model = model;
        model.addObserver(this);
        kdRoot = model.getKdTree();
        dsc = model.getDrawStyleController();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bufferedImage.createGraphics();
        paintToImage(g2d);
        graphics.drawImage(bufferedImage,getX(), getY(), this);
    }

    /**
     * This method paints to a bufferedImage by using the bufferedImages Graphics2D
     * @param g2d is the graphic2D object retrieved from the bufferedImagte that is being painted upon.
     */
    private void paintToImage(Graphics2D g2d) {
        shapeCounter = new HashMap<>();
        StrokeStyle defaultStrokeStyle = LineType.DEFAULT.strokeStyle;
        g = g2d;
        g.setTransform(model.transform);
        g.setStroke(defaultStrokeStyle.createBasicStroke(1));
        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintWater();
        paintCoastLines();
        KDRangeQuery(); //Searches KDTree for shapes via a range query based on the size of the canvas

        drawWayTypes();
        drawPath();

        drawIcons();
        drawPOI();

        drawMarkers();

        drawNewMapLoaded();

    }

    /**
     * Draw icons from drawstyle
     * @param drawStyle Drawstyle to be drawn from
     * @param type Waytype that has the icon
     */
    private void drawIcons(DrawStyle drawStyle, WayType type) {
        ImageIcon icon;
        try {
            icon = new AutoFileReader.ImageReader(drawStyle.getIcon().getPath()).getImageIcon();

            for (Shape shape : wayTypeToShapeMap.get(type)) {
                drawIcon(icon, shape);
            }
        } catch (IOException e) {
            //Do nothing
        }
    }


    /**
     * Draw icon
     * @param icon The icon to be drawn
     * @param shape The shape related to the icon
     */
    private  void drawIcon(ImageIcon icon, Shape shape) {
        if (icon != null) {
            AffineTransform imageTransform = new AffineTransform();
            imageTransform.translate(shape.getBounds2D().getCenterX()-icon.getIconWidth()*ICON_ZOOM_FACTOR/2, shape.getBounds2D().getCenterY()-icon.getIconHeight()*ICON_ZOOM_FACTOR);
            imageTransform.scale(ICON_ZOOM_FACTOR, ICON_ZOOM_FACTOR);
            g.drawImage(icon.getImage(), imageTransform, this);
        }
    }


    /**
     * Searching in kdTree with bottom left and top right coordinates and
     * sets enum map for the current shapes
     */
    private void KDRangeQuery() {
        botLeft = new Point.Double(getBounds().getMinX(),getBounds().getMinY());
        topRight = new Point.Double(getBounds().getMaxX(),getBounds().getMaxY());

        botLeft = toModelCoords(botLeft);
        topRight = toModelCoords(topRight);

        searchRect = new KDSearchRect(botLeft, topRight);
        wayTypeToShapeMap =  KDTree.searchKDTree(searchRect, kdRoot);
    }

    /**
     * Paints a Rectrangle2D based on the canvas size as water to fill the ocean
     */
    private void paintWater() {
        botLeft = new Point.Double(getBounds().getMinX(),getBounds().getMinY());
        topRight = new Point.Double(getBounds().getMaxX(),getBounds().getMaxY());

        botLeft = toModelCoords(botLeft);
        topRight = toModelCoords(topRight);

        waterBounds = new Rectangle2D.Double(botLeft.getX(), botLeft.getY(),
                topRight.getX()-botLeft.getX(), topRight.getY()-botLeft.getY());
        g = dsc.setGraphicBasedOnDrawStyle(g,dsc.getDrawStyle(WayType.WATER));
        g.fill(waterBounds);
    }

    /**
     * Draw mapMarker from point
     * @param pos The given position in Point2D
     * @param dim Dimensions of the mapMarker
     * @param c Color of the mapMarker
     */
    private void drawMapMarker(Point2D pos, Point2D dim, Color c) {
        g.setStroke(new BasicStroke(Float.MIN_VALUE));
        g.setColor(c);

        Point2D markerPos = new Point2D.Double(pos.getX(), pos.getY());
        Point2D markerUpLeftBound = toModelCoords(new Point2D.Double(0, 0));
        Point2D markerDownRightBound = toModelCoords(new Point2D.Double(dim.getX(), dim.getY()));
        Point2D markerDim = (new Point2D.Double(markerDownRightBound.getX()-markerUpLeftBound.getX(), markerDownRightBound.getY()-markerUpLeftBound.getY()));
        MapMarker mm = new MapMarker(markerPos, markerDim);

        g.fill(mm);
        g.setColor(Color.BLACK);
        g.draw(mm);
    }

    /**
     * Paint all coastlines from model
      */
    private void paintCoastLines() {
        DrawStyle drawStyle = dsc.getDrawStyle(WayType.NATURAL_COASTLINE);
        g = dsc.setGraphicBasedOnDrawStyle(g, drawStyle);
        for (KDFriendlyShape shape : model.getCoastLines()) {
            g.fill(shape);
        }
    }

    /**
     * Draw shapes from the given drawstyle and waytype
     * @param drawStyle Drawstyle to draw from
     * @param type Waytype to be drawn
     */
    private void drawFromDrawStyle(DrawStyle drawStyle, WayType type) {
        if (type.toString().startsWith("HIGHWAY_") || type.toString().startsWith("RAILWAY_")) {
            g = dsc.setGraphicForHighways(g, drawStyle, getZoomLevel());
        }
        else {
            g = dsc.setGraphicBasedOnDrawStyle(g, drawStyle);
        }
        if (getZoomLevel() <= dsc.getZoomLevel(drawStyle)){
            if (dsc.checkIfArea(drawStyle)) {
                fill(type);
            }
            else if (!dsc.checkIfArea(drawStyle)) {
                draw(type);
            }
        }

    }


    /**
     * Pan to a given position
     * @param dx x-coordinates
     * @param dy y-coordinates
     */
    public void pan(double dx, double dy) {
        model.transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        kdRoot = model.getKdTree();
        repaint();
    }

    /**
     * Zoom with a given factor, and setting minimum and maximum zoom level
     * @param factor Factor to zoom with
     */
    public void zoom(double factor) {
        currentZoomLevel = Math.sqrt(model.transform.getDeterminant());

        repaint();

        if (startZoomLevel == 0){//Draw until zoomlevel is no longer 0
            model.transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            repaint();
        }else if (startZoomLevel <= currentZoomLevel && currentZoomLevel <= minimumZoomLevel){
            //Make it possible to zoom if within minimum and startzoomlevel
            model.transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            repaint();
        }


        if (factor > 1 && startZoomLevel > currentZoomLevel){//Make it possible to zoom when reache minimum
            model.transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            repaint();
        }

        if (factor < 1 && currentZoomLevel > minimumZoomLevel){
            //Make it possible to zoom when reached maximum
            factor = Math.abs(factor);
            model.transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            repaint();
        }

    }

    /**
     * Move view to given marker
     * @param currentMapMarker Marker to move to
     * @param pointZoomFactor new absolute zoom factor
     */
    private void moveViewTo(Point2D currentMapMarker, double pointZoomFactor) {
        double zoomScaleX = model.transform.getScaleX();
        double zoomFactor = pointZoomFactor/zoomScaleX;
        zoom(zoomFactor);
        Point2D centerPoint = new Point2D.Double(getBounds().getCenterX(), getBounds().getCenterY());
        centerPoint = toModelCoords(centerPoint);
        double dx = centerPoint.getX() - currentMapMarker.getX();
        double dy = centerPoint.getY() - currentMapMarker.getY();
        model.transform.translate(dx, dy);
        model.needsToMove = false;
    }


    private int getZoomLevel(){
        return dsc.getCurrentZoomLevel(g.getTransform());
    }

    /**
     * Converts screen coordinates to model coordinates
     * @param screenCoords Screen coordinates
     * @return Converted coordinates
     */
    private Point2D toModelCoords(Point2D screenCoords) {
        try {
            return model.transform.inverseTransform(screenCoords, null);
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts model coordinates to screen coordinates
     * @param modelCoords Model coordinates
     * @return Converted coordinates
     */
    private Point2D toScreenCoords(Point2D modelCoords) {
        return model.transform.transform(modelCoords, null);
    }

    /**
     * Toggle Antialiasing for the wayTypeToShapeMap
     */
    void toggleAA() {
        antiAlias = !antiAlias;
        repaint();
    }

    /**
     * This method will draw every shape for the given WayType in the current EnumMap wayTypeToShapeMap
     * @param type Waytype to draw
     */
    private void draw(WayType type) {
        for (KDFriendlyShape shape : wayTypeToShapeMap.get(type)) {
            shapeCounter.putIfAbsent(shape, 0);
            int shapeDrawnCount = shapeCounter.get(shape);
            if (shapeDrawnCount < 1) {
                g.draw(shape);
                shapeCounter.put(shape, shapeDrawnCount + 1);
            }
        }
    }

    /**
     * This method will fill every shape for the given WayType in the current EnumMap wayTypeToShapeMap
     * @param type Waytype to fill
     */
    private void fill(WayType type) {
        for (KDFriendlyShape shape : wayTypeToShapeMap.get(type)) {
            shapeCounter.putIfAbsent(shape, 0);
            int shapeDrawnCount = shapeCounter.get(shape);
            if (shapeDrawnCount < 1) {
                g.fill(shape);
                shapeCounter.put(shape, shapeDrawnCount + 1);
            }
        }
    }

    /**
     * Draw all available waytypes
     */
    private void drawWayTypes(){
        for (WayType type : WayType.values()) {
            DrawStyle drawStyle = dsc.getDrawStyle(type);
            if (drawStyle != null) {
                drawFromDrawStyle(drawStyle, type);
            }
        }
    }

    /**
     * Draws found path between destination and departure markers
     */
    private void drawPath(){
        if (model.getGraph().getPath() != null) {
            Point2D lastPoint;
            Path2D path = new Path2D.Double();
            for (RoadGraph.Edge e : model.getGraph().getPath()) {
                lastPoint = null;
                for (Point2D p : e.getWayPoints()) {
                    if (lastPoint != null) path.lineTo(p.getX(), p.getY());
                    else path.moveTo(p.getX(), p.getY());
                    lastPoint = p;
                }
            }
            g.setColor(Color.BLUE);
            g.setStroke(new StrokeStyle(BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, null).createBasicStroke(120,getZoomLevel()));
            g.draw(path);
        }
    }

    /**
     * Draws icons to all available waytypes
     */
    private void drawIcons(){
        for (WayType type : WayType.values()) {
            DrawStyle drawStyle = model.getCurrentMapDrawStyle().getDrawStyles().get(type);
            if (drawStyle != null && drawStyle.getIcon() != MapIcon.NONE && drawStyle.getZoomLevel() >= getZoomLevel()) {
                drawIcons(drawStyle, type);
            }
        }
    }

    /**
     * Draws user specified points of interest
     */
    private void drawPOI(){
        g.setColor(Color.BLACK);
        Font f  =  new Font("Arial", Font.BOLD, 1);
        final float RELATIVE_FONT_SIZE = 1/(float)model.transform.getScaleX()*25;
        f = f.deriveFont(RELATIVE_FONT_SIZE);
        g.setFont(f);
        FontRenderContext frc =  new FontRenderContext(model.transform, true, true);
        for (PointOfInterest poi : model.getPointsOfInterest()) {
            drawMapMarker(poi.getCoord(), MAP_MARKER_STANDARD_DIMENSIONS, poi.getColorHex().getColor());
            Point2D screenCoords = toScreenCoords(poi.getCoord());
            double stringWidth = f.getStringBounds(poi.getName(), frc).getWidth();
            Point2D modelCoords = toModelCoords(new Point2D.Double(screenCoords.getX(), screenCoords.getY() - 100));
            g.drawString(poi.getName(), (float) (modelCoords.getX() - stringWidth / 2), (float) modelCoords.getY());
        }

    }

    /**
     * Draws current mapmarker, destination mapmarker and departure mapmarker
     */
    private void drawMarkers(){
        if (model.getCurrentMapMarker() != null) {
            OnePointMapMarker cmm = model.getCurrentMapMarker();
            drawMapMarker(cmm, new Point2D.Double(60, 90), Color.GREEN);
            if (cmm.isNeedsToMove()){
                moveViewTo(cmm,cmm.getZoomFactor());
                model.currentMapMarkerHasMoved();
            }
        }
        if (model.getDepartureMapMarker() != null) {
            drawMapMarker(model.getDepartureMapMarker(), MAP_MARKER_STANDARD_DIMENSIONS, Color.BLUE);
        }
        if (model.getDestinationMarker() != null) {
            drawMapMarker(model.getDestinationMarker(), MAP_MARKER_STANDARD_DIMENSIONS, Color.RED);
            if (model.getDepToDest().isNeedsToMove()) {
                RouteMapMarkers depToDest = model.getDepToDest();
                moveViewTo(depToDest.getCenterPoint(),depToDest.getZoomFactor());
                model.depToDesthasMoved();

            }
        }
    }

    /**
     * Zooms and pans when new map loaded
     */
    private void drawNewMapLoaded(){
        if (model.newMapLoaded) {
            moveViewTo(model.getMapCenterPoint(), model.getMapZoomFactor());
            startZoomLevel =  Math.sqrt(model.transform.getDeterminant());
            model.newMapLoaded = false;
        }
    }
}
