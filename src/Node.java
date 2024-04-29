/**
 * @file Node.java
 * @summary Implementation of a Chord node, implements INode. This file contains the main
 * logic for the Chord protocol. This includes finding successors, predecessors, and updating
 * finger tables. This file also contains methods for joining remote or local networks, inserting
 * and looking up data. This file is the actual implementation of the DHT.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 2 grace days
 */
package src;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @class Node
 * @summary Implementation of a Chord node.
 */
public class Node implements INode {
    
    // The number of bits in the hash
    protected final int m = 31;
    // The number of nodes in the network
    protected final int mod = (int)Math.pow(2, m);
    // The finger table
    public Finger[] finger = new Finger[m];
    // The local dictionary
    public ConcurrentHashMap<Integer, String> dictionary = new ConcurrentHashMap<Integer, String>();
    
    // The node's id
    public int id;
    // The node's id specified by the user config file
    public String nodeId;
    // The node's url
    protected String url;
    // The node's current predecessor
    protected INode predecessor;
    // The node's current successor
    protected INode successor;
    // Lock for joining the network
    public boolean joinLock = false;

    /**
     * Constructor for the Node class
     * 
     * @param url The url of the node, E.g.: //localhost:8103/Node03
     */
    public Node(String url) {
        // Update config values
        this.url = url;
        this.id = Hash.hash32(url);
        this.nodeId = url.split("/")[url.split("/").length - 1];

        // Set pre/succ to self
        this.predecessor = this;
        this.successor = this;

        // Initialize the finger table with start values
        Printer.print("Creating finger table", this.nodeId);
        for(int i = 0; i < m; i++) {
            int start = modulo31Add(this.id, (int)Math.pow(2, i)) % mod;
            finger[i] = new Finger(start, this);
            Printer.print("Finger[" + i + "]: " + finger[i].toString(), this.nodeId);
        }
    }

    /**
     * Given a word and definition, find the node where this should word
     * should be stored and send a message to that node to insert the word.
     * 
     * @param word The word to insert
     * @param definition The definition of the word
     * @return The id of the node where the word was inserted
     */
    public int insert(String word, String definition) throws RemoteException {
        int key = Hash.hash32(word);
        Printer.print("[insert] Received request to insert word " + word + " with key " + key + " and definition " + definition, this.nodeId);
        INode nPrime = this.findSuccessor(key, false);
        Printer.print("[insert] Forwarding request to key successor " + nPrime.getId(), this.nodeId);
        boolean res = nPrime.insertLocal(key, definition);
        // If successful, return the id of the node where the word was inserted
        if(res) return nPrime.getId();
        else return -1;
    }

    /**
     * Insert a key and definition into the local dictionary of the node.
     * 
     * @param key The key to insert
     * @param definition The definition of the key
     * @return True if the key was successfully inserted, false if a hash collision occurred
     */
    public boolean insertLocal(int key, String definition) throws RemoteException {
        // Hash collision occurred, do not insert the word
        if(this.dictionary.containsKey(key)) {
            Printer.print("[insertLocal] Collision: " + key + " in node " + this.id, this.nodeId);
            return false;
        }
        Printer.print("[insertLocal] Inserting key " + key + " into node " + this.id, this.nodeId);
        this.dictionary.put(key, definition);
        return true;
    }

    /**
     * Given a word, find the node where it is stored (if present)
     * and return its definition. If the word does not exist, return null.
     * 
     * @param word The word to lookup
     * @return The definition of the word if it exists, null otherwise
     */
    public String lookup(String word) throws RemoteException {
        int key = Hash.hash32(word);
        Printer.print("[lookup] Received request to lookup word " + word + " with key " + key, this.nodeId);
        INode nPrime = this.findSuccessor(key, false);
        Printer.print("[lookup] If key exists, it is stored in node " + nPrime.getId(), this.nodeId);
        String res = nPrime.getDictionary().get(key);
        Printer.print("[lookup] Returning definition: " + res, this.nodeId);
        return res;
    }

    /**
     * Move `key` from `this` node to node `s`
     * 
     * @param s The node to move the key to
     * @param key The key to move
     */
    public void moveKey(INode s, int key) throws RemoteException {
        s.getDictionary().put(key, this.dictionary.get(key));
        this.dictionary.remove(key);
    }

    /**
     * Given an id, find the successor node of that id
     * 
     * @param id The id to find the successor of
     * @param traceFlag Whether to print trace information
     * @return The successor node of the given id
     */
    public INode findSuccessor(int id, boolean traceFlag) throws RemoteException {
        Printer.print("[findSuccessor] Finding successor of " + id, this.nodeId);
        INode nPrime = this.findPredecessor(id, traceFlag);
        Printer.print("[findSuccessor] Returning successor: " + nPrime.getSuccessor().getId(), this.nodeId);
        return nPrime.getSuccessor();
    }

    /**
     * Given an id, find the predecessor node of that id
     * 
     * @param id The id to find the predecessor of
     * @param traceFlag Whether to print trace information
     * @return The predecessor node of the given id
     */
    public INode findPredecessor(int id, boolean traceFlag) throws RemoteException {
        Printer.print("[findPredecessor] Finding predecessor of " + id, this.nodeId);
        INode nPrime = this;
        Range r = new Range(nPrime.getId(), false, nPrime.getSuccessor().getId(), true);
        // while (id ∉ (n', n'.successor])
        while(!r.contains(id)) {
            nPrime = nPrime.closestPrecedingFinger(id);
            r = new Range(nPrime.getId(), false, nPrime.getSuccessor().getId(), true);
            if(traceFlag)
                Printer.print("[findPredecessor] New nPrime is " + nPrime.getId(), this.nodeId);
        }
        Printer.print("[findPredecessor] Found predecessor: " + nPrime.getId(), this.nodeId);
        return nPrime;
    }

    /**
     * Given an id, find the closest preceding finger of that id
     * 
     * @param id The id to find the closest preceding finger of
     * @return The closest preceding finger of the given id
     */
    public INode closestPrecedingFinger(int id) throws RemoteException {
        Printer.print("[closestPrecedingFinger] Finding closest preceding finger of " + id, this.nodeId);
        for(int i = m - 1; i >= 0; i--) {
            Range r = new Range(this.id, id);
            // if (finger[i].node ∈ (n, id))
            if(this.finger[i].node != null && r.contains(this.finger[i].node.getId())) {
                Printer.print("[closestPrecedingFinger] Found closest preceding finger: " + this.finger[i].node.getId(), this.nodeId);
                return this.finger[i].node;
            }
        }
        Printer.print("[closestPrecedingFinger] Returning self as closest preceding finger", this.nodeId);
        return this;
    }

    public boolean getJoinLock() {
        return this.joinLock;
    }
    public void setJoinLock(boolean joinLock) {
        this.joinLock = joinLock;
    }

    /**
     * Add node `this` to the network that `estNode` is in. If `estNode` is null,
     * then `this` is the first node in the network - in this case, create a new network.
     * 
     * @param estNode The node to join with, or null if `this` is the first node
     * @return True if the node was successfully added to the network
     */
    public boolean join(INode estNode) throws RemoteException {
        // Join network with estNode
        if(estNode != null) {
            // Update the finger table of self and 
            // other nodes to reflect the new join
            Printer.print("[join] Joining network - updating finger table", this.nodeId);
            this.printFingerTable("[join] Before update");
            this.initFingerTable(estNode);
            this.printFingerTable("[join] After update");
            this.updateOthers();
            Printer.print("[join] Finished updating other finger tables", this.nodeId);

            // Move keys in (predecessor, n] from successor
            INode s = this.getSuccessor();
            INode p = this.getPredecessor();
            Range r = new Range(p.getId(), false, this.getId(), true); // (predecessor, n]
            Printer.print("[join] Moving keys from " + r.toString() + " from successor", this.nodeId);
            for(Integer key : s.getDictionary().keySet()) {
                if(r.contains(key)) {
                    Printer.print("[join] Moving key " + key + " from successor to self", this.nodeId);
                    s.moveKey(this, key);
                }
            }
        }
        // Create new network
        else {
            Printer.print("[join] New network - updating finger table", this.nodeId);
            for(int i = 0; i < m; i++) {
                this.finger[i].node = this;
            }
            this.printFingerTable("[join]");
            this.predecessor = this;
            this.successor = this.finger[0].node;
            this.printPreSucc("[join]");
        }
        return true;
    }

    /**
     * Initialize the finger table of `this` node using estNode to find successors
     * 
     * @param estNode The node to use to find successors
     */
    public void initFingerTable(INode estNode) throws RemoteException {
        // Set the first finger in the table
        this.finger[0].node = estNode.findSuccessor(this.finger[0].start, false);
        this.predecessor = this.getSuccessor().getPredecessor();
        this.getSuccessor().setPredecessor(this);
        this.printPreSucc("[initFingerTable]");

        // Update the rest of the finger table
        for(int i = 0; i < m - 1; i++) {
            Printer.print("[initFingerTable] Updating finger " + (i + 1), this.nodeId);
            Range r = new Range(this.id, true, this.finger[i].node.getId(), false);
            // if (finger[i + 1].start ∈ [n, finger[i].node])
            if(r.contains(this.finger[i + 1].start)) {
                this.finger[i + 1].node = this.finger[i].node;
                Printer.print("[initFingerTable] Setting finger " + (i + 1) + " to " + this.finger[i].node.getId(), this.nodeId);
            }
            else {
                this.finger[i + 1].node = estNode.findSuccessor(this.finger[i + 1].start, false);
                Printer.print("[initFingerTable] Setting finger " + (i + 1) + " to " + this.finger[i + 1].node.getId(), this.nodeId);
            }
        }
    }

    /**
     * Update the finger tables of other nodes in the network
     */
    public void updateOthers() throws RemoteException {
        Printer.print("[updateOthers] Updating other finger tables", this.nodeId);
        for(int i = 0; i < m; i++) {
            int id = (this.id + 1 - (int)Math.pow(2, i)); // n - 2^i, i=0,1,...,m-1
            // If the value wraps counterclockwise around 0 and becomes negative, add
            // the total number of nodes back to it to get the correct value
            if(id < 0) id += mod; 
            INode p = this.findPredecessor(id, false);
            Printer.print("[updateOthers] Clockwise id " + id + " found predecessor " + p.getId(), this.nodeId);
            // No need to update own table - we already did this in initFingerTable
            if(p.getId() != this.id)
                p.updateFingerTable(this, i);
        }
    }

    /**
     * Update a single entry of the finger table for node `this`. Set the finger
     * at index `i` to have a successor node of `s`.
     * 
     * @param s The node to set as the successor
     * @param i The index of the finger table to update
     */
    public void updateFingerTable(INode s, int i) throws RemoteException {
        Range r = new Range(this.id, false, this.finger[i].node.getId(), false);
        // (s ∈ [n, finger[i].node))
        if(r.contains(s.getId())) {
            Printer.print("[updateFingerTable] Setting finger " + i + " to " + s.getId(), this.nodeId);
            this.finger[i].node = s;
            INode p = this.getPredecessor();
            p.updateFingerTable(s, i);
        }
    }

    /**
     * Get the hashed id of the node
     */
    public int getId() {
        return id;
    }

    /**
     * Get the RMI url of the node
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the successor fo the node. This is the first entry in the finger table.
     */
    public INode getSuccessor() {
        return this.finger[0].node;
    }

    /**
     * Update the succssor of the node, and set the first entry in the finger table to the new successor.
     */
    public void setSuccessor(INode node) {
        successor = node;
        finger[0].node = node;
    }

    /**
     * Get the predecessor of the node
     */
    public INode getPredecessor() {
        return predecessor;
    }

    /**
     * Update the predecessor of the node
     */
    public void setPredecessor(INode node) {
        predecessor = node;
    }

    /**
     * Get the finger table of the node
     * @return The finger table of the node
     */
    public Finger[] getFingerTable() {
        return finger;
    }

    /**
     * Get the dictionary of the node
     * @return The dictionary of the node
     */
    public ConcurrentHashMap<Integer, String> getDictionary() {
        return dictionary;
    }

    /**
     * Print the finger table of the node. A message can be passed to 
     * distinguish the output.
     * 
     * @param msg The message to print before the finger table
     */
    public void printFingerTable(String msg) {
        Printer.print(msg + " Node " + this.id + " finger table:", this.nodeId);
        for(Finger f : finger) {
            Printer.print(f.toString(), this.nodeId);
        }
    }

    /**
     * Print the predecessor and successor of the node. A message can be passed to
     * distinguish the output.
     * 
     * @param msg The message to print before the predecessor and successor
     */
    public void printPreSucc(String msg) throws RemoteException {
        Printer.print("Pre/Succ: " + this.predecessor.getId() + " => [Node " + this.id + "] => " + this.getSuccessor().getId(), this.nodeId);
    }

    /**
     * Get the predecessor and successor of the node as a string
     * @return The predecessor and successor of the node as a string
     */
    public String getPreSucc() throws RemoteException {
        return this.predecessor.getId() + " => [Node " + this.id + "] => " + this.getSuccessor().getId();
    }

    /**
     * Print the dictionary of the node
     */
    public void printDictionary() {
        Printer.print("Node " + this.id + " dictionary:", this.nodeId);
        for(Integer key : dictionary.keySet()) {
            Printer.print(key + ": " + dictionary.get(key), this.nodeId);
        }
    }

    /**
     * Add two numbers and return the result modulo 2^31 - 1. This wraps numbers around
     * the circle of nodes rather than overflowing above the number of nodes. In Java, this can
     * also cause negative numbers if you use integers as they do not have unsigned types and use 2 
     * complements.
     * 
     * @param n The first number
     * @param m The second number
     * @return A number bounded between [0, 2^31 - 1]
     */
    public int modulo31Add(int n, int m) {
        int tmp = n + m;
        return (tmp & Integer.MAX_VALUE);
    }

    /**
     * Shutdown the node by unbinding it from the RMI registry and unexporting it.
     */
    public void shutdown() {
        try {
            Naming.unbind(url);
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception e) {
            System.out.println("Error shutting down node: " + e.getMessage());
        }
    }

    /**
     * Main method for the Node class. This method is responsible for parsing 
     * command line arguments, finding the host and port of the node, and starting
     * the node. Then, the node finds the bootstrap node and joins the network.
     * 
     * @param args
     *   <nodeID>: ID of node as specified in the configuration file, e.g.: 06
     *   <configFilepath>: path to the configuration file, e.g.: ./config.txt
     */
    public static void main(String[] args) {
        
        // Validate command line arguments
        if(args.length != 2) {
            System.out.println("Usage: java src/Node <nodeID> <configFilepath>");
            System.exit(1);
        }

        // Parse command line arguments
        String nodeId = args[0];
        String configFilepath = args[1];

        // Load and read from the configuration file
        ConfigStore config = new ConfigStore(configFilepath);
        String thisHost = config.get("node" + nodeId + ".host");
        int thisPort = Integer.parseInt(config.get("node" + nodeId + ".port"));
        // Make sure host and port are present
        if(thisHost == null || thisPort == 0) {
            System.out.println("The configuration file does not contain the required information for node " + nodeId);
        }
        String thisUrl = "//" + thisHost + ":" + thisPort + "/Node" + nodeId;
        Printer.print("Initializing node " + nodeId + " on " + thisUrl, "Node" + nodeId);

        // Initialize the node variables
        INode nodeStub;
        Node node;
        Registry localReg; // Ignore this warning

        // Attempt to start the node and connect to the bootstrap node
        try {
            // Start this node
            node = new Node(thisUrl);
            System.setProperty("java.rmi.server.hostname", thisHost);
            nodeStub = (INode) UnicastRemoteObject.exportObject(node, 0);
            localReg = LocateRegistry.createRegistry(thisPort);
            Naming.bind(thisUrl, nodeStub);
            System.out.println("Node " + nodeId + " running on " + thisUrl + " with id " + node.getId());
            Printer.print("Node " + nodeId + " running on " + thisUrl + " with id " + node.getId(), "Node" + nodeId);

            // Get the configuration for the bootstrap node
            INode bsNode;
            String bsNodeId = config.get("node.bootstrap");
            String bsHost = config.get("node" + bsNodeId + ".host");
            int bsPort = Integer.parseInt(config.get("node" + bsNodeId + ".port"));
            String bsNodeUrl = "//" + bsHost + ":" + bsPort + "/Node" + bsNodeId;
            
            // If this node is the bootstrap node, start a network
            if(bsNodeId.equals(nodeId)) {
                System.out.println("Creating network....");
                node.join(null);
                System.out.println("Network created!");
                Printer.print("Node " + nodeId + " is the bootstrap node, creating network.", "Node" + nodeId);
            }
            // If this node not the bootstrap node, wait until the bootstrap node is available
            else {
                System.out.println("Connecting to " + bsNodeUrl + "...");
                Printer.print("Node " + nodeId + " is not the bootstrap node, connecting to " + bsNodeUrl + "...", "Node" + nodeId);

                // Continuously attempt to connect to the bootstrap node until 
                // it is available, waiting 500ms in between connection attempts.
                while(true) {
                    // Attempt to connect to the bootstrap node
                    try {
                        bsNode = (INode) Naming.lookup(bsNodeUrl);
                        System.out.println("Connected!");
                        Printer.print("Connected to bootstrap node " + bsNode.getId() + ", now joining network.", "Node" + nodeId);
                        node.join(bsNode);
                        System.out.println("Joined network!");
                        Printer.print("Joined network with node running on " + bsNodeUrl + ".", "Node" + nodeId);
                        break;
                    } 
                    // Bootstrap node was not available, wait and try again
                    catch(Exception e) {
                        Thread.sleep(500);
                        continue;
                    }
                }
            }

            // Run node for s seconds - in this case, 5 minutes
            int s = 300;
            System.out.println("Running node for " + s + " seconds.");
            Printer.print("Running node for " + s + " seconds.", "Node" + nodeId);
            Thread.sleep(s * 1000);
            System.out.println("Shutting down node " + nodeId + "..");
            Printer.print("Shutting down node.", "Node" + nodeId);
            node.shutdown();
            Printer.print("Node " + nodeId + " has been shut down successfully.", "Node" + nodeId);
        } 
        // An error occured while trying to start this node.
        catch (Exception e) {
            System.out.println("Error starting node, see the logs for more info.");
            Printer.print("Error starting node: " + e.getMessage(), "Node" + nodeId);
        }
    }

}
