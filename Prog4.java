/*
 * Prog4.java
 * 
 * Main class for ski resort database user interface.
 * 
 * Authors: Elias Zheng, Ethan Huang, Matthew Song, Stephin Tomson
 * 
 * Usage: Prog4.java <username> <password>
 */

import java.io.*;
import java.util.*;

public class Prog4 {
    //private static DatabaseConnection conn;
    private static Scanner userin;
    private static int actionState;

    private static void queryMenu(){
        String input = "";
        String arg1 = "";

        System.out.println("//- AVAILABLE QUERIES -\\\\");
        System.out.println("1 | List all ski lessons purchased for a member");
        System.out.println("2 | List all lift and rental records for a ski pass");
        System.out.println("3 | List all intermediate-level trails and associated information.");
        System.out.println("4 | List the total wage expense for a given property");
        System.out.println("Type 'exit' to return to the main menu.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("exit")){
            try {
                switch (input) {
                    case "":
                        System.out.println("//- AVAILABLE QUERIES -\\\\");
                        System.out.println("1 | List all ski lessons purchased for a member");
                        System.out.println("2 | List all lift and rental records for a ski pass");
                        System.out.println("3 | List all intermediate-level trails and associated information.");
                        System.out.println("4 | List the total wage expense for a given property");
                        System.out.println("Type 'exit' to return to the main menu.");
                        break;
                    case "1":
                        System.out.print("\nEnter Member ID: ");
                        arg1 = userin.nextLine();
                        // execute
                        break;
                    case "2":
                        System.out.print("\nEnter Ski Pass ID: ");
                        arg1 = userin.nextLine();
                        // execute
                        break;
                    case "3":
                        // execute
                        break;
                    case "4":
                        System.out.print("\nEnter Property ID: ");
                        arg1 = userin.nextLine();
                        //execute
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.println("ERROR: Invalid option selected.");
                        break;
                }
                arg1 = "";
                input = "";
            } catch (InputMismatchException e){
                System.out.println("Invalid argument provided. Please try again.");
                input = "";
            }
        }

        return;
    }
    
    private static void actionMenu(){

    }
    public static void main(String[] args){
        // initialize database connection
        if (args.length != 2) {
            System.err.println("USAGE: Prog4.java <username> <password>");
            System.exit(1);
        } 
        //conn = new DatabaseConnection(args[0], args[1]);

        userin = new Scanner(System.in);
        String input = "";

        System.out.println("//- SKI RESORT DATABASE -\\\\");
        System.out.println("1 | Add, delete, or update a member.");
        System.out.println("2 | Add, delete, or update a ski pass.");
        System.out.println("3 | Add, delete, or update an equipment record.");
        System.out.println("4 | Add, update, or delete a rental record.");
        System.out.println("5 | Add, update, or delete a lesson purchase record.");
        System.out.println("Type 'query' to access the queries submenu.");
        System.out.println("Type 'exit' to close the application.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("exit")) {
            // informational menu print
            switch (input) {
                case "":
                    // main menu print
                    System.out.println("//- SKI RESORT DATABASE -\\\\");
                    System.out.println("1 | Add, delete, or update a member.");
                    System.out.println("2 | Add, delete, or update a ski pass.");
                    System.out.println("3 | Add, delete, or update an equipment record.");
                    System.out.println("4 | Add, update, or delete a rental record.");
                    System.out.println("5 | Add, update, or delete a lesson purchase record.");
                    System.out.println("Type 'query' to access the queries submenu.");
                    System.out.println("Type 'exit' to close the application.");
                    break;
                case "1":
                    // member
                    break;
                case "2":
                    // ski pass
                    break;
                case "3":
                    // equipment
                    break;
                case "4":
                    // rental

                    break;
                case "5":  
                    // lesson

                    break;
                case "query":
                    // queries
                    queryMenu();
                    
                    break;
                default:
                    System.out.println("ERROR: Invalid option selected.");
                    break;
            }

            input = "";
            
        }

        userin.close();
        // TO DO: close database connection when finished

        System.exit(0);
    }
}
