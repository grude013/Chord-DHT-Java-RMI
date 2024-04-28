/**
 * 
 */
package src;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface INode extends Remote {
  // Get the ID of the node
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

  // Insert a word and its definition into the dictionary
  public int insert (String word, String definition) throws RemoteException;

  // Lookup a word in the dictionary
  public String lookup (String word) throws RemoteException;

  // Print the node's finger table
  public void printFingerTable (String msg) throws RemoteException;

  // Print the node's predecessor and successor
  public void printPreSucc (String msg) throws RemoteException;

  // Print the dictionary
  public void printDictionary () throws RemoteException;
}
