package src;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class ONode implements INode {

    protected final int m = 3;
    protected ConcurrentHashMap<String, String> localDict = new ConcurrentHashMap<String, String>();
    public Finger[] fingerTable = new Finger[m];

    protected int id;
    protected String nodeId;
    protected String url;
    protected INode successor;
    protected INode predecessor;

    /**
     * Constructor for the Node class
     * 
     * @param url The url of the node
     */
    public ONode(String url) {
        // Generate a unique id for this node from the node URL
        this.url = url;
        this.id = Hash.hash32(url);
        this.nodeId = url.split("/")[url.split("/").length - 1] + " (" + id + ")";

        this.predecessor = this;
        this.successor = this;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean isBetween(int inputValue, int lowerBound, int upperBound, boolean includeLower, boolean includeUpper) {
        if (includeLower && includeUpper) {
            if (lowerBound >= upperBound) {
              //looping through 0
              return inputValue >= lowerBound || inputValue <= upperBound;
            } else {
              return inputValue >= lowerBound && inputValue <= upperBound;
            }
        } else if (includeLower && !includeUpper) {
            if (lowerBound >= upperBound) {
              //looping through 0
              return inputValue >= lowerBound || inputValue < upperBound;
            } else {
              return inputValue >= lowerBound && inputValue < upperBound;
            }
        } else if (!includeLower && includeUpper) {
            if (lowerBound >= upperBound) {
              //looping through 0
              return inputValue > lowerBound || inputValue <= upperBound;
            } else {
              return inputValue > lowerBound && inputValue <= upperBound;
            }
        } else {
            if (lowerBound >= upperBound) {
              //looping through 0
              return inputValue > lowerBound || inputValue < upperBound;
            } else {
              return inputValue > lowerBound && inputValue < upperBound;
            }
        }
    }   

    /**
     * Find the successor of an id/key
     * 
     * Psuedocode:
     * --------------
     *  n.find_successor(id)
     *    n' = n.find_predecessor(id)
     *    return n'.successor
     */
    public INode findSuccessor(int key, boolean traceFlag) throws RemoteException {
        INode nPrime = findPredecessor(key, traceFlag);
        // System.out.println("FS: predecessor of " + key + " is " + nPrime.getId() + " with successor " + nPrime.getSuccessor().getId());
        System.out.println("Node " + nPrime.getId() + " finger table:");
        for(Finger f : nPrime.getFingerTable()) {
            System.out.println(f);
        }
        return nPrime.getSuccessor();
    }

    /**
     * Find the predecessor of an id/key
     * 
     * Psuedocode:
     * --------------
     *  n.find_predecessor(id)
     *    n' = n
     *    while id not in (n, n.successor]
     *      n' = n.closest_preceding_finger(id)
     *    return n'
     */
    public INode findPredecessor(int key, boolean traceFlag) throws RemoteException {
        INode nPrime = this;
        int id = key;
        int nPrimeId = nPrime.getId();
        int nPrimeSuccessorId = nPrime.getSuccessor().getId();

        // System.out.println("while " + id + " not in (" + nPrimeId + ", " + nPrimeSuccessorId + "]");
        while(!isBetween(id, nPrimeId, nPrimeSuccessorId, false, true)) {

            // System.out.println("if " + nPrimeSuccessorId + " < " + nPrimeId);
            if(nPrimeSuccessorId < nPrimeId) {
                // System.out.println("if " + id + " in (" + nPrimeId + ", " + nPrimeSuccessorId + "]");
                if(id > nPrimeId || id <= nPrimeSuccessorId) {
                    // System.out.println("return nPrime");
                    return nPrime;
                }
            }

            // System.out.println("nPrime before: " + nPrime.getId());
            nPrime = nPrime.closestPrecedingFinger(id);
            // System.out.println("nPrime after: " + nPrime.getId());
            nPrimeId = nPrime.getId();
            nPrimeSuccessorId = nPrime.getSuccessor().getId();
            System.out.println("while " + id + " not in (" + nPrimeId + ", " + nPrimeSuccessorId + "]");
        }
        return nPrime;
    }

    /**
     * Find the closest proceeding finger of an id/key
     * 
     * Psuedocode:
     * --------------
     *  n.closest_proceeding_finger(id)
     *    for i = m downto 1
     *      if finger[i].node in (n, id)
     *          return finger[i].node
     *    return n
     */
    public INode closestPrecedingFinger(int key) throws RemoteException {
        for(int i = m - 1; i >= 0; i--) {
            int fingerId = fingerTable[i].node.getId();
            
            // System.out.println("CPF: if " + fingerId + " in (" + this.getId() + ", " + key + ")");
            if(isBetween(fingerId, this.getId(), key, false, false)) {
                System.out.println("CPF: return " + fingerId);
                return fingerTable[i].node;
            }
        }
        return this;
    }

    public boolean join(INode nPrime) throws RemoteException {
        // Node `this` is joining the network with node `nPrime`
        if(nPrime != null) {
            System.out.println("Node " + this.id + " joining network");
            this.initFingerTable(nPrime);
            this.updateOthers();

            System.out.println("Node " + this.id + " updated finger table:");
            for(Finger f : fingerTable) {
                System.out.println(f);
            }

            System.out.println("Node " + nPrime.getId() + " updated finger table:");
            for(Finger f : nPrime.getFingerTable()) {
                System.out.println(f);
            }
            System.out.println("\n");
        }
        // Node `this` is creating a new network
        else {
            Printer.print("Node " + this.id + " creating network", nodeId);

            for(int i = 0; i < m - 1; i++) {
                int thisStart = (this.id + (int) Math.pow(2, i)) % (int) Math.pow(2, m);
                fingerTable[i] = new Finger(thisStart, this);
            }
            int lastStart = (this.id + (int) Math.pow(2, m - 1)) % (int) Math.pow(2, m);
            fingerTable[m - 1] = new Finger(lastStart, this);

            this.setPredecessor(this);
        }
        return true;
    }

    /**
     * Initialize the finger table of this node
     * @param nPrime
     * @throws RemoteException
     */
    public void initFingerTable(INode nPrime) throws RemoteException {

        // Initialize all start and intervals for the finger table
        for(int i = 0; i < m - 1; i++) {
            int thisStart = (this.getId() + (int) Math.pow(2, i)) % (int) Math.pow(2, m);
            fingerTable[i] = new Finger(thisStart, this);
        }
        int lastStart = (this.getId() + (int) Math.pow(2, m - 1)) % (int) Math.pow(2, m);
        fingerTable[m - 1] = new Finger(lastStart, this);

        INode firstSucc = nPrime.findSuccessor(fingerTable[0].start, false);
        // System.out.println("INIT FT: Succesor of " + fingerTable[0].start + " is " + firstSucc.getId() + " wrt N" + this.getId());
        fingerTable[0].node = firstSucc;
        setPredecessor(getSuccessor().getPredecessor());
        getSuccessor().setPredecessor(this);

        for(int i = 0; i < m - 1; i++) {
            int nextStart = fingerTable[i + 1].start;

            // System.out.println("if " + nextStart + " in [" + this.getId() + ", " + fingerTable[i].node.getId() + ")");
            if(isBetween(nextStart, this.getId(), fingerTable[i].node.getId(), true, false)) {
                fingerTable[i + 1].node = fingerTable[i].node;
                // System.out.println("Case 1 (inside): Finger " + (i + 1) + " set to " + fingerTable[i].node.getId());
            }
            else {
                fingerTable[i + 1].node = nPrime.findSuccessor(fingerTable[i + 1].start, false);
                // System.out.println("Case 2 (outside): Finger " + (i + 1) + " set to " + fingerTable[i + 1].node.getId());
            }
        }

        System.out.println("Node " + this.id + " initialized finger table:");
        for(Finger f : fingerTable) {
            System.out.println(f);
        }
    }

    /**
     * Update the finger tables of all other nodes in the network
     * 
     * Psuedocode:
     * --------------
     *  n.update_others()
     *    for i = 1 to m
     *      p = find_predecessor(n - 2^(i-1))
     *      p.update_finger_table(n, i)
     */
    public void updateOthers() throws RemoteException {
        for(int i = 0; i < m; i++) {
            int updateId = (this.getId() - (int) Math.pow(2, i));
            if(updateId < 0) {
                updateId %= (int) Math.pow(2, m);
                updateId += (int) Math.pow(2, m);
            }
            INode p = findPredecessor(updateId, false);
            System.out.println("UpdateOthers:\n  i: " + i + "\n  updateId: Node" + updateId + "\n  p: Node" + p.getId() + "\n  this: Node" + this.getId());
            if(id != p.getId()) {
                p.updateFingerTable(this, i);
            }
        }
    }

    /**
     * Update the finger table of a node
     * 
     * Psuedocode:
     * --------------
     *  n.update_finger_table(s, i)
     *    if s in [n, finger[i].node)
     *      finger[i].node = s
     *      p = predecessor
     *      p.update_finger_table(s, i)
     */
    public void updateFingerTable(INode s, int i) throws RemoteException {
        System.out.println("Update finger table (node in range?):\n  i: " + i + "\n  s: " + s.getId() + "\n  this: " + this.id);
        System.out.println("Node in range?: " + isBetween(s.getId(), this.getId(), fingerTable[i].node.getId(), true, false));
        if(isBetween(s.getId(), this.getId(), fingerTable[i].node.getId(), true, false)) {    
            fingerTable[i].node = s;
            INode p = predecessor;
            // count++;
            // if(count < 10) 
            System.out.println("Updated finger " + i + " of node " + this.id + " to " + s.getId());
            System.out.println("Now calling: p.updateFingerTable(" + s.getId() + ", " + i + ")");
            p.updateFingerTable(s, i);
        }
    }

    /**
     * Get the finger table of this node
     */
    public Finger[] getFingerTable() {
        return fingerTable;
    }

    /**
     * Get the url of this node's successor
     */
    public INode getSuccessor() {
        return fingerTable[0].node;
    }
    public void setSuccessor(INode succ) {
        this.successor = succ;
    }

    /**
     * Get the url of this node's predecessor
     */
    public INode getPredecessor() {
        return predecessor;
    }
    public void setPredecessor(INode pre) {
        this.predecessor = pre;
    }

    /**
     * Insert a new word and its definition into the dictionary
     */
    public boolean insert(String word, String definition) {
        return false;
    }

    /**
     * Lookup a word in the dictionary
     */
    public String lookup(String word) {
        return "";
    }   

    /**
     * Print this node's finger table
     */
    public void printFingerTable(String msg) {
        for(Finger f : fingerTable) {
            // System.out.println("Finger: " + f);
            Printer.print("Finger: " + f, nodeId);
        }
    }

    public void printPreSucc(String msg) throws RemoteException {
        Printer.print("Predecessor: " + predecessor.getId() + "\nSuccessor: " + successor.getId(), nodeId);
    }

    /**
     * Print this node's local dictionary
     */
    public void printDictionary() {
        
    }

    /**
     * Add two numbers with modulo 2^31
     */
    public int modulo31Add(int n, int m) {
        int tmp = n + m;
        return (tmp & Integer.MAX_VALUE);
    }

    public void shutdown() {
        try {
            Naming.unbind(url);
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception e) {
            System.out.println("Error shutting down node: " + e.getMessage());
        }
    }
}









// MAIN FUNCTION 


// public static void main(String[] args) {
        
//     // Validate command line arguments
//     if(args.length != 2) {
//         System.out.println("Usage: java src/Node <nodeID> <configFilepath>");
//         System.exit(1);
//     }

//     // Parse command line arguments
//     String nodeId = args[0];
//     String configFilepath = args[1];

//     // Load and read from the configuration file
//     ConfigStore config = new ConfigStore(configFilepath);
//     String host = config.get("node" + nodeId + ".host");
//     int port = Integer.parseInt(config.get("node" + nodeId + ".port"));
//     // Make sure host and port are present
//     if(host == null || port == 0) {
//         System.out.println("The configuration file does not contain the required information for node " + nodeId);
//     }
//     String nodeUrl = "//" + host + ":" + port + "/Node" + nodeId;

//     // Attempt to start RMI server
//     INode nodeStub;
//     Node node;
//     Registry localReg;
//     try {
//         node = new Node(nodeUrl);
//         System.setProperty("java.rmi.server.hostname", host);
//         nodeStub = (INode) UnicastRemoteObject.exportObject(node, 0);
//         localReg = LocateRegistry.createRegistry(port);
//         Naming.bind(nodeUrl, nodeStub);
//         System.out.println("ChordNode " + nodeId + " running on " + nodeUrl + " with id " + node.id);

//         // Get the bootstrap node
//         BootstrapNode bsNode;
//         String bsNodeId = config.get("node.bootstrap");
//         // If the node is not a bootstrap node, connect to the bootstrap node
//         if(!bsNodeId.equals(nodeId)) {
//             String bsHost = config.get("node" + bsNodeId + ".host");
//             int bsPort = Integer.parseInt(config.get("node" + bsNodeId + ".port"));
//             String bsNodeUrl = "//" + bsHost + ":" + bsPort + "/Node" + bsNodeId;
//             System.out.print("Connecting to " + bsNodeUrl + "..");
//             while(true) {
//                 System.out.print(".");
//                 try {
//                     bsNode = (BootstrapNode) Naming.lookup(bsNodeUrl);
//                     System.out.println("\nConnected!");
//                     break;
//                 } catch(Exception e) {
//                     e.printStackTrace();
//                     Thread.sleep(1000);
//                     continue;
//                 }
//             }

//             // Join the network
//             bsNode.addNode(node);
//         }

//         Thread.sleep(30000);

//         node.shutdown();
//     } catch (Exception e) {
//         System.out.println("Error starting RMI server: " + e.getMessage());
//     }
// }