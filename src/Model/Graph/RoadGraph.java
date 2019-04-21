package Model.Graph;

import Model.Exceptions.RouteNotPossibleException;
import Model.KDTree.KDFriendlyShape;
import Model.Model;
import Model.OSM.OSMHighway;
import Model.OSM.SimpleOSMNode;
import View.MenuElements.DirectionsPanel;
import lib.Bag;
import Model.EuclideanDistance;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A graph representation of the network of roads
 */
public class RoadGraph implements Serializable {
    private static final int VERTEX_OVERHEAD = 10;
    //Fields from Sedgewick and Wayne
    private int vertexCount;
    private List<Bag<Edge>> adj;

    private ArrayList<SimpleOSMNode> intersectionPoints;
    private HashMap<SimpleOSMNode, Integer> reverseIntersectionPoints;
    private Model model;
    private Iterable<RoadGraph.Edge> path;
    private DirectionsPanel directionsPanel;
    private double routeLength;

    /**
     * The initial construction of the graph
     * @param model The model to be represented
     * @param highways The highways to be represented
     */
    public RoadGraph(Model model, List<OSMHighway> highways) {
        this.model = model;

        directionsPanel = new DirectionsPanel(); //TODO: FLYT MIG UD HER FRA

        findIntersections(highways);

        //Setting up graph according to Sedgewick and Wayne
        vertexCount = intersectionPoints.size();

        adj = new ArrayList<Bag<Edge>>(vertexCount + VERTEX_OVERHEAD);
        for (int v = 0; v < vertexCount; v++) {
            adj.add(v, new Bag<>());
        }

        buildGraph(highways);
    }

    /**
     * Building the graph
     * @param highways the highways to be represented in the graph
     */
    private void buildGraph(List<OSMHighway> highways) {
        for (OSMHighway highway : highways) {
            splitWayToEdges(highway);
        }
    }

    /**
     * Splitting the highway into edges by considering the contained intersections
     * @param highway the highway to be splitted
     */
    private void splitWayToEdges(OSMHighway highway) {
        Point2D lastIntersection = null;
        Point2D lastWayPoint = null;
        float lengthSinceLastIntersection = 0;
        List<SimpleOSMNode> wayPoints = new ArrayList<>();
        for (SimpleOSMNode wayPoint : highway.getPoints()) {
            if (lastWayPoint != null) {
                lengthSinceLastIntersection += EuclideanDistance.dist(model, wayPoint, lastWayPoint);
            }
            if (reverseIntersectionPoints.containsKey(wayPoint)) {
                SimpleOSMNode intersection = wayPoint;
                wayPoints.add(intersection);
                if (lastIntersection != null) {
                    RoadGraph.Edge newEdge = new RoadGraph.Edge(reverseIntersectionPoints.get(lastIntersection), reverseIntersectionPoints.get(intersection), lengthSinceLastIntersection,
                            highway.getSpeedLimit(), highway.getName(), wayPoints, highway, highway.isOneWay(), highway.isWalkingAllowed(), highway.isCyclingAllowed(), highway.isDrivingAllowed(), highway.isRoundabout());

                    addDoubleEdge(newEdge, highway);
                    lengthSinceLastIntersection = 0;
                }
                lastIntersection = intersection;
                wayPoints = new ArrayList<>();
            }
            lastWayPoint = wayPoint;
            wayPoints.add(wayPoint);
        }
    }

    /**
     * Finding the intersections based on the intersectionCounter
     * @param highways The highways to be checked for intersections
     */
    private void findIntersections(List<OSMHighway> highways) {
        Map<Point2D, Integer> intersectionCounter = countWaysPerPoint(highways);
        intersectionPoints = new ArrayList<>();
        reverseIntersectionPoints = new HashMap<>();
        int j = 0;
        for (Map.Entry e : intersectionCounter.entrySet()) {
            if (((int) e.getValue()) > 1) {
                intersectionPoints.add((SimpleOSMNode) e.getKey());
                reverseIntersectionPoints.put((SimpleOSMNode) e.getKey(), j);
                j++;
            }
        }
    }

    /**
     * Counting how many highways that use a specific point
     * @param highways the highways to be considered
     * @return The map of point2d->number of highways using that point
     */
    private Map<Point2D, Integer> countWaysPerPoint(List<OSMHighway> highways) {
        Map<Point2D, Integer> intersectionCounter = new HashMap<>();
        for (OSMHighway way : highways) {
            for (Point2D point : way.getPoints()) {
                if (intersectionCounter.containsKey(point)) {
                    Integer value = intersectionCounter.get(point);
                    intersectionCounter.put(point, ++value);
                } else {
                    intersectionCounter.put(point, 1);
                }
            }
        }
        return intersectionCounter;
    }

    /**
     * Adding a vertex to the data structure
     * @return The id of the new vertex
     */
    public int addVertex() {
        adj.add(new Bag<>());
        return (++vertexCount)-1;
    }

    /**
     * A getter for the number of vertices
     * @return The number of vertices in the data structure
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Checks if a given vertex exists in the data structure
     * @param v The given vertex
     */
    public void validateVertex(int v) {
        if (v < 0 || v >= vertexCount)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (vertexCount -1));
    }

    /**
     * Doubly links the two vertexes bound in the given edge
     * @param e the given edge
     * @param highway the highway that the edge partially represents
     */
    public void addDoubleEdge(Edge e, OSMHighway highway) {
        addSingleEdge(e, highway);
        Edge reverseEdge;
        if (!e.isOneWay()) {
            reverseEdge = new Edge(e.to(), e.from(), e.getLength(), e.getSpeedLimit(), e.getName(), e.getWayPoints(), e.getHighway(), e.isOneWay(),
                    e.isWalkingAllowed(), e.isCyclingAllowed(), e.isDrivingAllowed(), e.isRoundabout());
        } else {
            reverseEdge = new Edge(e.to(), e.from(), e.getLength(), e.getSpeedLimit(), e.getName(), e.getWayPoints(), e.getHighway(), e.isOneWay(),
                    e.isWalkingAllowed(), e.isCyclingAllowed(), false, e.isRoundabout());
        }
        addSingleEdge(reverseEdge, highway);
    }

    /**
     * Adds the given edge as a single link between the vertices given in the edge
     * @param e the given edge
     * @param highway the highway that the edge partially represents
     */
    public void addSingleEdge(Edge e, OSMHighway highway) {
        adj.get(e.from()).add(e);
        highway.addAssociatedEdge(e);
    }

    /**
     * Getter for all the adjacent edges to a given vertex
     * @param v the given vertex
     * @return An iterable object with all the adjacent edges
     */
    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj.get(v);
    }

    /**
     * Getter for all the edges in the data structure
     * @return An iterable object with all the edges
     */
    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < vertexCount; v++) {
            for (Edge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Calculates the angle between a startpoint and endpoint as a part of a series of (path) points via the centerpoint.
     * The calculation is based on the vectors spanning between the centerpoint and startpoint, and centerpoint and endpoint respectively
     * @param startPoint the
     * @param centerPoint
     * @param endPoint
     * @return the angle between startpoint and endpoint via centerpoint
     */
    private double calcAngle(Point2D startPoint, Point2D centerPoint, Point2D endPoint) {
        double angle = Math.toDegrees(Math.atan2(endPoint.getY()-centerPoint.getY(),endPoint.getX() -centerPoint.getX())
                - Math.atan2(startPoint.getY()-centerPoint.getY(), startPoint.getX()-centerPoint.getX()));
        //Assures that all angles are positive
        if (angle < 0) angle += 360;
        return angle;
    }

    /**
     * Clears the current found path
     */
    public void clearPath() {
        path = null;
    }

    /**
     * Find a path between the two given vertices and setting the path field to the result
     * @param start Start vertex
     * @param end End vertex
     * @param travelType The selected method of traveling
     * @throws RouteNotPossibleException If the route is not possible
     */
    public void findPath(int start, int end, TravelType travelType) throws RouteNotPossibleException {
        RoadDijsktra pathFinder = new RoadDijsktra(model, this, start, end, travelType);

        directionsPanel = new DirectionsPanel();
        path = pathFinder.pathTo(end);
        RoadGraph.Edge prevEdge = null;
        float roadLength = 0, routeTime = 0;
        String name;
        double prevAngle = 0;
        routeLength = 0;
        boolean inRoundabout = false;
        Point2D roundaboutStartPoint = null, roundaboutEndPoint = null, roundaboutCenterPoint = null;

        if (path == null) throw new RouteNotPossibleException();

        //runs through all edges in the path
        for (RoadGraph.Edge e : path) {
            routeLength += e.getLength();
            routeTime += calcTravelTime(travelType, e);

            prevEdge= pathFinder.getPrevEdge(e);
            if (prevEdge != null) {
                Point2D startPoint = getSpecificPoint(prevEdge.from());
                Point2D centerPoint = getSpecificPoint(e.from());
                Point2D endPoint = getSpecificPoint(e.to());
                name = e.getName();
                double angle = calcAngle(startPoint, centerPoint, endPoint);

                //checks if it is a roundabout
                if (!inRoundabout && e.isRoundabout()) {
                    inRoundabout = true;
                    roundaboutStartPoint = getSpecificPoint(prevEdge.from());
                    roundaboutCenterPoint = getSpecificPoint(prevEdge.to());
                } else if (inRoundabout && !e.isRoundabout()) {
                    roundaboutEndPoint = getSpecificPoint(e.to());
                    angle = calcAngle(roundaboutStartPoint, roundaboutCenterPoint, roundaboutEndPoint);
                    directionsPanel.addDirectionBasedOnAngle(angle, e.getLength(), e.getName(), true);
                    inRoundabout = false;
                } else if (prevEdge.getName() != null && e.getName() != null && prevEdge.getName().equals(e.getName())) {
                    roadLength += e.getLength();
                } else if (prevAngle != 0) {
                    if(path.iterator().hasNext()) {
                        directionsPanel.addDirectionBasedOnAngle(angle, roadLength, name, false);
                        roadLength = e.getLength();
                    } else {
                        directionsPanel.addDirectionBasedOnAngle(angle, roadLength, name, false);
                    }
                } else {
                    directionsPanel.addDirectionBasedOnAngle(angle, roadLength, name, false);
                }
                prevAngle = angle;
            }
        }
        directionsPanel.setRouteLength(routeLength);
        String time = getTimeString(routeTime);
        directionsPanel.setRouteTime(time);
    }

    /**
     * Getting the corresponding formatted time string to the time input
     * @param routeTime The time in hours
     * @return The formatted time string
     */
    private String getTimeString(double routeTime) {
        int seconds, minute = 0, hour = 0;
        if(routeTime >= 1.0) {
            hour = (int) Math.floor(routeTime);
            routeTime = routeTime - hour;
        }
        if(routeTime > 0.0165) {
            minute = (int) Math.floor(routeTime * 60);
            routeTime = (routeTime*60) - minute;
            seconds = (int) Math.floor(routeTime*60);
        } else {
            seconds = (int) Math.floor(routeTime*3600);
        }
        if(hour > 99) hour = 99;
        return numberToString(hour) + ":" + numberToString(minute) + ":" + numberToString(seconds);
    }

    /**
     * Formats the given number to a string
     * @param number The given number
     * @return The resulting string
     */
    private String numberToString(int number) {
        if(number < 10 && number >= 0) {
            return "0"+number;
        } else {
            return new DecimalFormat("##").format(number);
        }
    }

    /**
     * Calculates the traveltime for the given edge
     * @param travelType The selected method of traveling
     * @param edge The given edge
     * @return The total travel time in hours
     */
    private float calcTravelTime(TravelType travelType, RoadGraph.Edge edge) {
        float travelTime;
        if(travelType.getMaxSpeed() < edge.getSpeedLimit()) {
            travelTime = edge.getLength()/travelType.getMaxSpeed();
        } else if (edge.getSpeedLimit() != 0){
            travelTime = edge.getLength()/edge.getSpeedLimit();
        } else {
            travelTime = 0;
        }
        return  travelTime;
    }

    public ArrayList<SimpleOSMNode> getIntersectionPoints() {return intersectionPoints;}

    public Iterable<RoadGraph.Edge> getPath() {
        return path;
    }

    public Point2D getSpecificPoint(int key) {
        return intersectionPoints.get(key);
    }

    public DirectionsPanel getDirectionsPanel() {
        return directionsPanel;
    }

    /**
     * Adds a new vertex to the data structure based on the point of the vertex and the way it's part of
     * @param p the vertex point
     * @param way the way it's part of
     * @return the id of the newly added vertex
     */
    public int addVertex(Point2D p, KDFriendlyShape way) {
        return addVertex(new SimpleOSMNode(p.getX(), p.getY()), way);
    }

    /**
     * Adds a new vertex to the data structure based on the osm node of the vertex and the highway it's part of
     * @param p the vertex point
     * @param way the way it's part of
     * @return the id of the newly added vertex
     */
    public int addVertex(SimpleOSMNode p, KDFriendlyShape way) {
        if (reverseIntersectionPoints.containsKey(p)) return reverseIntersectionPoints.get(p);
        SimpleOSMNode lastIntersectionPoint = null;
        boolean pIsMet = false;
        List<SimpleOSMNode> wayPointsSinceLastIntersection = new ArrayList<>();
        List<SimpleOSMNode> wayPointsFromPToIntersectionBefore = new ArrayList<>();
        for (SimpleOSMNode wayPoint : way.getPoints()) {
            wayPointsSinceLastIntersection.add(wayPoint);
            if (wayPoint.equals(p)) {
                wayPointsFromPToIntersectionBefore = wayPointsSinceLastIntersection;
                wayPointsSinceLastIntersection = new ArrayList<>();
                wayPointsSinceLastIntersection.add(wayPoint);
                pIsMet = true;
            }
            if (pIsMet && reverseIntersectionPoints.containsKey(wayPoint)) {
                if(reverseIntersectionPoints.get(lastIntersectionPoint) == null) break;
                int intersectionBefore = reverseIntersectionPoints.get(lastIntersectionPoint);
                int intersectionAfter = reverseIntersectionPoints.get(wayPoint);

                return interposeVertex(p, intersectionBefore, intersectionAfter, wayPointsFromPToIntersectionBefore, wayPointsSinceLastIntersection, lastIntersectionPoint, wayPoint);
            }
            if (reverseIntersectionPoints.containsKey(wayPoint)) {
                lastIntersectionPoint = wayPoint;
                wayPointsSinceLastIntersection = new ArrayList<>();
                wayPointsSinceLastIntersection.add(wayPoint);
            }
        }
        return reverseIntersectionPoints.get(wayPointsSinceLastIntersection.get(wayPointsSinceLastIntersection.size()-1));
    }

    /**
     * Interposes a vertex between two existing vertices and their edges
     * @param vertexPosition The vertex position to be interposed
     * @param intersectionBefore The id of the intersection before the interposed intersection
     * @param intersectionAfter The id of the intersection after the interposed intersection
     * @param wayPointsFromIntersectionBeforeToVertex The waypoints from the previous intersection to the interposed vertex
     * @param wayPointsFromVertexToIntersectionAfter The waypoints from he interposed vertex to the next intersection
     * @param intersectionPointBefore The position of the intersection before the interposed vertex
     * @param intersectionPointAfter The position of the intersection after the interposed vertex
     * @return
     */
    public int interposeVertex(SimpleOSMNode vertexPosition, int intersectionBefore, int intersectionAfter, List<SimpleOSMNode> wayPointsFromIntersectionBeforeToVertex, List<SimpleOSMNode> wayPointsFromVertexToIntersectionAfter, SimpleOSMNode intersectionPointBefore, SimpleOSMNode intersectionPointAfter) {
        int vertexId = addVertex();
        intersectionPoints.add(vertexPosition);
        reverseIntersectionPoints.put(vertexPosition, vertexId);

        splitEdgeWithInterposedVertex(vertexId, vertexPosition, intersectionBefore, intersectionAfter, wayPointsFromIntersectionBeforeToVertex, wayPointsFromVertexToIntersectionAfter, intersectionPointBefore, intersectionPointAfter);
        splitEdgeWithInterposedVertex(vertexId, vertexPosition, intersectionAfter, intersectionBefore, wayPointsFromVertexToIntersectionAfter, wayPointsFromIntersectionBeforeToVertex, intersectionPointAfter, intersectionPointBefore);

        return vertexId;
    }

    /**
     * Splits the edges between the vertices before and after the interposed vertex
     * @param vertex id of the interposed vertex
     * @param vertexPosition position of the interposed vertex
     * @param intersectionBefore The id of the intersection before the interposed intersection
     * @param intersectionAfter The id of the intersection after the interposed intersection
     * @param wayPointsFromIntersectionBeforeToVertex The waypoints from the previous intersection to the interposed vertex
     * @param wayPointsFromVertexToIntersectionAfter The waypoints from he interposed vertex to the next intersection
     * @param intersectionPointBefore The position of the intersection before the interposed vertex
     * @param intersectionPointAfter The position of the intersection after the interposed vertex
     */
    private void splitEdgeWithInterposedVertex(int vertex, SimpleOSMNode vertexPosition, int intersectionBefore, int intersectionAfter, List<SimpleOSMNode> wayPointsFromIntersectionBeforeToVertex, List<SimpleOSMNode> wayPointsFromVertexToIntersectionAfter, SimpleOSMNode intersectionPointBefore, SimpleOSMNode intersectionPointAfter) {
        for (RoadGraph.Edge e : adj(intersectionAfter)) {
            if (e.to() == intersectionBefore) {
                addSingleEdge(new RoadGraph.Edge(vertex, intersectionBefore, (float) EuclideanDistance.dist(model, vertexPosition, intersectionPointBefore), e.getSpeedLimit(), e.getName(), wayPointsFromIntersectionBeforeToVertex, e.getHighway(),
                        e.isOneWay(), e.isWalkingAllowed(), e.isCyclingAllowed(), e.isDrivingAllowed(), e.isRoundabout()), e.getHighway());
                e.setTo(vertex);
                e.setLength((float) EuclideanDistance.dist(model,intersectionPointAfter, vertexPosition));
                e.setWayPoints(wayPointsFromVertexToIntersectionAfter);
                break;
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public static class Edge implements Serializable {
        private int fromVertex;
        private int toVertex;
        private float length;
        private int speedLimit;
        private String name;
        private List<SimpleOSMNode> wayPoints;
        private OSMHighway highway;
        private boolean isOneWay;
        private boolean walkingAllowed;
        private boolean cyclingAllowed;
        private boolean drivingAllowed;
        private boolean isRoundabout;

        /**
         * Initializes the edge with all the relevant information
         * @param fromVertex The vertex the edge goes from
         * @param toVertex The vertex the edge goes to
         * @param length The length of the vertex in kilometers
         * @param speedLimit The speedlimit of the vertex in km/t
         * @param name The name of the vertex
         * @param wayPoints The waypoints to be drawn when the edge is to be drawn
         * @param highway The highway which is partially represented by the ede
         * @param isOneWay If the edge is oneway
         * @param walkingAllowed If walking is allowed along the edge
         * @param cyclingAllowed If cycling is allowed along the edge
         * @param drivingAllowed If driving is allowed along the edge
         * @param isRoundabout If the edge represents a part of a roundabout
         */
        public Edge(int fromVertex, int toVertex, float length, int speedLimit, String name, List<SimpleOSMNode> wayPoints, OSMHighway highway, boolean isOneWay, boolean walkingAllowed,
                    boolean cyclingAllowed, boolean drivingAllowed, boolean isRoundabout) {
            this.fromVertex = fromVertex;
            this.toVertex = toVertex;
            this.length = length;
            this.speedLimit = speedLimit;
            this.name = name;
            this.wayPoints = wayPoints;
            this.highway = highway;
            this.isOneWay = isOneWay;
            this.walkingAllowed = walkingAllowed;
            this.cyclingAllowed = cyclingAllowed;
            this.drivingAllowed = drivingAllowed;
            this.isRoundabout = isRoundabout;
        }

        public float getLength() {
            return length;
        }

        public List<SimpleOSMNode> getWayPoints() {
            return wayPoints;
        }

        public int to() {
            return toVertex;
        }

        public int from() {
            return fromVertex;
        }

        public void setTo(int toVertex) {
            this.toVertex = toVertex;
        }

        public void setFrom(int fromVertex) {
            this.fromVertex = fromVertex;
        }

        public void setLength(float length) {
            this.length = length;
        }

        @Override
        public String toString() {
            return fromVertex +"->"+ toVertex +": "+length+"km @ "+speedLimit+"km/t, "+name+(isOneWay?" is oneway":"")+" - is allowed: "+(cyclingAllowed?"cykler ":"")+(walkingAllowed?"gang ":"")+(drivingAllowed?"biler ":"");
        }

        public int getSpeedLimit() {
            return speedLimit;
        }

        public void setSpeedLimit(int speedLimit) {
            this.speedLimit = speedLimit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isOneWay() {
            return isOneWay;
        }

        public void setOneWay(boolean oneWay) {
            isOneWay = oneWay;
        }

        public void setWayPoints(List<SimpleOSMNode> wayPoints) {
            this.wayPoints = wayPoints;
        }

        public boolean isWalkingAllowed() { return walkingAllowed; }

        public boolean isCyclingAllowed() {
            return cyclingAllowed;
        }

        public boolean isDrivingAllowed() {
            return drivingAllowed;
        }

        public boolean isRoundabout() {
            return isRoundabout;
        }

        public OSMHighway getHighway() {
            return highway;
        }

        public void setHighway(OSMHighway highway) {
            this.highway = highway;
        }
    }
}
