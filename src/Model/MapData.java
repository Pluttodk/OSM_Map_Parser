package Model;

import Model.Address.Address;
import Model.Graph.RoadGraph;
import Model.OSM.WayType;
import lib.RedBlackBST;
import lib.TST;
import Model.KDTree.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class MapData implements Serializable {
    private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class); {
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
    }
    private KDTreeNode kdRoot;
    private float minlat, minlon, maxlat, maxlon;
    private List<KDFriendlyShape> shapesForKDTree;

    private List<PointOfInterest> pointsOfInterest;

    private TST<String> cities, postcodes, streetNames;

    private RoadGraph graph;

    private RedBlackBST<Address, OnePointMapMarker> addressCoords = new RedBlackBST<>();

    /**
     * Constructs an instance of MapData
     * @param minlat
     * @param minlon
     * @param maxlat
     * @param maxlon
     */
    public MapData(float minlat, float minlon, float maxlat, float maxlon) {
        this.minlat = minlat;
        this.minlon = minlon;
        this.maxlat = maxlat;
        this.maxlon = maxlon;
        shapesForKDTree = new ArrayList<>();

        cities = new TST<>();
        postcodes = new TST<>();
        streetNames = new TST<>();
        pointsOfInterest = new ArrayList<>();
    }

    public OnePointMapMarker getAddressCoords(Address a) {
        return addressCoords.get(a);
    }

    public void putAddress(Address a, OnePointMapMarker coords) {
        addressCoords.put(a, coords);
    }

    public float getMinlat() {
        return minlat;
    }

    public float getMinlon() {
        return minlon;
    }

    public float getMaxlat() {
        return maxlat;
    }

    public float getMaxlon() {
        return maxlon;
    }

    public TST<String> getCities() {
        return cities;
    }

    public void setCities(TST<String> cities) {
        this.cities = cities;
    }

    public TST<String> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(TST<String> postcodes) {
        this.postcodes = postcodes;
    }

    public TST<String> getStreetNames() {
        return streetNames;
    }

    public void setStreetNames(TST<String> streetNames) {
        this.streetNames = streetNames;
    }

    public EnumMap<WayType, List<Shape>> getShapes() {
        return shapes;
    }

    public void addShapeToKDTree(KDFriendlyShape shape) {
        shapesForKDTree.add(shape);
    }

    /***
     * Calls KDtree methods to
     * create an KDTree and saves kdRoot
     */
    public void createKD() {
        kdRoot = KDTree.getRoot(shapesForKDTree.toArray(new KDFriendlyShape[]{}));
        shapesForKDTree.clear();
    }

    public KDTreeNode getKdTree() {
        return kdRoot;
    }

    public List<PointOfInterest> getPointsOfInterest(){
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest){
        this.pointsOfInterest = pointsOfInterest;
    }

    public void setGraph(RoadGraph graph) {
        this.graph = graph;
    }

    public RoadGraph getGraph() {
        return graph;
    }
}
