package Controller;

import Model.Address.Address;
import Model.*;
import Model.Graph.RoadGraph;
import Model.OSM.*;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.awt.geom.Point2D;
import java.util.*;
import lib.TST;

/**
 * This class is an XMLHandler that handles OSM file types
 */
public class OSMHandler implements ContentHandler {

    private Model model;
    private float minlat, minlon, maxlat,maxlon, currentLat, currentLon;

    private LongToPointMap points;
    private Map<Long, List<SimpleOSMNode>> ways;
    private List<OSMHighway> highways;
    private Map<Long, OSMHighway> highwayMap;
    private Map<SimpleOSMNode, SimpleOSMWay> coastlines;

    private long id;
    private SimpleOSMWay way;
    private SimpleOSMRelation relation;
    private WayType type;
    public MapData mapdata;
    private boolean isArea;
    private String wayStreetName;
    private Integer waySpeedLimit;
    private boolean wayIsOneway;
    private boolean wayIsRoundabout;

    private String nodeName;
    private boolean nodeStarted;

    private boolean relationIsRestriction;
    private long restrictionFrom;
    private long restrictionTo;

    //For addresses
    private String streetName, streetNumber, city, postcode;

    //For autocompleter
    private TST<String>
            cities = new TST<>(),
            postcodes = new TST<>(),
            streetNames = new TST<>();

    private boolean cyclingAllowed = true;
    private boolean walkingAllowed = true;
    private boolean drivingAllowed = true;
    private boolean wayIsReverseOneway = false;

    private float lat;
    private float lon;

    public OSMHandler(Model model){
        this.model = model;
        points = new LongToPointMap(22);
        ways = new HashMap<>();
        mapdata = model.getMapData();
        coastlines = new HashMap<>();
        highways = new ArrayList<>();
        highwayMap = new HashMap<>();
    }


    @Override
    public void setDocumentLocator(Locator locator) {        }

    @Override
    public void startDocument() throws SAXException {     }

    @Override
    public void endDocument() throws SAXException {
        model.setCities(cities);
        model.setPostcodes(postcodes);
        model.setStreetNames(streetNames);
        model.maxlat = maxlat;
        model.maxlon = maxlon;
        model.minlat = minlat;
        model.minlon = minlon;
        model.setGraph(new RoadGraph(model, highways));
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (qName) {
            case "bounds":
                parseBounds(atts);
                break;
            case "node":
                parseNodes(atts);
                break;
            case "way":
                parseWay(atts);
                break;
            case "relation":
                parseRelation(atts);
                break;
            case "nd":
                parseND(atts);
                break;
            case "tag":
                parseTag(atts);
                break;
            case "member":
                parseMember(atts);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "way":
                if (type == WayType.NATURAL_COASTLINE || type == WayType.BORDER_TYPE_NATION && !model.isCoastlinesLoaded()) {
                    SimpleOSMWay before = coastlines.remove(way.getFirstNode());
                    SimpleOSMWay after = coastlines.remove(way.getLastNode());
                    SimpleOSMWay merged = new SimpleOSMWay();
                    if (before != null){
                        merged.addAll(before.subList(0,before.size() - 1));
                    }

                    merged.addAll(way);

                    if(after != null && after != before) {
                        merged.addAll(after.subList(1,after.size()));
                    }

                    coastlines.put(merged.getFirstNode(), merged);
                    coastlines.put(merged.getLastNode(), merged);
                } else if (type.toString().toUpperCase().startsWith("HIGHWAY")) {
                    OSMHighway highwayToAdd = new OSMHighway(way);
                    highwayToAdd.setWayType(type);
                    highwayToAdd.setName(wayStreetName);
                    if (waySpeedLimit == null) {
                        if (type.toString().startsWith("HIGHWAY_RESIDENTIAL") || type.toString().startsWith("HIGHWAY_LIVING_STREET") ||
                                type.toString().startsWith("HIGHWAY_SERVICE") || type.toString().startsWith("HIGHWAY_TERTIARY")) {
                            highwayToAdd.setSpeedLimit(50);
                        } else if (type.toString().startsWith("HIGHWAY_MOTORWAY") || type.toString().startsWith("HIGHWAY_TRUNK")) {
                            highwayToAdd.setSpeedLimit(130);
                        } else {
                            //for road and proposed roads ? not enough info about raods and
                            //proposed roads shouldn't be used
                            //should be extended
                            highwayToAdd.setSpeedLimit(80);
                        }
                    } else {
                        highwayToAdd.setSpeedLimit(waySpeedLimit);
                    }

                    if (wayIsOneway) highwayToAdd.setOneWay();

                    if (type == WayType.HIGHWAY_MOTORWAY || type == WayType.HIGHWAY_MOTORWAY_LINK) {
                        walkingAllowed = false;
                        cyclingAllowed = false;
                    }
                    if (type == WayType.HIGHWAY_FOOTWAY || type == WayType.HIGHWAY_PEDESTRIAN || type == WayType.HIGHWAY_CYCLEWAY || type == WayType.HIGHWAY_PEDESTRIAN_AREA || type == WayType.HIGHWAY_PATH) {
                        drivingAllowed = false;
                    }

                    highwayToAdd.setWalkingAllowed(walkingAllowed);
                    highwayToAdd.setCyclingAllowed(cyclingAllowed);
                    highwayToAdd.setDrivingAllowed(drivingAllowed);
                    highwayToAdd.setIsRoundabout(wayIsRoundabout);
                    if (wayIsReverseOneway) Collections.reverse(highwayToAdd.getPoints());
                    model.add(highwayToAdd);
                    highways.add(highwayToAdd);
                    highwayMap.put(id, highwayToAdd);

                } else {
                    OSMWay shapeToAdd = new OSMWay(way);
                    shapeToAdd.setWayType(type);
                    shapeToAdd.setName(wayStreetName);
                    model.add(shapeToAdd);
                }
                wayStreetName = null;
                walkingAllowed = true;
                cyclingAllowed = true;
                drivingAllowed = true;
                wayIsReverseOneway = false;
                type = null;
                waySpeedLimit = null;
                wayIsOneway = false;
                wayIsRoundabout = false;

                break;
            case "relation":
                if (relationIsRestriction && highwayMap.get(restrictionFrom) != null && highwayMap.get(restrictionTo) != null) {
                    highwayMap.get(restrictionFrom).addRestrictedHighway(highwayMap.get(restrictionTo));

                } else {
                    if (relation.get(0) != null) {
                        OSMRelation shapeToAdd = new OSMRelation(relation);
                        shapeToAdd.setWayType(type);
                        model.add(shapeToAdd);
                    }
                }

                relationIsRestriction = false;
                break;


            case "osm":
                if (!model.isCoastlinesLoaded()) {
                    coastlines.forEach((key, way) -> {
                        if (key == way.getFirstNode()) {
                            OSMWay shapeToAdd = new OSMWay(way);
                            shapeToAdd.setWayType(WayType.NATURAL_COASTLINE);
                            model.add(shapeToAdd);
                        }
                    });
                }
                break;

            case "node":
                if (streetName != null && streetNumber != null && city != null && postcode != null) {
                    model.getMapData().putAddress(new Address(streetName.toLowerCase(), streetNumber.toLowerCase(), null, null, postcode.toLowerCase(), city.toLowerCase()), new OnePointMapMarker(currentLon*model.getLonfactor(), -currentLat));
                    streetNames.put(streetName, "");
                    postcodes.put(postcode, "");
                    postcodes.put(postcode+" "+city, "");
                    cities.put(city, "");
                }

                if (nodeStarted && type != null) {
                    model.add(new OSMNode(new Point2D.Float(model.getLonfactor() * lon, -lat), type, nodeName));
                    type = null;
                    nodeName = null;
                }
                nodeStarted = false;

                streetName = null;
                streetNumber = null;
                city = null;
                postcode = null;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

    /**
     * Parses bounds for canvas
     * sets MapData
     * @param atts
     */
    private void parseBounds(Attributes atts){
        minlat = Float.parseFloat(atts.getValue("minlat"));
        minlon = Float.parseFloat(atts.getValue("minlon"));
        maxlat = Float.parseFloat(atts.getValue("maxlat"));
        maxlon = Float.parseFloat(atts.getValue("maxlon"));
        float avglat = minlat + (maxlat - minlat) / 2;
        //Checks if coastlines are already loaded for a .bin
        if (!model.isCoastlinesLoaded()) {
            model.initCoastlines(new CoastLines((float) Math.cos(avglat / 180 *Math.PI)));
        }
        minlon *= model.getLonfactor();
        maxlon *= model.getLonfactor();
        minlat = -minlat;
        maxlat = -maxlat;
        model.setMapData(new MapData(minlat, minlon,maxlat,maxlon));
    }

    /**
     * Parses nodes
     * Adds node to HashMap points
     * sets currentLat and currentLon
     * @param atts
     */
    private void parseNodes(Attributes atts) {
        id = Long.parseLong(atts.getValue("id"));
        lat = Float.parseFloat(atts.getValue("lat"));
        lon = Float.parseFloat(atts.getValue("lon"));
        currentLat = lat;
        currentLon = lon;
        points.put(id,model.getLonfactor() * lon, -lat);
        nodeStarted = true;
    }

    /**
     * Parses way
     * Adds way to HashMap ways
     * @param atts
     */
    private void parseWay(Attributes atts) {
        way = new SimpleOSMWay();
        id = Long.parseLong(atts.getValue("id"));
        type = WayType.UNKNOWN;
        ways.put(id, way);
    }

    /**
     * Parses relations
     * @param atts
     */
    private void parseRelation(Attributes atts) {
        relation = new SimpleOSMRelation();
        type = WayType.UNKNOWN;
    }

    /**
     * Parses a node which is a member of a way
     * @param atts
     */
    private void parseND(Attributes atts) {
        long ref = Long.parseLong(atts.getValue("ref"));
        way.add(points.get(ref));
    }

    /**
     * Parses a tag
     * @param atts
     */
    private void parseTag(Attributes atts) {
        String k = atts.getValue("k");
        String v = atts.getValue("v");
        String stringToCompare = k.toUpperCase() + "_" + v.toUpperCase();

        if (k.equals("name")) {
            wayStreetName = v;
        }
        if (dummyTagsCather(k)) return;

        if (k.equals("type") && v.equals("restriction")) relationIsRestriction = true;

        if (isArea) {
            stringToCompare = stringToCompare + "_AREA";
            isArea = false;
        }
        if (stringToCompare.equals("AREA_YES")) isArea = true;

        //Check for all valid WayTypes if key and value equals one of them
        for (WayType _type : WayType.values()) {
            if (stringToCompare.equals(_type.toString())) {
                type = _type;
                isArea = false;
                break;
            }
        }

        //Adding speedlimit and oneway info to OSMWays that are roads
        if ( type != null && type.toString().startsWith("HIGHWAY_")) {

            if(k.equals("maxspeed")) {
                if(isNumeric(v)) {
                    waySpeedLimit = Integer.parseInt(v);
                }
                else if (v.equals("DK:urban")){
                    waySpeedLimit = 50;
                }
                else if (v.equals("DK:rural")){
                    waySpeedLimit = 80;
                }
            } else if((k.equals("oneway") && v.equals("yes"))) {
                wayIsOneway = true;
            } else if(k.equals("oneway") && v.equals("-1")) {
                wayIsOneway = true;
                wayIsReverseOneway = true;
            } else if(k.equals("bicycle") && v.equals("no")) {
                cyclingAllowed = false;
            } else if(k.equals("foot") && v.equals("no")) {
                walkingAllowed = false;
            } else if(k.equals("motor_vehicle") && v.equals("no")) {
                drivingAllowed = false;
            } else if(k.equals("junction") && v.equals("roundabout")) {
                wayIsOneway = true;
                wayIsRoundabout = true;
            }
        }

        //Address & completer related:
        if (k.equals("addr:street")) {
            streetName = v;
        }
        if (k.equals("addr:city")) {
            city = v;
        }
        if (k.equals("addr:postcode")) {
            postcode = v;
        }
        if (k.equals("addr:housenumber")){
            streetNumber = v;
        }
    }
    private boolean isNumeric(String v) {
        try {
            Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    /**
     * Method that catches stupid tags with no use
     * @param k is the key
     * @return true if a tag is caught and otherwise false
     */
    private boolean dummyTagsCather(String k) {
        switch (k) {
            case "architect":
                return true;
            case "level":
                return true;
            case "note":
                return true;
            case "covered":
                return true;
            case "surface":
                return true;
            case "lit":
                return true;
            case "access":
                return true;
        }
        return false;
    }

    /**
     * Parse member
     * @param atts
     */
    private void parseMember(Attributes atts) {
        long ref = Long.parseLong(atts.getValue("ref"));
        String role = atts.getValue("role");
        if (role.equals("from")) restrictionFrom = ref;
        if (role.equals("to")) restrictionTo = ref;

        relation.add(ways.get(ref));
    }
}