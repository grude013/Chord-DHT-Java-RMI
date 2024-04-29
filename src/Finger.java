/**
 * @file Finger.java
 * @summary Contains the Finger class. This class is used to represent an 
 * entry in the finger table of a node.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 1 grace day
 */
package src;

import java.io.Serializable;


/**
 * @class Finger
 * @summary Represents an entry in the finger table of a node.
 */
public class Finger implements Serializable {

    // The start of the finger table entry, start + 2^i
    public int start;
    // The successor node for the entry, successor(start + 2^i)
    public INode node;

    /**
     * Constructor for Finger
     * @param start The start of the finger table entry
     * @param node The successor node for the entry
     */
    public Finger(int start, INode node) {
        this.start = start;
        this.node = node;
    }

    /**
     * Return a string representation of the finger.
     * This is simply: start | node
     */
    public String toString() {
        String str = start + "";

        // Print 12 characters for start so that the table is aligned
        for(int i = 0; i < 12 - (start + "").length(); i++) {
            str += " ";
        }
        try { str += "|  " + (node == null ? "null" : node.getId()); } catch (Exception e) {}

        return str;
    }
}
