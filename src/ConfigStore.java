/**
 * @file ConfigStore.java
 * @summary Reads in a configuration file and stores key-value pairs for easy access.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 1 grace day
 */
package src;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;


/**
 * @class ConfigStore
 * @summary Reads in a configuration file and provides retrieval abstraction.
 *  Format:
 *     key=value
 *     key2=value2
 *     ...
 */
public class ConfigStore {

    // Key-value pairs from the config file
    private Map<String, String> kvs;

    // Path to the config file
    private String filepath;

    /**
     * Constructor for ConfigStore
     * @param filepath Path to the config file
     */
    public ConfigStore(String filepath) {
        this.filepath = filepath;
        this.kvs = new HashMap<String, String>();
        this.read();
    }

    /**
     * Read in all the key-value pairs from the config file
     */
    private void read() {
        // Read in all the key-value pairs from the config file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine();
            while(line != null) {
                String[] parts = line.split("=");
                kvs.put(parts[0], parts[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading config file: " + e.getMessage());
        }
    }

    /**
     * Get the value associated with a key
     * @param key Key to lookup
     * @return Value associated with the key, or null if not found
     */
    public String get(String key) {
        return kvs.get(key);
    }
}