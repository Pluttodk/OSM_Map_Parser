package Model.KDTree;

import Model.OSM.WayType;

import java.util.EnumMap;
import java.util.List;

/**
 * Interface for Nodes in the KDTree
 */
public interface KDTreeNode {
    KDTreeNode getLeft();
    KDTreeNode getRight();
    EnumMap<WayType, List<KDFriendlyShape>> getShapes();
    float getValue();
    void add(KDFriendlyShape shape);
}
