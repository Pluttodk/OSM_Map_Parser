package Model;

import Model.OSM.SimpleOSMNode;

import java.awt.geom.Point2D;


/**
 * This class contains all of the nodes that are being parsed
 */
public class LongToPointMap {
	private int MASK;
	public Node[] tab;

	/**
	 * Creates new LongToPoinMap of a fixed size, that later is being used
	 * for creating an array. This array is created with a sized using bitshifting.
	 * This means the size of the array will be in binary 1 shifted to left 'capacity'
	 * times.
	 * @param capacity
	 */
	public LongToPointMap(int capacity) {
		tab = new Node[1 << capacity];
		MASK = tab.length - 1;
	}


	/**
	 * This method adds a new node to a hashed position in an bitshifted array.
	 * This makes it a lot faster to find the node that we are looking for
	 * @param key Key to be hashed after
	 * @param x x-coordinates for the node
	 * @param y y-coordinates for the node
	 */
	public void put(long key, float x, float y) {
		int h = Long.hashCode(key) & MASK;
		tab[h] = new Node(key, x, y, tab[h]);
	}

	/**
	 * Method to get a Coordinates for given key. It searches through
	 * nodes until it finds the right one
	 * @param key key to search after
	 * @return returns a SimpleOSMNode that contains the coordinates
	 * for the chosen Node
	 **/
	public SimpleOSMNode get(long key) {
		for (Node n = tab[Long.hashCode(key) & MASK] ; n != null ; n = n.next) {
			if (n.key == key) return n;
		}
		return null;
	}

	/**
	 * Node class that extends SimpleOSMNode, which helps us
	 * knowing the next node
	 */
	private static class Node extends SimpleOSMNode {
		Node next;
		long key;
		public Node(long key, float x, float y, Node next) {
			super(x, y);
			this.key = key;
			this.next = next;
		}
	}
}
