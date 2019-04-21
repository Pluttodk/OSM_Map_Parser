package Model.KDTree;

import Model.Graph.TravelType;
import Model.OSM.WayType;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * This class is used for various methods for a KDTree containing KDFriendlyShapes
 */
public class KDTree {
    private static int size;
    private static final int LEAF_ARRAY_SIZE = 256;

    /**
     * This method creates a KDTree from an array of KDFriendlyShapes by recursively dividing the shapes by the middle
     * and going down the tree until the hasReachedLeaf method returns true.
     * It finds the middle index or median index and uses the QuickSelect Algorithm to find the the shape that would
     * be sorted to the middle of the array.
     * The shape returned by the quickselect method will be the splitShape and its center coordinate will be used as
     * the roots splitValue
     * @param shapes is the array of KDFriendlyShapes that to be stored the in the KDTree
     * @return The Root of the created KDTree
     */
    public static KDTreeNode getRoot(KDFriendlyShape[] shapes) {
        if (shapes.length == 0) throw new NullPointerException("Shapes given to KDTree construction is of length 0");
        size = 1;
        int lo = 0, hi = shapes.length-1;
        if (hasReachedLeaf(lo, hi)) return new LeafNode(getArrayCopy(shapes, hi - lo+1, lo));
        int medianIndex =  lo + (int) Math.floor((hi-lo)/2);

        KDFriendlyShape splitShape = QuickSelect.quickselect(shapes, lo, hi, medianIndex, true); //Find the median shape

        assert splitShape != null;
        float splitValue = (float)splitShape.getCenter(true); //Define the splitValue as the splitShapes center coord

        KDTreeNode left = getNode(shapes, lo, medianIndex, false); //Recursively go down the left child
        KDTreeNode right = getNode(shapes, medianIndex+1, hi, false); //Recursively go down the right child
        checkIntersectShapes(shapes, left, right, splitValue); //Find which leafs the nodes should be placed in
        size++;
        return new KDNode(left, right, splitValue);
    }

    /**
     * This method checks if the difference between hi and lo is lower than or equal to the LEAF_ARRAY_SIZE
     * @param lo is the lowest index integer
     * @param hi is the highest index integer
     * @return True if the difference between hi and lo is lower than or equal to LEAF_ARRAY_SIZE otherwise false
     */
    private static boolean hasReachedLeaf(int lo, int hi) {
        return hi - lo <= LEAF_ARRAY_SIZE;
    }

    /**
     * This method is called recursively to create a KDTree by dividing the shapes by the middle
     * and going down the tree until the hasReachedLeaf method returns true.
     * It finds the middle index or median index and uses the QuickSelect Algorithm to find the the shape that would
     * be sorted to the middle of the array.
     * The shape returned by the quickselect method will be the splitShape and its center coordinate will be used as
     * the roots splitValue.
     * @param shapes is the array of KDFriendlyShapes that to be stored the in the KDTree
     * @param lo is the lowest index
     * @param hi is the highest index
     * @param sortX is a boolean which if it is true means that the current node should be sorted by the x coords, if false it should be sorted by the y coords.
     * @return A KDTreeNode which could be either a KDNode or A a LeafNode
     */
    private static KDTreeNode getNode(KDFriendlyShape[] shapes, int lo, int hi, boolean sortX) {
        if (hasReachedLeaf(lo, hi)) {
            size++;
            return new LeafNode();
        }
        int medianIndex =  lo + (int) Math.floor((hi-lo)/2);

        KDFriendlyShape splitShape = QuickSelect.quickselect(shapes, lo, hi, medianIndex, sortX);

        assert splitShape != null;
        float splitValue = (float)splitShape.getCenter(sortX);
        KDTreeNode left = getNode(shapes, lo, medianIndex, !sortX);
        KDTreeNode right = getNode(shapes, medianIndex+1, hi, !sortX);
        size++;
        return new KDNode(left, right, splitValue);
    }

    /**
     * This method iterates through an array of KDFriendlyShapes and checks for every shape if it intersects with the
     * roots split coordinate and if not, if it is to the left of the coord or to the right.
     * The method will call the checkIntersectShapes recursively until every shape has been placed in the KDTrees leafs
     * @param shapes is an the array of shapes to be iterated through
     * @param left is the left child node of the root
     * @param right is the right child node of the root
     * @param splitValue is the split coordinate of the root
     */
    private static void checkIntersectShapes(KDFriendlyShape[] shapes, KDTreeNode left, KDTreeNode right, float splitValue) {
        for (int i = 0 ; i < shapes.length ; i++) {
            if (intersectsSplit(shapes[i], splitValue, true)) {
                checkIntersectShapes(shapes[i], left, false);
                checkIntersectShapes(shapes[i], right, false);
            } else if (checkIfLeft(shapes[i], splitValue, true)) {
                checkIntersectShapes(shapes[i], left, false);
            } else {
                checkIntersectShapes(shapes[i], right, false);
            }
        }
    }

    /**
     * This method checks for a KDFriendlyShape if it intersects with the
     * current nodes split coordinate and if not, if it is to the left of the coord or to the right.
     * If the split value is 0 then it will have reached a leaf and the shape will be placed into that leaf.
     * @param shape is the KDFriendly shape being checked
     * @param node is the KDTreeNode which value is used as a split coordinate for the shape to check
     * @param sortX is a boolean which if it is true check for the split x coordinate and if false for the split y coordinate
     */
    private static void checkIntersectShapes(KDFriendlyShape shape, KDTreeNode node, boolean sortX) {
        float splitValue = node.getValue();
        if (splitValue != Float.MIN_VALUE) {
            if (intersectsSplit(shape, splitValue, sortX)) {
                checkIntersectShapes(shape, node.getLeft(), !sortX);
                checkIntersectShapes(shape, node.getRight(), !sortX);
            } else if (checkIfLeft(shape, splitValue, sortX)) {
                checkIntersectShapes(shape, node.getLeft(), !sortX);
            } else {
                checkIntersectShapes(shape, node.getRight(), !sortX);
            }
        } else {
            node.add(shape);
        }
    }

    /**
     * This method checks if shape intersects with the splitValue.
     * It does this by creating either a vertical or horizontal line depending on the sortX boolean and check if
     * the shape intersects with the line.
     * @param shape is the shape to check with
     * @param splitValue is the current split coordinate which can be either an x or y coordinate
     * @param sortX is a boolean which if it is true create a vertical line and if false create a horizontal
     * @return true if the shape intersects and false otherwise
     */
    private static boolean intersectsSplit(KDFriendlyShape shape, float splitValue, boolean sortX) {
        Line2D line;
        if (sortX){
            line = new Line2D.Float(splitValue,-1000,splitValue,1000);
        }else{
            line = new Line2D.Float(-1000, splitValue ,1000,splitValue);
        }
        return shape.getBounds2D().intersectsLine(line);
    }

    /**
     * This method checks if the shape is on the left site of the current split value.
     * @param shape is the current shape being checked
     * @param splitValue is the current split value used to check the shapes position
     * @param sortX is a boolean which determines whether the method should look at the x coordinate or the y
     *              coordinate of the split value
     * @return it returns true if the shape is to the left of the split value and false if not to the left
     */
    private static boolean checkIfLeft(KDFriendlyShape shape, float splitValue, boolean sortX) {
        if (sortX) {
            return shape.getBounds2D().getMinX() < splitValue;
        } else {
            return shape.getBounds2D().getMinY() < splitValue;
        }
    }

    /**
     * This method searches the KDTree for which KDLeafs are in range of the searchRectangle.
     * It is the start method for the recursively called method search().
     * @param searchRect is the search rectangle used for defining which area the method should search for leafs
     * @param root is the root of the KDTree
     * @return a enumMap with key = WayType and value = a collection of KDFriendlyShapes
     */
    public static EnumMap<WayType,List<KDFriendlyShape>> searchKDTree(KDSearchRect searchRect, KDTreeNode root) {
        EnumMap<WayType, List<KDFriendlyShape>> shapesToReturn = new EnumMap<>(WayType.class); {
            for (WayType type : WayType.values()) {
                shapesToReturn.put(type, new ArrayList<>());
            }
        }
        search(searchRect, root, true, shapesToReturn);
        return shapesToReturn;
    }

    /**
     * This method searches the KDTree for leafs within the searchRectangle area and adds the shapes contained in the
     * leaf node if one is reached, otherwise the search is continued recursively
     * @param searchRect is the search rectangle used for defining which area the method should search for leafs
     * @param node is the current KDTreeNode the search is at
     * @param isX is a boolean that if true means that the search is based on an x coordinate split value and otherwise
     *            on an y coordinate split value
     * @param shapesToReturn is the EnumMap which the searchKDTree() method returns when recursion if done
     */
    private static void search(KDSearchRect searchRect, KDTreeNode node, boolean isX,
                               EnumMap<WayType,List<KDFriendlyShape>> shapesToReturn) {
        float value = node.getValue();
        if (value == Float.MIN_VALUE) {
            for (WayType type : WayType.values()) {
                shapesToReturn.get(type).addAll(node.getShapes().get(type));
            }
            return;
        }
        if (isX){
            if (value > searchRect.getBotLeft().getX()) {
                search(searchRect, node.getLeft(), !isX, shapesToReturn);
            }
            if (value < searchRect.getTopRight().getX()) {
                search(searchRect, node.getRight(), !isX, shapesToReturn);
            }
        } else {
            if (value > searchRect.getBotLeft().getY()) {
                search(searchRect, node.getLeft(), !isX, shapesToReturn);
            }
            if (value < searchRect.getTopRight().getY()) {
                search(searchRect, node.getRight(), !isX, shapesToReturn);
            }
        }
    }

    /**
     * @return the number of KDNodes in the KDTree
     */
    public static int getSize() {
        return size;
    }

    public static int getLeafArraySize() {
        return LEAF_ARRAY_SIZE;
    }

    /**
     * This method copies an array of KDFriendlyShapes
     * @param shapes is the KDFriendlyShapes array
     * @param length is what the length of the new copied array should be
     * @param lo is the lowest index
     * @return a copy of the array with the length of the length parameter
     */
    private static KDFriendlyShape[] getArrayCopy(KDFriendlyShape[] shapes, int length, int lo) {
        KDFriendlyShape[] arrayToReturn = new KDFriendlyShape[length];
        System.arraycopy(shapes, lo, arrayToReturn, 0, length);
        return arrayToReturn;
    }

    /**
     * This method returns the nearest way to a query point if the way is a WayType that start starts with HIGHWAY and
     * has a name. It does this by using the searchKDTree method and finding the leaf wherein the queryPoint is placed
     * at and adding the shapes who fulfills the requirements to a list which is sorted towards the shortest distance
     * to the queryPoint first.
     * @param queryPoint is the point to find the shortest length towards
     * @param root is the root of the KDTree
     * @return it returns the KDFriendlyShape closest to the queryPoint
     */
    public static KDFriendlyShape getNearestWayWithName(Point2D queryPoint, KDTreeNode root) {
        EnumMap<WayType, List<KDFriendlyShape>> map = searchKDTree(new KDSearchRect(queryPoint, queryPoint), root);
        List<KDFriendlyShape> ways = new ArrayList<>();
        for (WayType type : WayType.values()) {
            if (type.toString().startsWith("HIGHWAY")) {
                for (KDFriendlyShape shape : map.get(type)) {
                    if (shape.getName() != null) {
                        ways.add(shape);
                    }
                }
            }
        }
        if (ways.size() == 0) {
            return null;
        }
        ways.sort(new KDShapeDistanceComparator(queryPoint));
        return ways.get(0);
    }

    /**
     * This method returns the nearest way to a query point if the way is a WayType that start starts with HIGHWAY and
     * is not a illegal road compared to the travelType. Like if the travelType is a car then you cannot drive on bicycle
     * paths.
     * It does this by using the searchKDTree method and finding the leaf wherein the queryPoint is placed
     * at and adding the shapes who fulfills the requirements to a list which is sorted towards the shortest distance
     * to the queryPoint first.
     * @param queryPoint is the point to find the shortest length towards
     * @param root is the root of the KDTree
     * @param travelType is the type of travel the search is based upon
     * @return it returns the KDFriendlyShape closest to the queryPoint
     */
    public static KDFriendlyShape getNearestWay(Point2D queryPoint, KDSearchRect searchRect, KDTreeNode root, TravelType travelType) {
        EnumMap<WayType, List<KDFriendlyShape>> map = searchKDTree(searchRect, root);
        List<KDFriendlyShape> ways = new ArrayList<>();
        for (WayType type : WayType.values()) {
            if (type.toString().startsWith("HIGHWAY") && !isIllegalType(type, travelType)) {
                ways.addAll(map.get(type));
            }
        }
        if (ways.size() == 0) {
            try {
                return getNearestWay(queryPoint, increaseSearchRange(searchRect), root, travelType);
            } catch (StackOverflowError e) {
                return null;
            }
        }
        ways.sort(new KDShapeDistanceComparator(queryPoint));
        return ways.get(0);
    }

    /**
     * This method increases the size of a KDSearchRect by 0.2 in each direction
     * @param oldRect the old KDSearchRectangle
     * @return the new and bigger KDSearchRectangle
     */
    private static KDSearchRect increaseSearchRange(KDSearchRect oldRect) {
        return new KDSearchRect(new Point2D.Double(oldRect.getBotLeft().getX()-0.2, oldRect.getBotLeft().getY()+0.2)
                , new Point2D.Double(oldRect.getTopRight().getX()+0.2, oldRect.getTopRight().getY()-0.2));
    }

    /**
     * This method is used to detect illegal ways for the given travelType
     * @param type is what type of WayType the way is
     * @param travelType is the type of travel that the check is based upon
     * @return it returns true if an illegal type is detected and false otherwise
     */
    private static boolean isIllegalType(WayType type, TravelType travelType) {
        switch (travelType) {
            case BICYCLE:
                switch (type) {
                    case HIGHWAY_MOTORWAY: return true;
                    case HIGHWAY_MOTORWAY_LINK: return true;
                }
                break;
            case CAR:
                switch (type) {
                    case HIGHWAY_PATH: return true;
                    case HIGHWAY_FOOTWAY: return true;
                    case HIGHWAY_STEPS: return true;
                    case HIGHWAY_CYCLEWAY: return true;
                }
                break;
            case WALK:
                switch (type) {
                    case HIGHWAY_MOTORWAY: return true;
                    case HIGHWAY_MOTORWAY_LINK: return true;
                    case HIGHWAY_TRUNK: return true;
                    case HIGHWAY_TRUNK_LINK: return true;
                }
                break;
        }
        return false;
    }

    /**
     * This method finds the point on a way which is the nearest point to the queryPoint
     * @param queryPoint is the point to find the nearest point to
     * @param root is the root of the KDTree
     * @param travelType is the type of travel the search is based upon
     * @return it returns the point on a way nearest to the queryPoint
     */
    public static Point2D getNearestWayAsPoint(Point2D queryPoint, KDTreeNode root, TravelType travelType) {
        KDFriendlyShape shape = getNearestWay(queryPoint, new KDSearchRect(queryPoint, queryPoint), root, travelType);
        return shape.nearestPoint(queryPoint);
    }

    /**
     * Is a comparator used to compare KDFriendlyShapes distance to a queryPoint in nondecreasing order
     */
    private static class KDShapeDistanceComparator implements Comparator<KDFriendlyShape> {
        private Point2D queryPoint;

        public KDShapeDistanceComparator(Point2D point) {
            queryPoint = point;
        }

        @Override
        public int compare(KDFriendlyShape o1, KDFriendlyShape o2) {
            if (o1.distTo(queryPoint) < o2.distTo(queryPoint)) {
                return -1;
            } else if (o1.distTo(queryPoint) > o2.distTo(queryPoint)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
