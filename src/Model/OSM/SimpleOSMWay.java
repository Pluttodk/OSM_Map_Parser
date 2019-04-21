package Model.OSM;

import java.util.ArrayList;

/**
 * A class to represent a way (NOT VIZUAL!)
 */
public class SimpleOSMWay extends ArrayList<SimpleOSMNode> {
    /**
     * @return first node in way
     */
    public SimpleOSMNode getFirstNode() {
        return get(0);
    }

    /**
     * @return returns the node at the end of the way
     */
    public SimpleOSMNode getLastNode() {
        return get(size()-1);
    }

}
