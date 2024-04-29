/**
 * @file Range.java
 * @summary Contains the Range class. This class is used to represent a range of integers.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 1 grace day
 */
package src; 

/**
 * @class Range
 * @summary Represents a range of integers. Provides support for inclusion and exclusion of
 * the lower and upper bounds.
 */
public class Range {

    // Lower bound of the range
    private int lower;
    // Flag to include lower bound
    private boolean lowerIncluded;

    // Upper bound of the range
    private int upper;
    // Flag to include upper bound
    private boolean upperIncluded;
    

    /**
     * Constructor for Range - default to exclusive bounds
     * @param lower The lower bound of the range
     * @param upper The upper bound of the range
     */
    public Range(int lower, int upper) {
        this.lower = lower;
        this.lowerIncluded = false;
        this.upper = upper;
        this.upperIncluded = false;
    
    }

    /**
     * Constructor for Range - specify inclusion of bounds
     * @param lower The lower bound of the range
     * @param lowerIncluded Flag to include the lower bound
     * @param upper The upper bound of the range
     * @param upperIncluded Flag to include the upper bound
     */
    public Range(int lower, boolean lowerIncluded, int upper, boolean upperIncluded) {
        this.lower = lower;
        this.lowerIncluded = lowerIncluded;
        this.upper = upper;
        this.upperIncluded = upperIncluded;
    }

    /**
     * Check if a value is contained within the range
     * 
     * @param value The value to check
     * @return True if the value is in the range, false otherwise
     */
    public boolean contains(int value) {
        // Standard range, e.g.: (1, 10)
        if(lower < upper) {
            if(lowerIncluded && upperIncluded) 
                return value >= lower && value <= upper;
            else if(lowerIncluded) 
                return value >= lower && value < upper;
            else if(upperIncluded)
                return value > lower && value <= upper;
            else
                return value > lower && value < upper;
        }
        // Equal bounds, e.g.: (1, 1) or [0, 0]
        else if(lower == value && upper == value)
            return lowerIncluded && upperIncluded;
        else if(lower == value)
            return lowerIncluded;
        else if(upper == value)
            return upperIncluded;
        // Modulo rollover, e.g.: (8, 3)
        else {
            if(lowerIncluded && upperIncluded)
                return value >= lower || value <= upper;
            else if(lowerIncluded)
                return value >= lower || value < upper;
            else if(upperIncluded)
                return value > lower || value <= upper;
            else 
                return value > lower || value < upper;
        }
    }

    /**
     * Return a string representation of the range. Uses [] if a bound is inclusive and () otherwise.
     * @return String representation of the range, e.g.: [7, 18)
     */
    public String toString() {
        return (lowerIncluded ? "[" : "(") + lower + ", " + upper + (upperIncluded ? "]" : ")");
    }
}
