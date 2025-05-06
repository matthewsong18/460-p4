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
import common.*;
import features.member_management.Member;
import features.member_management.MemberService;

public class Prog4 {
    private static DatabaseConnection conn;
    private static Scanner userin;
    private static MemberService memberHandler;
    
  
    private static String actionMenu(String context){
        /*
         * Handles logic for the action state menu
         * 
         */

        System.out.println();
        System.out.println("What would you like to do?");
        System.out.println("1 | Add a " + context);
        System.out.println("2 | Delete a " + context);
        System.out.println("3 | Update a " + context);
        System.out.println("Type 'back' to go back.");

        String input = userin.nextLine().toLowerCase();

        if (input.equals("1") || input.equals("2") || input.equals("3")) {
            return input;
        }

        System.out.println("ERROR: Invalid argument provided. Please try again.");
        return "";

    }

    private static void queryMenu(){
        /*
         * Handles logic for the query submenu.
         * 
         * 
         */

        String input = "";
        String arg1 = "";

        // initial print
        System.out.println();
        System.out.println("//- AVAILABLE QUERIES -\\\\");
        System.out.println("1 | List all ski lessons purchased for a member");
        System.out.println("2 | List all lift and rental records for a ski pass");
        System.out.println("3 | List all intermediate-level trails and associated information.");
        System.out.println("4 | List the total wage expense for a given property");
        System.out.println("Type 'back' to return to the main menu.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("back")){
            try {
                switch (input) {
                    case "":
                        System.out.println("//- AVAILABLE QUERIES -\\\\");
                        System.out.println("1 | List all ski lessons purchased for a member");
                        System.out.println("2 | List all lift and rental records for a ski pass");
                        System.out.println("3 | List all intermediate-level trails and associated information.");
                        System.out.println("4 | List the total wage expense for a given property");
                        System.out.println("Type 'back' to return to the main menu.");
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
                    case "back":
                        return;
                    default:
                        System.out.println("ERROR: Invalid option selected.");
                        break;
                }
                arg1 = "";
                input = "";
            } catch (InputMismatchException e){
                System.out.println("ERROR: Invalid argument provided. Please try again.");
                input = "";
            }
        }

        return;
    }
    
    private static void executionMenu(String actionState, String context){
        // TO DO: add logic for ski pass, equipment, rentals, lessons
        // action logic
        try{
            switch (actionState) {
                case "1":
                    // add
                    if (context.equals("member")) {
                        // MEMBER LOGIC //
                        System.out.print("Name: ");
                        String name = userin.nextLine();
                        System.out.print("Telephone #: ");
                        String telephoneNumber = userin.nextLine();
                        System.out.print("Email: ");
                        String email = userin.nextLine();
                        System.out.print("Date of Birth: ");
                        String dateOfBirth = userin.nextLine();
                        System.out.print("Emergency Telephone #: ");
                        String emergency = userin.nextLine();
    
                        // add member
                        long id = memberHandler.registerMember(name, telephoneNumber, email, dateOfBirth, emergency);
                        System.out.println("Added new member: " + id);
                    } else if (context.equals("ski")){
                        // SKI PASS LOGIC //
                        System.out.print("Member ID: ");
                        String id = userin.nextLine();
                        System.out.print("Pass type: ");
                        String type = userin.nextLine();
    
                        // add pass
                        System.out.println("Added new ski pass: [id here]");
                    } 
                    break;
                case "2":
                    // delete
                    if (context.equals("member")) {
                        // MEMBER LOGIC //
                        System.out.print("Member ID: ");
    
                        // delete member
                        memberHandler.deleteMemberById(null);
                        System.out.println("Deleted member [id here].");
                    } else if (context.equals("ski")) {
    
                    }
                    break;
                case "3":
                    // update
                    if (context.equals("member")) {
                        // MEMBER LOGIC //
                        System.out.print("Member ID: ");
                        long id = userin.nextLong();
    
                        System.out.println("What would you like to update?");
                        System.out.println("1 | Email");
                        System.out.println("2 | Telephone #");
                        System.out.println("3 | Emergency Telephone #");
                        
                        // grab the member in question
                        Optional<Member> values = memberHandler.findMemberById((long)id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Member with id " + id + " does not exist. Try again.");
                            return;
                        }

                        String field = userin.nextLine();
                        Member toUpdate;

                        if (field.equals("1")) {
                            // update email
                            System.out.print("New email: ");
                            String value = userin.nextLine();

                            toUpdate = new Member(id, values.get().name(), values.get().telephoneNumber(), value, values.get().dateOfBirth(), values.get().emergencyContact());
                        } else if (field.equals("2")){
                            // update phone number
                            System.out.print("New telephone #: ");
                            String value = userin.nextLine();
                            toUpdate = new Member(id, values.get().name(), value, values.get().email(), values.get().dateOfBirth(), values.get().emergencyContact());
                        } else if (field.equals("3")) {
                            // update emergency phone number
                            System.out.print("New emergency telephone #: ");
                            String value = userin.nextLine();
                            toUpdate = new Member(id, values.get().name(), values.get().telephoneNumber(), values.get().email(), values.get().dateOfBirth(), value);
                        } else {
                            System.out.println("ERROR: Invalid option selected. Please try again.");
                            return;
                        }

                        // execute modification
                        memberHandler.updateMember(toUpdate);
                        System.out.println("Update on member " + id + " succesful.");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            System.out.println("ERROR: Invalid argument provided. Double check your input and try again.");
            return;
        }
    }

    public static void main(String[] args){
        // initialize database connection
        if (args.length != 2) {
            System.err.println("USAGE: Prog4.java <username> <password>");
            System.exit(1);
        } 
        
        conn = new DatabaseConnection(args[0], args[1]);

        // initialize DB services
        memberHandler = new MemberService(conn);

        userin = new Scanner(System.in);
        String input = "";

        // initial print
        System.out.println("//- SKI RESORT DATABASE -\\\\");
        System.out.println("1 | Add, delete, or update a member.");
        System.out.println("2 | Add, delete, or update a ski pass.");
        System.out.println("3 | Add, delete, or update an equipment record.");
        System.out.println("4 | Add, update, or delete a rental record.");
        System.out.println("5 | Add, update, or delete a lesson purchase record.");
        System.out.println("Type 'query' to access the queries submenu.");
        System.out.println("Type 'exit' to close the application.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("exit")) {
            String actionState = "";
            String context = "";
            
            // main logic
            try{
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
                        actionState = actionMenu("member");
                        context = "member";
                        break;
                    case "2":
                        // ski pass
                        actionState = actionMenu("ski pass");
                        context = "ski";
                        break;
                    case "3":
                        // equipment
                        actionState = actionMenu("equipment record");
                        context = "equipment";
                        break;
                    case "4":
                        // rental
                        actionState = actionMenu("rental record");
                        context = "rental";
    
                        break;
                    case "5":  
                        // lesson
                        actionState = actionMenu("lesson purchase record");
                        context = "lesson";
                        break;
                    case "query":
                        // queries
                        queryMenu();
                        
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
                    default:
                        System.out.println("ERROR: Invalid option selected.");
                        break;
                }

               executionMenu(actionState, context);
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid argument provided. Try again.");
            }
                        
            input = "";
        }

        userin.close();

        System.exit(0);
    }
}
