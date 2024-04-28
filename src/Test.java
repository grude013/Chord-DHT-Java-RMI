package src;

import java.rmi.RemoteException;

public class Test {
    public static void main(String[] args) throws RemoteException {
        // Create network with node 0
        Node node0 = new BootstrapNode("//localhost:8103/Node03");
        node0.join(null);

        // Create chord nodes
        Node node1 = new ChordNode("//localhost:8167/Node08");
        node1.join(node0);
        // Node node2 = new ChordNode("//localhost:8812/Node08");
        // node2.join(node0);
        Node node3 = new ChordNode("//localhost:8193/Node07");
        node3.join(node0);
        Node node6 = new ChordNode("//localhost:8102/Node02");
        node6.join(node0);

        Node[] nodes = {node0, node1, node3, node6};

        // Update Node0 finger table
        // node0.fingerTable[0].setNode(node1);
        // node0.fingerTable[1].setNode(node3);
        // node0.fingerTable[2].setNode(node0);
        // node0.setSuccessor(node1);
        // node0.setPredecessor(node3);

        // // Update Node1 finger table
        // node1.fingerTable[0].setNode(node3);
        // node1.fingerTable[1].setNode(node3);
        // node1.fingerTable[2].setNode(node0);
        // node1.setSuccessor(node3);
        // node1.setPredecessor(node0);

        // // Update Node3 finger table
        // node3.fingerTable[0].setNode(node0);
        // node3.fingerTable[1].setNode(node0);
        // node3.fingerTable[2].setNode(node0);
        // node3.setSuccessor(node0);
        // node3.setPredecessor(node1);

        // Print all finger tables
        System.out.println();
        for(Node n : nodes) {
            n.printFingerTable("Test.java");
        }

        // Test closest preceding finger
        // for(int key = 0; key < 8; key++) {
        //     Node closest = (Node) node0.closestPrecedingFinger(key);
        //     System.out.println("[Node " + node0.getId() + "] Closest preceding finger of " + key + " is " + closest.getId());
        // }

        // Test find predecessor
        // for(int key = 0; key < 8; key++) {
        //     Node predecessor = (Node) node0.findPredecessor(key, false);
        //     System.out.println("Predecessor of " + key + " is " + predecessor.getId());
        // }
        
        // Test find successor
        // for(int key = 0; key < 8; key++) {
        //     Node successor = (Node) node3.findSuccessor(key, false);
        //     System.out.println("Successor of " + key + " is " + successor.getId());
        // }
    }
}
