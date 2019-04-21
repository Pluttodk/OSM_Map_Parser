package Model;

import Controller.DrawStyleController;
import Controller.OSMHandler;
import Model.Address.Address;
import Model.Exceptions.FileExtensionNotSupportedException;
import Model.Graph.RoadGraph;
import Model.KDTree.KDFriendlyShape;
import Model.Strings.Language;
import Model.MapDrawStyle.ColorHex;
import Model.MapDrawStyle.DrawStyleParser;
import Model.MapDrawStyle.MapDrawStyle;
import Model.MapDrawStyle.MapDrawStyleList;
import Model.OSM.WayType;
import View.Popups.ProgressPopup;
import lib.TST;
import org.xml.sax.*;

import Model.KDTree.KDTreeNode;

import javax.swing.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.zip.ZipInputStream;

public class Model extends Observable implements Serializable {

    private static final String COASTLINES_FILE_PATH = "resources/coastlines.bin";
    public MapData mapData;
    private static List<Language> languageList;
    public AffineTransform transform = new AffineTransform();

    //DrawStyle related fields
    private MapDrawStyleList mapDrawStyles;
    private final String DRAW_STYLE_DIRECTORY = "resources/drawStyle";

    private DrawStyleController drawStyleController;
    private static Map<String, Language> languages;
    private static Language language;
    private CoastLines coastLines;
    private boolean isCoastlinesLoaded;

    private OnePointMapMarker currentMapMarker;
    private RouteMapMarkers depToDest = new RouteMapMarkers();
    private Point2D mapCenterPoint;
    private double mapZoomFactor;
    public boolean newMapLoaded;
    public boolean needsToMove = false;
    public float minlat, minlon, maxlat, maxlon;

    //Fields for loading UI
    private transient int total;
    private transient long startTime;
    private transient Timer progressPrinter;
    private transient ProgressPopup progressPopup;

    /**
     * Creates instance of Model, by loading default map
     */
    public Model() {
        this("map.osm", false);
    }

    /**
     * Calls itself to create an instance
     * @param filename
     */
    public Model(String filename) {
        this(filename,false);
    }

    /**
     * Creats instance of Model by loading a file
     * @param filename path to file to be opened
     * @param isAbsolute is path absolute or not
     */
    public Model(String filename, boolean isAbsolute) {
        try {
            loadCoastLines();
            isCoastlinesLoaded = true;
        } catch (IOException | ClassNotFoundException e) {
            isCoastlinesLoaded = false;
        }

        load(filename, isAbsolute);
        try {
            mapDrawStyles = new DrawStyleParser().autoloadAllStyles(DRAW_STYLE_DIRECTORY);
            drawStyleController = new DrawStyleController(mapDrawStyles);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            languages = new HashMap<>();
        }
        update();
    }

    private void loadCoastLines() throws IOException, ClassNotFoundException {
        AutoFileReader.ObjectReader<CoastLines> binReader;
        binReader = new AutoFileReader.ObjectReader<CoastLines>(COASTLINES_FILE_PATH, true);
        coastLines = binReader.getObject();
    }

    public void add(KDFriendlyShape shape) {
        if (shape.getWayType() != WayType.UNKNOWN) {
            if (shape.getWayType().toString().equals("NATURAL_COASTLINE")) {
               coastLines.add(shape);
            } else {
                mapData.addShapeToKDTree(shape);
            }
            update();
        }
    }

    /**
     * Notifies the observer that model has been changed
     */
    private void update() {
        setChanged();
        notifyObservers();
    }

    /**
     * Method that saves the contents of the mapData object to a file which is decided by the filename argument
     * @param filename
     */
    public void save(String filename) {
        ObjectOutputStream fileOutput;
        try {
            fileOutput = new ObjectOutputStream(new FileOutputStream(filename));
            fileOutput.writeObject(mapData);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), Model.getString("save-error"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method that starts the UI load label
     * @param is The inputstream to be shown progress of
     * @throws IOException If inputstream was not valid
     */
    public void initiateLoadBar(InputStream is) throws IOException {
        progressPopup = new ProgressPopup();
        total = is.available();
        startTime = System.nanoTime();
        progressPrinter = new Timer();
        progressPrinter.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            double fractionLeft = is.available() / (double) total;
                            double fractionDone = 1 - fractionLeft;
                            progressPopup.updatePercentage(fractionDone*100);
                            if (fractionLeft < 1) {
                                double secondsUsed = (System.nanoTime() - startTime) / 1e9;
                                long secondsLeft = Math.round(secondsUsed / fractionDone * fractionLeft);
                                progressPopup.updateTimeLeft(secondsLeft);
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }, 0, 1000);
    }

    /**
     * Aborts the updating of the UI load label
     */
    private void stopLoadBar() {
        progressPrinter.cancel();
        progressPopup.close();
        update();
    }

    /**
     * Loads a file from a given path
     * @param filename The path to the file
     * @param isAbsolute true if path is absolute, false if relative
     */
    public void load(String filename, boolean isAbsolute) {
        try {
            if (filename.endsWith(".osm")) {
                loadOSM(new AutoFileReader.XMLReader(filename, !isAbsolute));
            } else if (filename.endsWith(".zip")) {
                loadZIP(new AutoFileReader.ZipReader(filename, !isAbsolute));
            } else if (filename.endsWith(".bin")) {
                loadBIN(new AutoFileReader.ObjectReader<MapData>(filename, !isAbsolute));
            } else {
                throw new FileExtensionNotSupportedException();
            }
        } catch (IOException e) {
            progressPopup.close();
            JOptionPane.showMessageDialog(new JFrame(), Model.getString("load-io-exception"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
        } catch (FileExtensionNotSupportedException e) {
            progressPopup.close();
            JOptionPane.showMessageDialog(new JFrame(), Model.getString("load-file-extension-not-supported-exception"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
        } catch (SAXException e) {
            progressPopup.close();
            JOptionPane.showMessageDialog(new JFrame(), Model.getString("load-sax-exception"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            progressPopup.close();
            JOptionPane.showMessageDialog(new JFrame(), Model.getString("load-class-not-found-exception"), Model.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
        updateView();
    }

    /**
     * Loads specifically osm-files
     * @param xmlReader The xml-reader with the osm inpustream
     * @throws IOException  If the inputstream was not valid
     * @throws SAXException If the parsing went wrong
     */
    private void loadOSM(AutoFileReader.XMLReader xmlReader) throws IOException, SAXException {
        initiateLoadBar(xmlReader.getInputStream());
        xmlReader.parse(new OSMHandler(this));
        mapData.createKD();
        stopLoadBar();
    }

    /**
     * Loads specifically zip-files
     * @param zipReader The zip-reader to be loaded
     * @throws IOException  If the inputstream was not valid
     * @throws SAXException If the parsing of inner file went wrong
     * @throws ClassNotFoundException If casting inner file to mapdata went wrong
     */
    private void loadZIP(AutoFileReader.ZipReader zipReader) throws IOException, SAXException, ClassNotFoundException {
        ZipInputStream firstEntry = zipReader.getFirstInputStream();
        String filePath =  zipReader.getCurrentZipEntry().getName();
        int fileExtensionPosition = filePath.lastIndexOf(".")+1;
        String fileExtension = filePath.substring(fileExtensionPosition);
        if (fileExtension.equals("osm")) {
            loadOSM(new AutoFileReader.XMLReader(firstEntry));
        } else if(fileExtension.equals("bin")) {
            loadBIN(new AutoFileReader.ObjectReader<MapData>(firstEntry));
        }
    }

    /**
     * Loads specifically bin-files
     * @param objectReader The bin-reader to be loaded
     * @throws IOException  If the inputstream was not valid
     * @throws ClassNotFoundException If the casting of the file to mapdata went wrong
     */
    private void loadBIN(AutoFileReader.ObjectReader<MapData> objectReader) throws IOException, ClassNotFoundException {
        initiateLoadBar(objectReader.getInputStream());
        mapData = objectReader.getObject();
        mapData.getGraph().setModel(this);
        stopLoadBar();
    }

    /**
     * Updates the view and sets a new center for map
     */
    private void updateView() {
        double centerX = ((mapData.getMaxlon()-mapData.getMinlon())/2)+mapData.getMinlon();
        double centerY = ((mapData.getMaxlat()-mapData.getMinlat())/2)+mapData.getMinlat();
        mapCenterPoint = new Point2D.Double(centerX, centerY);
        mapZoomFactor = 100/(Math.abs(mapData.getMinlon() - mapData.getMaxlon()))*6;
        newMapLoaded = true;
    }


    public float getMinlon() {
        return mapData.getMinlon();
    }

    public float getMaxlat() {
        return mapData.getMaxlat();
    }

    public float getMaxlon() {
        return mapData.getMaxlon();
    }

    public List<KDFriendlyShape> getCoastLines() {
        return coastLines;
    }

    public ArrayList<MapDrawStyle> getMapDrawStyles() {
        return mapDrawStyles;
    }

    /**
     * Gets addresses OnePointMarker coordinates
     * @param a Address to find coordinates
     * @return OnePointMapMarker coordinates
     */
    public OnePointMapMarker getAddressCoords(Address a) {
        return mapData.getAddressCoords(a);
    }

    /**
     * Sets new current drawStyle
     * @param currentMapDrawStyle Drawstyle to be set as new current drawStyle
     */
    public void setCurrentMapDrawStyle(MapDrawStyle currentMapDrawStyle) {
        drawStyleController.setCurrentMapDrawStyle(currentMapDrawStyle);
        update();
    }

    public MapDrawStyle getCurrentMapDrawStyle() {
        return drawStyleController.getCurrentMapDrawStyle();
    }

    public OnePointMapMarker getCurrentMapMarker() {
        return currentMapMarker;
    }

    public void setCurrentMapMarker(OnePointMapMarker currentMapMarker) {
        this.currentMapMarker = currentMapMarker;
        needsToMove = true;
        update();
    }

    public Point2D getDepartureMapMarker() {
        return depToDest.getDeparturePoint();
    }

    public void setDepartureMapMarker(Point2D departureMapMarker) {
        depToDest.setDeparturePoint(departureMapMarker);
        needsToMove = true;
        update();
    }

    public Point2D getDestinationMarker() {
        return depToDest.getDestinationPoint();
    }

    public void setDestinationMarker(Point2D destinationMarker) {
        depToDest.setDestinationPoint(destinationMarker);
        depToDest.needsToMove();
        update();
    }

    public RouteMapMarkers getDepToDest(){
        return depToDest;
    }

    public TST<String> getCities() {
        return mapData.getCities();
    }

    public void setCities(TST<String> cities) {
        mapData.setCities(cities);
    }

    public TST<String> getPostcodes() {
        return mapData.getPostcodes();
    }

    public void setPostcodes(TST<String> postcodes) {
        mapData.setPostcodes(postcodes);
    }

    public TST<String> getStreetNames() {
        return mapData.getStreetNames();
    }

    public void setStreetNames(TST<String> streetNames) {
        mapData.setStreetNames(streetNames);
    }


    public KDTreeNode getKdTree() {
        return mapData.getKdTree();
    }


    public MapData getMapData() {
        return mapData;
    }

    public DrawStyleController getDrawStyleController() {
        return drawStyleController;
    }


    public List<PointOfInterest> getPointsOfInterest(){
        return mapData.getPointsOfInterest();
    }

    /**
     * Adds new Point of Interest
     * @param pointOfInterest PointOfInterest to be added to map
     */
    public void addPointOfInterest(PointOfInterest pointOfInterest){
        mapData.getPointsOfInterest().add(pointOfInterest);
        update();
    }

    /**
     * Set name for Point of Interest Marker
     * @param mapMarkerIndex index of marker
     * @param name name for the marker
     */
    public void setPointOfInterestName(int mapMarkerIndex, String name){
        mapData.getPointsOfInterest().get(mapMarkerIndex).setName(name);
        update();
    }

    /**
     * Set color for Point of Interest Marker
     * @param mapMarkerIndex index of marker
     * @param color color for the marker
     */
    public void setPointOfInterestColor(int mapMarkerIndex, ColorHex color){
        mapData.getPointsOfInterest().get(mapMarkerIndex).setColor(color);
        update();
    }

    /**
     * Deletes point of interest
     * @param mapMarkerIndex index of marker to be deleted
     */
    public void deletePointOfInterest(int mapMarkerIndex){
        mapData.getPointsOfInterest().remove(mapMarkerIndex);
        update();
    }

    /**
     * Converts screen coordinates to model coordinates
     * @param screenCoords Screen coordinates
     * @return Converted coordinates
     */
    public Point2D toModelCoords(Point2D screenCoords) {
        try {
            return transform.inverseTransform(screenCoords, null);
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCoastlinesLoaded() {
        return isCoastlinesLoaded;
    }

    public void initCoastlines(CoastLines coastLines) {
        this.coastLines = coastLines;
    }

    public float getLonfactor() {
        return coastLines.lonfactor;
    }

    public Point2D getMapCenterPoint() {
        return mapCenterPoint;
    }

    public double getMapZoomFactor() {
        return mapZoomFactor;
    }

    /**
     * Sets new current language, by searching list of all languages and sets the correct language as new current language
     * @param lang language to find
     */
    public static void setLanguage(String lang) {
        for (Language l : languageList){
            if (lang.equals(l.getName())){
                language = l;
            }
        }
    }

    /**
     * Static method that is used for getting strings from languages, this is helping
     * us to switch easier between different languages
     * @param s The key that will be used for searching in Language
     * @return The string to be used in current language
     */
    public static String getString(String s) {
        return language.getString(s);
    }

    public void setGraph(RoadGraph graph) {
        mapData.setGraph(graph);
    }

    public RoadGraph getGraph() {
        return mapData.getGraph();
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
        update();
    }

    public void currentMapMarkerHasMoved(){
        currentMapMarker.hasMoved();
    }

    public void depToDesthasMoved(){
        depToDest.hasMoved();
    }

    /**
     * Static method that will set the language list. Then sets a new current language to
     * be the first language in list of all available languages
     * @param languageList
     */
    public static void setLanguageList(List<Language> languageList) {
        Model.languageList = languageList;
        setLanguage(languageList.get(0).getName());
    }

    /**
     * Static method to get all languages
     * @return list of all available languages
     */
    public static List<Language> getLanguageList() {
        return languageList;
    }

    public void clearRouteMapMarkers() {
        depToDest.setDeparturePoint(new Point2D.Float());
        depToDest.setDestinationPoint(new Point2D.Float());
        update();
    }
}