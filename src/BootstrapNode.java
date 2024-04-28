package src;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class BootstrapNode extends Node {

    public BootstrapNode(String url) {
        super(url);
        Printer.print("Node ID: " + this.nodeId, nodeId);
        Printer.print("Chord ID: " + this.id, nodeId);
    }

    public void initFingerTableBootstrap() {
        // First, loop through and find all starts for the finger table
        for(int i = 0; i < m; i++) {
            int start = modulo31Add(id, (int) Math.pow(2, i)) % (int) Math.pow(2, m);
            finger[i] = new Finger(start, this);
        }
    }
    
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
        String host = config.get("node" + nodeId + ".host");
        int port = Integer.parseInt(config.get("node" + nodeId + ".port"));
        // Make sure host and port are present
        if(host == null || port == 0) {
            System.out.println("The configuration file does not contain the required information for node " + nodeId);
        }
        String nodeUrl = "//" + host + ":" + port + "/Node" + nodeId;

        // Attempt to start RMI server
        INode nodeStub;
        Node node;
        Registry localReg;
        try {
            node = new BootstrapNode(nodeUrl);
            System.setProperty("java.rmi.server.hostname", host);
            nodeStub = (INode) UnicastRemoteObject.exportObject(node, 0);
            localReg = LocateRegistry.createRegistry(port);
            Naming.bind(nodeUrl, nodeStub);
            System.out.println("BootstrapNode " + nodeId + " running on " + nodeUrl + " with id " + node.getId());

            // Create a network with self
            node.join(null);

            Thread.sleep(30000);

            node.shutdown();
        } catch (Exception e) {
            System.out.println("Error starting RMI server: " + e.getMessage());
        }
    }

}
