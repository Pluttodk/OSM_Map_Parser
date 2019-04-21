package Model.KDTree;

import Model.OSM.WayType;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Node class for a leaf node in the KDTree which contains an array of shapes to be drawn
 */
public class LeafNode implements KDTreeNode, Serializable {
    private EnumMap<WayType, List<KDFriendlyShape>> shapes;

    public LeafNode() {
        shapes = new EnumMap<>(WayType.class); {
            for (WayType type : WayType.values()) {
                shapes.put(type, new ArrayList<>());
            }
        }
    }

    public LeafNode(KDFriendlyShape[] shapes) {
        this.shapes = new EnumMap<>(WayType.class); {
            for (WayType type : WayType.values()) {
                this.shapes.put(type, new ArrayList<>());
            }
        }
        for (int i = 0 ; i < shapes.length ; i++) {
            this.shapes.get(shapes[i].getWayType()).add(shapes[i]);
        }
    }

    @Override
    public KDTreeNode getLeft() {
        return null;
    }

    @Override
    public KDTreeNode getRight() {
        return null;
    }

    @Override
    public EnumMap<WayType, List<KDFriendlyShape>> getShapes() {
        return shapes;
    }

    @Override
    public float getValue() {
        return Float.MIN_VALUE;
    }

    @Override
    public void add(KDFriendlyShape shape) {
        shapes.get(shape.getWayType()).add(shape);
    }
}
