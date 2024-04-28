package src;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class Node implements INode {
    
    protected final int m = 15;
    protected final int mod = (int)Math.pow(2, m);
    public Finger[] finger = new Finger[m];
    public ConcurrentHashMap<Integer, String> dictionary = new ConcurrentHashMap<Integer, String>();
    
    public int id;
    public String nodeId;
    protected String url;
    protected INode predecessor;
    protected INode successor;

    public Node(String url) {
        this.url = url;
        this.id = Hash.hash32(url);
        this.nodeId = url.split("/")[url.split("/").length - 1];

        this.predecessor = this;
        this.successor = this;

        for(int i = 0; i < m; i++) {
            int start = (this.id + (int)Math.pow(2, i)) % mod;
            finger[i] = new Finger(start, this);
        }
    }

    public int insert(String word, String definition) throws RemoteException {
        int key = Hash.hash32(word);
        INode nPrime = this.findSuccessor(key, false);
        nPrime.getDictionary().put(key, definition);
        return nPrime.getId();
    }

    public String lookup(String word) throws RemoteException {
        int key = Hash.hash32(word);
        INode nPrime = this.findSuccessor(key, false);
        return nPrime.getDictionary().get(key);
    }

    public INode findSuccessor(int id, boolean traceFlag) throws RemoteException {
        INode nPrime = this.findPredecessor(id, traceFlag);
        return nPrime.getSuccessor();
    }

    public INode findPredecessor(int id, boolean traceFlag) throws RemoteException {
        INode nPrime = this;
        Range r = new Range(nPrime.getId(), false, nPrime.getSuccessor().getId(), true);
        while(!r.contains(id)) {
            nPrime = nPrime.closestPrecedingFinger(id);
            r = new Range(nPrime.getId(), false, nPrime.getSuccessor().getId(), true);
        }
        return nPrime;
    }

    public INode closestPrecedingFinger(int id) throws RemoteException {
        for(int i = m - 1; i >= 0; i--) {
            Range r = new Range(this.id, id);
            if(this.finger[i].node != null && r.contains(this.finger[i].node.getId())) {
                return this.finger[i].node;
            }
        }
        return this;
    }

    public boolean join(INode estNode) throws RemoteException {
        if(estNode != null) {
            // this.printFingerTable("Before initFingerTable");
            // this.printPreSucc("Before initFingerTable");
            // estNode.printPreSucc("Before initFingerTable");

            this.initFingerTable(estNode);
            // this.printFingerTable("Before updateOthers");
            // this.printPreSucc("Before updateOthers");
            // estNode.printPreSucc("Before updateOthers");

            this.updateOthers();
            // this.printFingerTable("After updateOthers");
            // estNode.printFingerTable("After updateOthers");
            // this.printPreSucc("After updateOthers");
            // estNode.printPreSucc("After updateOthers");
            // move keys in (predecessor, n] from successor to n
        }
        else {
            for(int i = 0; i < m; i++)
                this.finger[i].node = this;
            this.predecessor = this;
            this.successor = this.finger[0].node;
        }
        return true;
    }

    public void initFingerTable(INode estNode) throws RemoteException {
        this.finger[0].node = estNode.findSuccessor(this.finger[0].start, false);
        this.predecessor = this.getSuccessor().getPredecessor();
        this.getSuccessor().setPredecessor(this);

        for(int i = 0; i < m - 1; i++) {
            Range r = new Range(this.id, true, this.finger[i].node.getId(), false);
            if(r.contains(this.finger[i + 1].start))
                this.finger[i + 1].node = this.finger[i].node;
            else
                this.finger[i + 1].node = estNode.findSuccessor(this.finger[i + 1].start, false);
        }
    }

    public void updateOthers() throws RemoteException {
        for(int i = 0; i < m; i++) {
            int id = (this.id + 1 - (int)Math.pow(2, i));
            if(id < 0)
                id += mod;
            INode p = this.findPredecessor(id, false);
            if(p.getId() != this.id)
                p.updateFingerTable(this, i);
        }
    }

    public void updateFingerTable(INode s, int i) throws RemoteException {
        Range r = new Range(this.id, false, this.finger[i].node.getId(), false);
        // System.out.println("Node " + this.id + " checking if " + s.getId() + " is in range " + r.toString());
        if(r.contains(s.getId())) {
            // System.out.println("Node " + this.id + " updating finger " + i + " to " + s.getId());
            // this.printPreSucc("Current before updating");
            this.finger[i].node = s;
            // this.printPreSucc("After updating");
            INode p = this.getPredecessor();
            // System.out.println(p.getId() + ".updateFingerTable(" + this.getId() + ", " + i + ")");
            p.updateFingerTable(s, i);
        }
    }

    public int getId() {
        return id;
    }
    public String getUrl() {
        return url;
    }

    public INode getSuccessor() {
        return this.finger[0].node;
    }
    public void setSuccessor(INode node) {
        successor = node;
        finger[0].node = node;
    }

    public INode getPredecessor() {
        return predecessor;
    }
    public void setPredecessor(INode node) {
        predecessor = node;
    }

    public Finger[] getFingerTable() {
        return finger;
    }
    public ConcurrentHashMap<Integer, String> getDictionary() {
        return dictionary;
    }

    public void printFingerTable(String msg) {
        System.out.println("[" + msg + "] Node " + this.id + " finger table:");
        for(Finger f : finger) {
            System.out.println(f);
        }
    }
    public void printPreSucc(String msg) throws RemoteException {
        System.out.println("[" + msg + "] Pre/Succ: " + this.predecessor.getId() + " => [Node " + this.id + "] => " + this.getSuccessor().getId());
    }
    public void printDictionary() {
        System.out.println("Node " + this.id + " dictionary:");
        for(Integer key : dictionary.keySet()) {
            System.out.println(key + ": " + dictionary.get(key));
        }
    }

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
