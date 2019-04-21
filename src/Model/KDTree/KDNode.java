package Model.KDTree;

import Model.OSM.WayType;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;

/**
 * node class for KDTree with left and right children and a splitValue
 */
public class KDNode implements KDTreeNode, Serializable {
    private KDTreeNode left, right;
    private float value;

    public KDNode(KDTreeNode left, KDTreeNode right, float value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    @Override
    public KDTreeNode getLeft() {
        return left;
    }

    @Override
    public KDTreeNode getRight() {
        return right;
    }

    @Override
    public EnumMap<WayType, List<KDFriendlyShape>> getShapes() {
        return null;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void add(KDFriendlyShape shape) {

    }
}
