package src;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        
        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        String option = "";
        while(option != "3") {
            System.out.println("Enter 1 to lookup, 2 to insert a new (key-value) item, or 3 to exit:");
            System.out.print("Enter your choice: ");
            option = scanner.nextLine();

            // Lookup a word in the dictionary
            if(option.equals("1")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                System.out.println("Result: Definition of the word");
            }
            // Insert a new word into the dictionary
            else if(option.equals("2")) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                System.out.print("Enter the meaning: ");
                String meaning = scanner.nextLine();
                System.out.println("Result status: Inserted word " + word + " (key = 1234567) at node //localhost:1099/Node01");
            }
            // Exit the client
            else if(option.equals("3")) {
                System.out.println("Exiting...");
                break;
            }
            // Invalid option
            else {
                System.out.println("Invalid option. Please try again.");
            }
        }

    }
}