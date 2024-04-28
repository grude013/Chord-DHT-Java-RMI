package src;

import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryLoader {
    public static void main(String[] args) {
        
        // Validate arguments
        if(args.length != 1) {
            System.out.println("Usage: java DictionaryLoader <nodeUrl> <dictionaryFile>");
        }

        // Parse the command line arguments
        String nodeUrl = args[0];
        String dictionaryFile = args[1];

        // Load the dictionary file
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
            String line = reader.readLine();
            while(line != null) {
                String[] parts = line.split(" : ");
                map.put(parts[0], parts[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading dictionary file: " + e.getMessage());
        }

        // Print the contents of the dictionary
        for(Object key : map.keySet()) {
            System.out.println(key + " : " + map.get(key));
        }

    }
}
