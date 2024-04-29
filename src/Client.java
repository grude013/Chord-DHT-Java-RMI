/**
 * @file Client.java
 * @summary Client class for the dictionary lookup system. Allows the user to lookup a word in the
 * dictionary or insert a new word into the dictionary. The client can also view the structure of the
 * DHT by printing the finger table or dictionary of a node.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 1 grace days
 */
package src;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @class Client
 * @summary Interact with the DHT.
 */
public class Client {

    public static String dataClient(Scanner scanner, INode node) throws RemoteException {
        String option = "";
        while(option != "3") {
            System.out.println("\nData Options:\n  1. Lookup\n  2. Insert\n  3. Back to main menu");
            System.out.print("Enter your choice: ");
            option = scanner.nextLine();
        
            // Lookup a word in the dictionary
            if(option.equals("1")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                String meaning = node.lookup(word);
                // Word not found
                if(meaning == null)
                    System.out.println("Word not found in dictionary.");
                // Word found
                else
                    System.out.println("Result: " + meaning);
            }

            // Insert a new word into the dictionary
            else if(option.equals("2")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                System.out.print("Enter the meaning: ");
                String meaning = scanner.nextLine();
                int key = node.insert(word, meaning);
                if(key == -1)
                    System.out.println("Word exists or collision detected.");
                else
                    System.out.println("Result status: Inserted word " + word + " at node " + key);
            }

            // Exit the client
            else if(option.equals("3")) {
                System.out.println("Exiting data menu...");
                break;
            }

            // Invalid option
            else {
                System.out.println("Invalid option. Please try again.");
            }
        }

        return "return";
    }
    
    public static String structureClient(Scanner scanner, INode node) throws RemoteException {
        String option = "";
        while(option != "4") {
            System.out.println("\nStructure Options:\n  1. Print Network\n  2. Print Finger Table\n  3. Print Dictionary\n  4. Back to main menu");
            System.out.print("Enter your choice: ");
            option = scanner.nextLine();

            // Print nodes in the network
            if(option.equals("1")) {
                INode start = node;
                INode cur = node;
                System.out.println(cur.getPreSucc());
                cur = cur.getSuccessor();
                while(start.getId() != cur.getId()) {
                    System.out.println(cur.getPreSucc());
                    cur = cur.getSuccessor();
                }
            }
        
            // Print the finger table
            else if(option.equals("2")) {
                System.out.print("Enter a node: ");
                int nodeNum = Integer.parseInt(scanner.nextLine());
                INode n = node.findSuccessor(nodeNum, false);

                // If the successor of `nodeNum` is not the node itself, then `nodeNum` is not in the network
                if(n.getId() != nodeNum) {
                    System.out.println("Node " + nodeNum + " not found in the network.");
                    continue;
                }

                // Print the finger table
                System.out.println("Finger Table for Node " + nodeNum + ":");
                Finger[] finger = n.getFingerTable();
                for(Finger f : finger) {
                    System.out.println(f);
                } 
            }

            // Print the dictionary
            else if(option.equals("3")) {
                System.out.print("Enter a node: ");
                int nodeNum = Integer.parseInt(scanner.nextLine());
                INode n = node.findSuccessor(nodeNum, false);

                // If the successor of `nodeNum` is not the node itself, then `nodeNum` is not in the network
                if(n.getId() != nodeNum) {
                    System.out.println("Node " + nodeNum + " not found in the network.");
                    continue;
                }

                // Print the ditionary
                System.out.println("Dictionary for Node " + nodeNum + ":");
                ConcurrentHashMap<Integer, String> dict = n.getDictionary();
                for(Integer key : dict.keySet()) {
                    System.out.println("Key " + key + ": " + dict.get(key));
                }   
                System.out.println("Total keys: " + dict.size());
            }

            // Exit the client
            else if(option.equals("4")) {
                System.out.println("Exiting structure menu...");
                break;
            }

            // Invalid option
            else {
                System.out.println("Invalid option. Please try again.");
            }
        }

        return "return";
    }

    public static void main(String[] args) throws RemoteException {
        
        // Validate command line arguments
        if(args.length != 1) {
            System.out.println("Usage: java Client <nodeUrl>");
            System.exit(1);
        }

        // Parse command line arguments
        String nodeUrl = args[0];
        INode node;

        // Connect to the node
        try {
            node = (INode) java.rmi.Naming.lookup(nodeUrl);
        } catch(Exception e) {
            System.out.println("Error: Unable to connect to node at " + nodeUrl);
            return;
        }

        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        String option = "";
        while(option != "3") {
            System.out.println("\nMenus:\n  1. Data Client\n  2. Structure Client\n  3. Exit");
            System.out.print("Enter your menu: ");
            option = scanner.nextLine();

            switch(option) {
                // Interact with the data client
                case "1":
                    option = dataClient(scanner, node);
                    break;
                // Interact with the structure client
                case "2":
                    option = structureClient(scanner, node);
                    break;
                // Exit the client
                case "3":
                    System.out.println("Exiting...");
                    return;
                // Come back to the main menu
                case "return":
                    break;
                // Invalid option
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        // Cleanup
        scanner.close();
        return;
    }
}