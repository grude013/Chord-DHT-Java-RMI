/**
 * @file Printer.java
 * @brief Printer class used for writing to the .log and .html files for logging and debugging.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 2 grace days
 */
package src;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Printer {

    // Stores the file writer
    static FileWriter writer;
    // Stores the buffered writer
    static BufferedWriter bwriter;

    /**
     * Initializes the writer and buffered writer
     */
    public static void start() {
        writer = null;
        bwriter = null;
    }

    /**
     * Prints to the console and the specified file
     * @param arg The string to print, delimited by '|' for HTML
     * @param file The enum value of the file to write to
     * @param id An optional identifier for the file
     */
    public static synchronized void print(String arg, String fileId) {
        try {
            // Write to the .log file as is
            String filename = "../log/" + fileId + ".log";
            writer = new FileWriter(filename, true);
            bwriter = new BufferedWriter(writer);
            bwriter.write(arg + '\n');
            bwriter.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }   
}
