package src;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class ConfigStore {

    private String filepath;
    private Map<String, String> kvs;

    public ConfigStore(String filepath) {
        // Initialize the filepath and key-value map
        this.filepath = filepath;
        this.kvs = new HashMap<String, String>();

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

    public String get(String key) {
        return kvs.get(key);
    }
}