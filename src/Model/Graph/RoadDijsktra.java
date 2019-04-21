package Model.Graph;

import Model.*;
import Model.OSM.OSMHighway;
import lib.IndexMinPQ;
import lib.Stack;
import java.util.*;
import java.util.List;

/**
 * An implementation of dijsktra
 */
public class RoadDijsktra {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private RoadGraph.Edge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    private final Model model;
    private RoadGraph G;
    private TravelType travelType;

    /**
     * A method that finds the fastest route from a given start point to an end point
     * @param model a reference to the model
     * @param G a graf that has to be analyzed
     * @param s a given start vertex
     * @param t an end vertex
     * @param travelType which traveltype to take in consideration
     */
    public RoadDijsktra(Model model, RoadGraph G, int s, int t, TravelType travelType) {
        this.model = model;
        this.G = G;
        this.travelType = travelType;

        distTo = new double[G.getVertexCount()];
        edgeTo = new RoadGraph.Edge[G.getVertexCount()];
        G.validateVertex(s);

        for (int v = 0; v < G.getVertexCount(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.getVertexCount());
        double h = h(s,t);
        pq.insert(s, distTo[s] + h);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (RoadGraph.Edge e : G.adj(v)) {
                if (travelType == TravelType.WALK) {
                    if (e.isWalkingAllowed()) {
                        relax(e, t);
                    }
                } else if (travelType == TravelType.BICYCLE) {
                    if (e.isCyclingAllowed()) {
                        relax(e, t);
                    }
                } else {
                    if (e.isDrivingAllowed()) {
                        relax(e, t);
                    }
                }
            }
        }
    }

    /**
     * A method that checks if the turn is allowed (based on restrictions in an intersection)
     * @param nextEdge the edge to turn on to
     * @return either true or false based on if the turn is allowed
     */
    private boolean turnAllowed(RoadGraph.Edge nextEdge) {
        if(edgeTo[nextEdge.from()] != null && edgeTo[nextEdge.from()].getHighway() != null) {
            OSMHighway restrictedFromHighway = edgeTo[nextEdge.from()].getHighway();
            if(restrictedFromHighway.getRestrictedHighways() != null) {
                for (OSMHighway highway : restrictedFromHighway.getRestrictedHighways()) {
                    if(highway.getAssociatedEdges() != null) {
                        for (RoadGraph.Edge f : highway.getAssociatedEdges()) {
                            if (f.equals(nextEdge)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * A method that checks if a new edge weight would be a quicker way to a given vertex
     * @param e edge to be relaxed
     * @param t the end vertex
     */
    private void relax(RoadGraph.Edge e, int t) {
        double edgeWeight;
        boolean turnAllowed = true;
        if (travelType == TravelType.CAR) {
            edgeWeight = e.getLength()/e.getSpeedLimit();
            turnAllowed = turnAllowed(e);

        } else {
            edgeWeight = e.getLength();
        }
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + edgeWeight && turnAllowed) {
            distTo[w] = distTo[v] + edgeWeight;
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w] + h(w,t));
            else                pq.insert(w, distTo[w] + h(w,t));
        }
    }

    /**
     * Gets the heuristic of graph. Used so that it will prioritize roads towards the end point
     * @param w some vertex along the path
     * @param t the end vertex
     * @return the length from w to t
     */
    private double h(int w, int t) {
        if (travelType == TravelType.CAR) {
            return EuclideanDistance.dist(model, G.getIntersectionPoints().get(w), G.getIntersectionPoints().get(t))/TravelType.CAR.getSpeed();
        } else {
            return EuclideanDistance.dist(model, G.getIntersectionPoints().get(w), G.getIntersectionPoints().get(t));
        }
    }

    /**
     * checks if there is a path to a given vertex
     * @param v vertex to check
     * @return true if there is a path to the vertex
     */
    public boolean hasPathTo(int v) {
        G.validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * returns the quickest path to a given vertex
     * @param v vertex to find path to
     * @return an iterable of edges
     */
    public Iterable<RoadGraph.Edge> pathTo(int v) {
        G.validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<RoadGraph.Edge> path = new Stack<RoadGraph.Edge>();
        for (RoadGraph.Edge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
                path.push(e);
        }
        return path;
    }

    public RoadGraph.Edge getPrevEdge(RoadGraph.Edge e) {return edgeTo[e.from()];}

}
