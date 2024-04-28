package src;

public class Range {
    private int lower;
    private boolean lowerIncluded;
    private int upper;
    private boolean upperIncluded;

    public Range(int lower, int upper) {
        this.lower = lower;
        this.lowerIncluded = false;
        this.upper = upper;
        this.upperIncluded = false;
    
    }

    public Range(int lower, boolean lowerIncluded, int upper, boolean upperIncluded) {
        this.lower = lower;
        this.lowerIncluded = lowerIncluded;
        this.upper = upper;
        this.upperIncluded = upperIncluded;
    }

    public boolean contains(int value) {
        // Standard range
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
        else if(lower == value && upper == value)
            return lowerIncluded && upperIncluded;
        else if(lower == value)
            return lowerIncluded;
        else if(upper == value)
            return upperIncluded;
        // Modulo rollover
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

    public String toString() {
        return (lowerIncluded ? "[" : "(") + lower + ", " + upper + (upperIncluded ? "]" : ")");
    }

    public static void main(String[] args) {
        Range r = new Range(1, 10);
        System.out.println("5 in (1, 10): " + r.contains(5));
        System.out.println("1 in (1, 10): " + r.contains(1));
        System.out.println("10 in (1, 10): " + r.contains(10));
        System.out.println("0 in (1, 10): " + r.contains(0));
        System.out.println();

        Range r2 = new Range(3, true, 5, true);
        System.out.println("5 in [3, 5]: " + r2.contains(5));
        System.out.println("3 in [3, 5]: " + r2.contains(3));
        System.out.println("4 in [3, 5]: " + r2.contains(4));
        System.out.println("6 in [3, 5]: " + r2.contains(6));
        System.out.println();

        Range r3 = new Range(8, 3);
        System.out.println("5 in (8, 3): " + r3.contains(5));
        System.out.println("8 in (8, 3): " + r3.contains(8));
        System.out.println("3 in (8, 3): " + r3.contains(3));
        System.out.println("10 in (8, 3): " + r3.contains(9));
        System.out.println("1 in (8, 3): " + r3.contains(1));
        System.out.println();

        Range r4 = new Range(0, 0);
        System.out.println("0 in (0, 0): " + r4.contains(0));
        System.out.println("1 in (0, 0): " + r4.contains(1));
        System.out.println();

        Range r5 = new Range(5, false, 5, false);
        System.out.println("5 in [5, 5): " + r5.contains(0));
        System.out.println("4 in [5, 5): " + r5.contains(1));
    }
}
