package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        // Node node6 = new ChordNode("//localhost:8102/Node02");
        // node6.join(node0);

        Node[] nodes = {node0, node1, node3};

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
            n.printFingerTable("");
        }

        // Load the dictionary file
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../dict.txt"));
            String line = reader.readLine();
            while(line != null) {
                String[] parts = line.split(" : ");
                map.put(parts[0], parts[1]);
                node0.insert(parts[0], parts[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading dictionary file: " + e.getMessage());
        }

        int totalWords = 0;
        for(Node n : nodes) {
            n.printDictionary();
            totalWords += n.dictionary.size();
        }
        System.out.println("Total words: " + totalWords);

        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        String option = "";
        while(option != "3") {
            System.out.println("Enter 1 to lookup, 2 to insert a new (key-value) item, or 3 to exit:");
            System.out.print("Enter your choice: ");
            option = scanner.nextLine();

            // Lookup a word in the dictionary
            if(option.equals("1")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                String meaning = node0.lookup(word);
                System.out.println("Result: " + meaning);
            }
            // Insert a new word into the dictionary
            else if(option.equals("2")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                System.out.print("Enter the meaning: ");
                String meaning = scanner.nextLine();
                int key = node0.insert(word, meaning);
                System.out.println("Result status: Inserted word " + word + " at node " + key);
            }
            // Exit the client
            else if(option.equals("3")) {
                System.out.println("Exiting...");
                break;
            }
            // Invalid option
            else {
                System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
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
