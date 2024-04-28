package src;

public class Finger {
    public int start;
    public INode node;

    public Finger() {
        this.start = 0;
        this.node = null;
    }

    public Finger(int start, INode node) {
        this.start = start;
        this.node = node;
    }

    public String toString() {
        String str = start + "";

        for(int i = 0; i < 3 - (start + "").length(); i++) {
            str += " ";
        }
        try { str += "|  " + (node == null ? "null" : node.getId()); } catch (Exception e) {}

        return str;
    }
}
