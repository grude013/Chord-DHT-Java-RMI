/**
 * @file INode.java
 * @summary Interface for the Node class. Contains remote methods to be used while implementing
 * the Chord protocol.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 1 grace days
 */
package src;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @interface INode
 * @summary Interface for Java RMI Node. Extends remote to allow for remote method invocation.
 */
public interface INode extends Remote {

  // Get hashed ID of the node
  public int getId() throws RemoteException;

  // Get the URL of the node
  public String getUrl() throws RemoteException;

  // Find the succesor of a given key
  public INode findSuccessor (int key, boolean traceFlag) throws RemoteException;

  // Find the predecessor of a given key
  public INode findPredecessor (int key, boolean traceFlag) throws RemoteException;

  // Find the closest preceding finger of a given key
  public INode closestPrecedingFinger (int key) throws RemoteException;

  // Get the successor of the node
  public INode getSuccessor () throws RemoteException;

  // Set the successor of the node
  public void setSuccessor (INode node) throws RemoteException;

  // Get the predecessor of the node
  public INode getPredecessor () throws RemoteException;

  // Set the predecessor of the node
  public void setPredecessor (INode node) throws RemoteException;

  // Get `this` join lock
  public boolean getJoinLock () throws RemoteException;

  // Set `this` join lock
  public void setJoinLock (boolean lock) throws RemoteException;

  // The calling node joins the network with the node `node`
  public boolean join(INode node) throws RemoteException;

  // Get the finger table
  public Finger[] getFingerTable() throws RemoteException;

  // Get the dictionary
  public ConcurrentHashMap<Integer, String> getDictionary() throws RemoteException;

  // Update the finger table entry i of node s
  public void updateFingerTable(INode s, int i) throws RemoteException;

  // Update all nodes whose finger table should refer to node s
  public void updateOthers() throws RemoteException;

  // Insert a word and definition into the DHT
  public int insert (String word, String definition) throws RemoteException;

  // Insert a key with a definition into a node's local dictionary
  public boolean insertLocal(int key, String definition) throws RemoteException;

  // Lookup a word in the dictionary
  public String lookup (String word) throws RemoteException;

  // Move a key from the node to the node s
  public void moveKey (INode s, int key) throws RemoteException;

  // Print the node's finger table
  public void printFingerTable (String msg) throws RemoteException;

  // Print the node's predecessor and successor
  public void printPreSucc (String msg) throws RemoteException;

  // Get the node's predecessor and successor as a string
  public String getPreSucc () throws RemoteException;

  // Print the dictionary
  public void printDictionary () throws RemoteException;

}
