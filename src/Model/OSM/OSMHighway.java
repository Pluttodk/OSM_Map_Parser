package Model.OSM;

import Model.Graph.RoadGraph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent all roads
 */
public class OSMHighway extends OSMWay {
    private boolean isOneWay;
    private int speedLimit;
    private boolean drivingAllowed;
    private boolean walkingAllowed;
    private boolean cyclingAllowed;
    private boolean isRoundabout;
    private List<OSMHighway> restrictedHighways;
    private List<RoadGraph.Edge> associatedEdges;

    /**
     * A constructor that initialize it as a way
     * @param way to be convertesd to a way
     */
    public OSMHighway(List<SimpleOSMNode> way) {
        super(way);
    }


    public void setOneWay() {
        isOneWay = true;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public void setSpeedLimit(int speedLimit){
        this.speedLimit = speedLimit;
    }

    public int getSpeedLimit(){
        return speedLimit;
    }

    public boolean isWalkingAllowed() {
        return walkingAllowed;
    }

    public boolean isCyclingAllowed() {
        return cyclingAllowed;
    }

    public boolean isDrivingAllowed() {
        return drivingAllowed;
    }

    public void setWalkingAllowed(boolean walkingAllowed) {
        this.walkingAllowed = walkingAllowed;
    }

    public void setCyclingAllowed(boolean cyclingAllowed) {
        this.cyclingAllowed = cyclingAllowed;
    }

    public void setDrivingAllowed(boolean drivingAllowed) {
        this.drivingAllowed = drivingAllowed;
    }

    public boolean isRoundabout() {
        return isRoundabout;
    }

    public void setIsRoundabout(boolean isRoundabout) {
        this.isRoundabout = isRoundabout;
    }

    public List<OSMHighway> getRestrictedHighways() {
        return restrictedHighways;
    }

    public void addRestrictedHighway(OSMHighway highway) {
        if (restrictedHighways == null) restrictedHighways = new ArrayList<>();
        restrictedHighways.add(highway);
    }

    public List<RoadGraph.Edge> getAssociatedEdges() {
        return associatedEdges;
    }

    public void addAssociatedEdge(RoadGraph.Edge edge) {
        if (associatedEdges == null) associatedEdges = new ArrayList<>();
        associatedEdges.add(edge);
    }
}
