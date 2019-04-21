package Model.KDTree;

import Model.Exceptions.IllegalLanguageException;
import Model.Graph.TravelType;
import Model.Model;
import Model.OSM.WayType;
import Model.Strings.StringHandler;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.*;

public class KDTreeTests {
    private static final String STRING_DIRECTORY = "resources/languages";

    @Before
    public void setLanguage() {
        try {
            Model.setLanguageList(StringHandler.getLanguageList(STRING_DIRECTORY));
        } catch (SAXException | IOException | IllegalLanguageException ignore) {
            //Do nothing
        }
    }

    @Test
    public void TestLeafRoot() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.UNKNOWN);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        assertTrue(testNode instanceof LeafNode);
    }

    @Test
    public void TestNonLeafRoot() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize()+2, WayType.UNKNOWN);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        assertFalse(testNode instanceof LeafNode);
    }

    //Added statement because of test
    @Test(expected = NullPointerException.class)
    public void TestEmptyInput() {
        KDFriendlyShape[] testShapes = createTestShapes(0, WayType.UNKNOWN);
        KDTree.getRoot(testShapes);
    }

    @Test
    public void TestSplitValueAtCreation() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize()*4, WayType.UNKNOWN);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        float valueExpected = (float) Math.floor((testShapes.length-1)/2);
        assertEquals(valueExpected, testNode.getValue(), 0);
    }

    @Test
    public void TestSearchRange() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize()*4, WayType.UNKNOWN);
        KDTreeNode testRoot = KDTree.getRoot(testShapes);
        Point2D botRight = new Point2D.Double(0, 0);
        Point2D topLeft = new Point2D.Double(testShapes.length, testShapes.length);
        EnumMap<WayType, List<KDFriendlyShape>> testMap = KDTree.searchKDTree(new KDSearchRect(botRight, topLeft), testRoot);
        assertNotNull(testMap);
    }

    @Test
    public void TestOutOfAreaMinSearchRange() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize()*4, WayType.UNKNOWN);
        KDTreeNode testRoot = KDTree.getRoot(testShapes);
        Point2D botRight = new Point2D.Double(-1, -1);
        Point2D topLeft = new Point2D.Double(-1, -1);
        EnumMap<WayType, List<KDFriendlyShape>> testMap = KDTree.searchKDTree(new KDSearchRect(botRight, topLeft), testRoot);
        assertNotNull(testMap);
    }

    @Test
    public void TestOutOfAreaMaxSearchRange() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize()*4, WayType.UNKNOWN);
        KDTreeNode testRoot = KDTree.getRoot(testShapes);
        Point2D botRight = new Point2D.Double(testShapes.length+10, testShapes.length+10);
        Point2D topLeft = new Point2D.Double(testShapes.length+10, testShapes.length+10);
        EnumMap<WayType, List<KDFriendlyShape>> testMap = KDTree.searchKDTree(new KDSearchRect(botRight, topLeft), testRoot);
        assertNotNull(testMap);
    }

    @Test
    public void TestNearestRoad() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_MOTORWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(testShapes.length/2, testShapes.length/2);
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.CAR);
        assertEquals(testShapes[testShapes.length/2], nearestTestShape);
    }

    @Test
    public void TestNearestRoadMin() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_MOTORWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(((testShapes.length/2)+.3), ((testShapes.length/2)+.3));
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.CAR);
        assertEquals(testShapes[testShapes.length/2], nearestTestShape);
    }

    @Test
    public void TestNearestRoadMax() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_MOTORWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(((testShapes.length/2)+.8), ((testShapes.length/2)+.8));
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.CAR);
        assertEquals(testShapes[testShapes.length/2+1], nearestTestShape);
    }

    @Test
    public void TestNearestRoadBicycleIllegal() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_MOTORWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(testShapes.length/2, testShapes.length/2);
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.BICYCLE);
        assertEquals(null, nearestTestShape);
    }

    @Test
    public void TestNearestRoadBicycleLegal() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_CYCLEWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(testShapes.length/2, testShapes.length/2);
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.BICYCLE);
        assertEquals(testShapes[testShapes.length/2], nearestTestShape);
    }

    @Test
    public void TestNearestRoadWalkIllegal() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_MOTORWAY);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(testShapes.length/2, testShapes.length/2);
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.WALK);
        assertEquals(null, nearestTestShape);
    }

    @Test
    public void TestNearestRoadWalkLegal() {
        KDFriendlyShape[] testShapes = createTestShapes(KDTree.getLeafArraySize(), WayType.HIGHWAY_PATH);
        KDTreeNode testNode = KDTree.getRoot(testShapes);
        Point2D testSearchPoint = new Point2D.Double(testShapes.length/2, testShapes.length/2);
        KDFriendlyShape nearestTestShape = KDTree.getNearestWay(testSearchPoint, new KDSearchRect(testSearchPoint, testSearchPoint), testNode, TravelType.WALK);
        assertEquals(testShapes[testShapes.length/2], nearestTestShape);
    }

    private KDFriendlyShape[] createTestShapes(int amount, WayType type) {
        KDFriendlyShape[] testShapes = new KDFriendlyShape[amount];
        for (int i = 0 ; i < testShapes.length ; i++) {
            testShapes[i] = new TestShape(i,i);
            testShapes[i].setWayType(type);
        }
        return testShapes;
    }
}