/**
 * @file src/Printer.java
 * @brief Printer class used for writing to the .log and .html files for logging and debugging.
 * @created 2024-03-30
 * @author Jamison Grudem (grude013)
 * 
 * @grace_days Using 2 grace days
 */

package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;

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

            // Write to the HTML file with a table row delimited by '|'
            String htmlFilename = "../log/html/" + fileId + ".html";
            FileWriter htmlWriter = new FileWriter(htmlFilename, true);
            BufferedWriter htmlBwriter = new BufferedWriter(htmlWriter);
            String[] args = arg.split("\\|");
            htmlBwriter.write("<tr>\n");
            for(String a : args) {
                htmlBwriter.write("<td style='padding: 0 5px; border: 1px solid black;'>" + a + "</td>\n");
            }
            htmlBwriter.write("</tr>\n");
            htmlBwriter.flush();
            htmlBwriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }   
    

    /**
     * Prints to the console and the specified file
     * @param arg The string to print, delimited by '|' for HTML
     * @param file The enum value of the file to write to
     * @param id An optional identifier for the file
     * @param htmlColor The color to use for the HTML row
     */
    public static synchronized void print(String arg, String fileId, String id, String htmlColor) {
        try {
            // System.out.println(arg);
            String filename = "../log/" + fileId + id + ".log";
            writer = new FileWriter(filename, true);
            bwriter = new BufferedWriter(writer);
            bwriter.write(arg + "\n");
            bwriter.flush();

            // Write to the html file - with specified color
            String htmlFilename = "../log/html/" + fileId + id + ".html";
            FileWriter htmlWriter = new FileWriter(htmlFilename, true);
            BufferedWriter htmlBwriter = new BufferedWriter(htmlWriter);
            String[] args = arg.split("\\|");
            htmlBwriter.write("<tr style='background-color:" + htmlColor + ";'>\n");
            for(String a : args) {
                htmlBwriter.write("<td style='padding: 0 5px; border: 1px solid black;'>" + a.trim() + "</td>\n");
            }
            htmlBwriter.write("</tr>\n");
            htmlBwriter.flush();
            htmlBwriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the HTML log file, writes HTML header and starts the table
     * @param file The file to write to
     * @param id The identifier for the file
     * @param links The links to other logs
     */
    public static synchronized void initHtmlLog(String fileId, String id, String links) {
        try {
            String htmlFilename = "../log/html/" + fileId + id + ".html";
            FileWriter htmlWriter = new FileWriter(htmlFilename, false);
            BufferedWriter htmlBwriter = new BufferedWriter(htmlWriter);
            htmlBwriter.write("<html>\n<head>\n<title>" + fileId + " log</title>\n</head>\n<body><h1>" + fileId + id + " log</h1><p>" + LocalDateTime.now() + "</p>\n" + links + "\n");

            // Write the header row for a client log
            if(fileId.contains("client")) {
                htmlBwriter.write("<br/><button onclick='document.body.scrollTop = document.body.scrollHeight'>Scroll To Bottom</button><table style='width: 100%; position: relative;'><tr style='position: sticky; top: 0; background: white;'>" + 
                    "<th style='border: 1px solid black;'>Thread</th>" +
                    "<th style='border: 1px solid black;'>Server</th>" +
                    "<th style='border: 1px solid black;'>Operation</th>" +
                    "<th style='border: 1px solid black;'>Timestamp</th>" +
                    "<th style='border: 1px solid black;'>Message</th>" +
                    "<th style='border: 1px solid black;'>Parameters</th></tr>\n"
                );
            }
            // Write the header row for a server log
            else {
                htmlBwriter.write("<br/><button onclick='document.body.scrollTop = document.body.scrollHeight'>Scroll To Bottom</button><table style='width: 100%; position: relative;'><tr style='position: sticky; top: 0; background: white;'>" + 
                    "<th style='border: 1px solid black;'>Server</th>" +
                    "<th style='border: 1px solid black;'>Operation</th>" +
                    "<th style='border: 1px solid black;'>Timestamp</th>" +
                    "<th style='border: 1px solid black;'>Lamport Clock</th>" +
                    "<th style='border: 1px solid black;'>Origin</th>" +
                    "<th style='border: 1px solid black;'>Message</th>" + 
                    "<th style='border: 1px solid black;'>Parameters</th></tr>\n"
                );
            }

            htmlBwriter.flush();
            htmlBwriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the HTML log file, writes the closing table and body tags
     * @param file The file to write to
     * @param id The identifier for the file
     */
    public static synchronized void closeHtmlLog(String fileId, String id) {
        try {
            String htmlFilename = "../log/html/" + fileId + id + ".html";
            FileWriter htmlWriter = new FileWriter(htmlFilename, true);
            BufferedWriter htmlBwriter = new BufferedWriter(htmlWriter);
            htmlBwriter.write("</table>\n<button onclick='document.body.scrollTop = 0;'>Scroll To Top</button></body>\n</html>\n");
            htmlBwriter.flush();
            htmlBwriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the writer and buffered writer
     */
    public static void close() {
        try {
            bwriter.close();
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
