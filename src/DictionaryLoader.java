/**
 * @file DictionaryLoader.java
 * @summary Loads a dictionary .txt file into the Chord network. The dictionary file should be formatted as follows:
 *   word : definition
 *   word : definition
 *   ...
 * 
 * @author: Jamison Grudem (grude013)
 * @grace_days Using 1 grace days
 */

package src;

import java.rmi.Naming;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @class DictionaryLoader
 */
public class DictionaryLoader {
    public static void main(String[] args) {
        
        // Validate arguments
        if(args.length != 2) {
            System.out.println("Usage: java DictionaryLoader <nodeUrl> <dictionaryFile>");
            return;
        }

        // Parse the command line arguments
        String nodeUrl = args[0];
        String dictionaryFile = args[1];
        Printer.print("Starting DictionaryLoader...\nNode URL: " + nodeUrl + "\nFile: " + dictionaryFile, "DictionaryLoader");

        // Attempt to connect to the node
        INode node;
        try {
            System.out.println("Connecting to node " + nodeUrl);
            Printer.print("Connecting to node " + nodeUrl, "DictionaryLoader");
            node = (INode) Naming.lookup(nodeUrl);
        } catch(Exception e) {
            System.out.println("An error occurred while connecting to node, see the logs.");
            Printer.print("Error connecting to node: " + e.getMessage(), "DictionaryLoader");
            return;
        }
        System.out.println("Connected to node!");
        Printer.print("Connected to node!", "DictionaryLoader");

        // Load the dictionary file
        System.out.println("Loading dictionary file...");
        Printer.print("Loading dictionary file...", "DictionaryLoader");
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
            String line = reader.readLine();
            while(line != null) {
                // Separate the word and definition
                String[] parts = line.split(" : ");
                String word = parts[0];
                String def = parts[1];

                // Insert the word into the dictionary
                map.put(word, def);
                int location = node.insert(word, def);
                Printer.print("Insert: " + word + " => Node " + location, "DictionaryLoader");

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the dictionary file, see the logs.");
            Printer.print("Error reading dictionary file: " + e.getMessage(), "DictionaryLoader");
        }

        // Print the number of words inserted
        System.out.println("Dictionary loaded successfully, " + map.size() + " words inserted.");
        Printer.print("Dictionary loaded successfully, " + map.size() + " words inserted.", "DictionaryLoader");
    }
}
