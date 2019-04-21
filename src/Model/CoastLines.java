package Model;

import Model.KDTree.KDFriendlyShape;

import java.util.ArrayList;

/**
 * A class that contains the coastlines and lonfactor
 */
public class CoastLines extends ArrayList<KDFriendlyShape> {
    float lonfactor;

    /**
     * A constructor that sets the lonfactor of the map
     * @param lonfactor
     */
    public CoastLines(float lonfactor) {
        this.lonfactor = lonfactor;
    }
}
