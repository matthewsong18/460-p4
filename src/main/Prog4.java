 /*=============================================================================
 |   Assignment:  Program #4
 |       Author:  Stephin Tomson (stephintomson@arizona.edu)
 |                Ethan Huang
 |                Matthew Song
 |                
 | 
 |       Course:  CS460 Database Design Spring 2025
 |   Instructor:  L. McCann
 | Sect. Leader:  [Your Section Leader's name]
 |     Due Date:  05/06/2025, at the beginning of class
 |
 |     Language:  Java 11
 |
 +-----------------------------------------------------------------------------
 |
 |  Description:  This program implements a complete Java-based client interface
 |                for interacting with the Ski Resort database system. It allows
 |                users to perform standard CRUD (Create, Read, Update, Delete)
 |                operations on the core entities of the system such as Members,
 |                Ski Passes, Equipment, Rentals, and Lesson Orders. It also
 |                includes a query menu to generate analytical or summary reports
 |                such as total wages by property, rental history by ski pass,
 |                or intermediate trails with lift mappings.
 |
 |        Input:  - Oracle database username and password as command-line arguments.
 |                - User input through a command-line menu system.
 |
 |       Output:  - Console output of user-friendly prompts, confirmation messages,
 |                  and formatted query results using ResultSet metadata.
 |                - Warning and error messages for failed operations or invalid input.
 |
 |   Techniques:  - JDBC (Java Database Connectivity) for database access and updates.
 |                - Encapsulation using service and domain classes for modular logic.
 |                - SQL operations for inserting, selecting, updating, and deleting
 |                  rows from related tables, including complex joins and triggers.
 |
 *===========================================================================*/


package main;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import common.*;
import features.member_management.*;
import features.equipment_management.*;
import features.rental_management.*;
import features.lesson_management.*;
import features.ski_pass.*;
import features.queries.*;

public class Prog4 {
    private static DatabaseConnection conn;
    private static Scanner userin;

    private static MemberService memberHandler;
    private static EquipmentService equipmentHandler;
    private static RentalService rentalHandler;
    private static LessonOrderService lessonHandler;
    private static SkiPassService passHandler;

    private static ArrayList<String> tracker = new ArrayList<String>();
/*--------------------------------------------------------------------- 
    |  Method printResults
    |
    |  Purpose:  To format and display the results from a SQL query stored
    |            in a ResultSet. It prints column headers and each row in a 
    |            user-readable format.
    |
    |  Pre-condition:  The ResultSet should not be null and should contain valid
    |                  metadata and rows from a SQL SELECT operation.
    |
    |  Post-condition: The formatted result is printed to the console.
    |
    |  Parameters:
    |      results -- The ResultSet containing query output from the database.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void printResults(ResultSet results) {
               try {
            if (results == null) {
                System.out.println("| NO RESULTS |");
                return;
            }

            ResultSetMetaData meta = results.getMetaData();

            // print attribute names
            StringBuilder builder = new StringBuilder("|");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                builder.append(String.format("| %-25s |", meta.getColumnName(i)));
            }
            builder.append("|");
            System.out.println(builder.toString());

            // print all tuples
            int count = 0;
            while (results.next()) {
                count++;
                builder = new StringBuilder("|");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    builder.append(String.format("| %-25s |", results.getString(i)));
                }
                builder.append("|");
                System.out.println(builder.toString());
            }

            if (count == 0) {
                System.out.println(String.format("|| %-25s || ", "NO RESULTS"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: " + e.getMessage());
        }
    }
 /*--------------------------------------------------------------------- 
    |  Method printResults (with tracking)
    |
    |  Purpose:  Same as the regular printResults method, but also tracks the 
    |            first column of each result row into the global tracker list.
    |
    |  Pre-condition:  Valid ResultSet object; first column expected to be a unique ID.
    |
    |  Post-condition: Results printed and tracker list populated with identifiers.
    |
    |  Parameters:
    |      results -- The ResultSet containing the query output.
    |      track   -- A boolean flag that, when true, enables ID tracking.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void printResults(ResultSet results, boolean track) {
     
        tracker = new ArrayList<String>();

        try {
            if (results == null) {
                System.out.println("| NO RESULTS |");
                return;
            }

            ResultSetMetaData meta = results.getMetaData();

            // print attribute names
            StringBuilder builder = new StringBuilder("|");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                builder.append(String.format("| %-25s |", meta.getColumnName(i)));
            }
            builder.append("|");
            System.out.println(builder.toString());

            // print all tuples
            int count = 0;
            while (results.next()) {
                count++;
                builder = new StringBuilder("|");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    builder.append(String.format("| %-25s |", results.getString(i)));
                }
                builder.append("|");
                tracker.add(results.getString(1));
                System.out.println(builder.toString());
            }

            if (count == 0) {
                System.out.println(String.format("|| %-25s ||b ", "NO RESULTS"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: " + e.getMessage());
        }
    }
    /*--------------------------------------------------------------------- 
    |  Method actionMenu
    |
    |  Purpose:  Displays a menu prompting the user to select a CRUD action 
    |            (Add, Delete, Update) for a given entity context and returns 
    |            the corresponding choice.
    |
    |  Pre-condition:  The input parameter `context` must be a valid non-null 
    |                  string representing the entity name (e.g., "member", "ski pass").
    |
    |  Post-condition: A string representing the user's valid action choice is returned.
    |                  If the user types 'back' or an invalid entry, an empty string is returned.
    |
    |  Parameters:
    |      context -- The entity type for which the action menu is being generated.
    |
    |  Returns:  A string ("1", "2", or "3") indicating Add, Delete, or Update,
    |            or an empty string if the user typed "back" or gave invalid input.
    *-------------------------------------------------------------------*/

    private static String actionMenu(String context) {


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

        if (input.equals("back")) {
            return "";
        }

        System.out.println("ERROR: Invalid argument provided. Please try again.");
        return "";

    }
    /*--------------------------------------------------------------------- 
    |  Method queryMenu
    |
    |  Purpose:  Displays a submenu of predefined queries for the Ski Resort database
    |            and handles execution of the selected query. Handles both parameterized
    |            and simple queries and prints results using printResults().
    |
    |  Pre-condition:  The user must be authenticated and the database connection
    |                  must be valid. QueryService must be properly initialized.
    |
    |  Post-condition: The selected query is executed and results are displayed.
    |                  If the query is parameterized, input is taken from the user.
    |
    |  Parameters:  None.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/

    private static void queryMenu() {
        String input = "";
        String arg1 = "";

        // initial print
        System.out.println();
        System.out.println("\n\n//- AVAILABLE QUERIES -\\\\");
        System.out.println("1  | List all ski lessons purchased for a member");
        System.out.println("2  | List all lift and rental records for a ski pass");
        System.out.println("3  | List all intermediate-level trails and associated information.");
        System.out.println("4  | List the total wage expense for a given property");
        System.out.println("5  | List all members");
        System.out.println("6  | List all active ski passes");
        System.out.println("7  | List all equipment");
        System.out.println("8  | List all rentals");
        System.out.println("9  | List all lessons");
        System.out.println("10 | List all lesson orders ");
        System.out.println("Type 'back' to return to the main menu.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("back")) {
            try {
                switch (input) {
                    case "1":
                        System.out.print("\nEnter Member ID: ");
                        arg1 = userin.nextLine();

                        // execute
                        System.out.println("LESSON HISTORY FOR MEMBER WITH ID " + arg1);
                        ResultSet results1 = QueryService.queryLessonHistory(arg1);
                        printResults(results1);
                        break;
                    case "2":
                        System.out.print("\nEnter Ski Pass ID: ");
                        arg1 = userin.nextLine();

                        // execute
                        System.out.println("LIFT AND RENTAL RECORDS ASSOCIATED WITH SKI PASS OF ID " + arg1);
                        ResultSet results2 = QueryService.queryPassRentals(arg1);
                        printResults(results2);
                        results2 = QueryService.queryPassLogs(arg1);
                        printResults(results2);
                        break;
                    case "3":
                        // execute
                        System.out.println("INTERMEDIATE TRAILS AND ASSOCIATED LIFTS");
                        ResultSet results3 = QueryService.queryIntTrails();
                        printResults(results3, true);
                        for (String trail : tracker) {
                            System.out.println("LIFTS GOING TO INTERMEDIATE TRIAL WTH ID " + trail);
                            results3 = QueryService.queryLifts(trail);
                            printResults(results3);
                        }
                        break;
                    case "4":
                        System.out.print("\nEnter Property ID: ");
                        arg1 = userin.nextLine();
                        // execute
                        ResultSet results4 = QueryService.queryWageExpense(arg1);
                        printResults(results4);
                        break;
                    // SIMPLE QUERIES //
                    case "5":
                        ResultSet results5 = QueryService.queryMembers();
                        printResults(results5);
                        break;
                    case "6":
                        ResultSet results6 = QueryService.queryPasses();
                        printResults(results6);
                        break;
                    case "7":
                        ResultSet results7 = QueryService.queryEquipment();
                        printResults(results7);
                        break;
                    case "8":
                        ResultSet results8 = QueryService.queryRental();
                        printResults(results8);
                        break;
                    case "9":
                        ResultSet results9 = QueryService.queryLesson();
                        printResults(results9);
                        break;
                    case "10":
                        ResultSet results10 = QueryService.queryOrder();
                        printResults(results10);
                        break;
                    case "back":
                        return;
                    default:
                        System.out.println("ERROR: Invalid option selected.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid argument provided. Please try again.");
                input = "";
            } catch (RuntimeException e) {
                System.err.println("ERROR: Something went wrong. Please try again.");
                System.out.print("DB: ");
                e.printStackTrace();
            }

            // reset to initial state
            arg1 = "";
            input = "";

            System.out.println("\n\n//- AVAILABLE QUERIES -\\\\");
            System.out.println("1  | List all ski lessons purchased for a member");
            System.out.println("2  | List all lift and rental records for a ski pass");
            System.out.println("3  | List all intermediate-level trails and associated information.");
            System.out.println("4  | List the total wage expense for a given property");
            System.out.println("5  | List all members");
            System.out.println("6  | List all active ski passes");
            System.out.println("7  | List all equipment");
            System.out.println("8  | List all rentals");
            System.out.println("9  | List all lessons");
            System.out.println("10 | List all lesson orders ");
            System.out.println("Type 'back' to return to the main menu.");
        }

        return;
    }
    /*--------------------------------------------------------------------- 
    |  Method executionMenu
    |
    |  Purpose:  Executes the specific operation (Add, Delete, Update) based on user 
    |            input for a given entity (e.g., member, ski pass, rental). Handles
    |            input collection, validation, and calls the appropriate service class.
    |
    |  Pre-condition:  
    |      - actionState must be one of {"1", "2", "3"}.
    |      - context must be a valid entity keyword (e.g., "member", "ski", "equipment").
    |      - Scanner input and DB services must be initialized.
    |
    |  Post-condition:  
    |      - The selected action is applied to the database via the appropriate handler.
    |      - Success or error messages are printed to the console.
    |
    |  Parameters:
    |      actionState -- A string indicating the action to perform:
    |                     "1" for Add, "2" for Delete, "3" for Update.
    |      context     -- The entity type being operated on.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/

    private static void executionMenu(String actionState, String context) {

        try {
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
                        System.out.print("Date of Birth (yyyy-[m]m-[d]d): ");
                        String dateOfBirthString = userin.nextLine();
                        java.sql.Date dateOfBirth = java.sql.Date.valueOf(dateOfBirthString);
                        System.out.print("Emergency Telephone #: ");
                        Long emergency = userin.nextLong();
                        userin.nextLine();

                        // add member
                        long id = memberHandler.registerMember(name, telephoneNumber, email, dateOfBirth, emergency);
                        System.out.println("Added new member: " + id);
                    } else if (context.equals("ski")) {
                        // SKI PASS LOGIC //
                        System.out.print("Member ID: ");
                        long memid = userin.nextLong();
                        userin.nextLine();
                        System.out.print("Pass type: ");
                        String type = userin.next();
                        userin.nextLine();
                        System.out.print("Price: ");
                        int price = userin.nextInt();
                        userin.nextLine();
                        System.out.print("Uses: ");
                        int uses = userin.nextInt();
                        userin.nextLine();

                        // add pass
                        long id = passHandler.createSkiPass(memid, uses,
                                type, price);

                        System.out.println("Added new ski pass: " + id);

                    } else if (context.equals("equipment")) {
                        // EQUIPMENT LOGIC //
                        System.out.print("Equipment type: ");
                        String type = userin.nextLine();
                        System.out.print("Equipment size: ");
                        String size = userin.nextLine();

                        // add equipment
                        long id = equipmentHandler.createEquipment(type, size, "0");
                        System.out.println("Added new equipment record: " + id);
                    } else if (context.equals("rental")) {
                        // RENTAL LOGIC //
                        System.out.print("Equipment ID: ");
                        long equpid = userin.nextLong();
                        userin.nextLine();
                        System.out.print("Ski Pass ID: ");
                        long skiid = userin.nextLong();
                        userin.nextLine();

                        // add rental
                        long id = rentalHandler.registerRental(skiid, equpid);
                        System.out.println("Added new equipment rental: " + id);
                    } else if (context.equals("lesson")) {
                        // LESSON LOGIC //
                        System.out.print("Member ID: ");
                        long memid = userin.nextLong();
                        userin.nextLine();
                        System.out.print("Lesson ID: ");
                        long lesid = userin.nextLong();
                        userin.nextLine();
                        System.out.print("Sessions: ");
                        int sess = userin.nextInt();
                        userin.nextLine();

                        // add lesson
                        long id = lessonHandler.createLessonOrder(lesid, memid, sess);
                        System.out.println("Added new lesson order: " + id);
                    }
                    break;
                case "2":
                    // delete
                    if (context.equals("member")) {
                        System.out.print("Member ID: ");
                        long id = userin.nextLong();
                        userin.nextLine(); // clear input buffer

                        Optional<Member> values = memberHandler.findMemberById(id);

                        if (!values.isPresent()) {
                            System.out.println("WARNING: Member with id " + id + " does not exist. Try again.");
                            return;
                        }

                        if (memberHandler.memberPreCheck(values) == 1) {
                            System.out.println("WARNING: Member with id " + id + " is associated with other data.");
                            System.out.print("Are you sure you want to delete this member? (y/n): ");
                            String confirm = userin.nextLine().trim();

                            if (confirm.equalsIgnoreCase("y")) {
                                memberHandler.deleteMemberById(values.get());
                                System.out.println("Deleted member " + id);
                            } else {
                                System.out.println("Member deletion cancelled.");
                            }
                        } else {
                            // No associated data — safe to delete
                            memberHandler.deleteMemberById(values.get());
                            System.out.println("Deleted member " + id);
                        }
                    }

                    else if (context.equals("ski")) {
                        // SKI PASS LOGIC //
                        System.out.print("Pass ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // archive the pass
                        passHandler.archivePass(id, false);
                        System.out.println("Archived pass " + id);
                    } else if (context.equals("equipment")) {
                        // EQUIPMENT LOGIC //
                        System.out.print("Equipment ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // archive equipment
                        equipmentHandler.archiveEquipment(id);
                        System.out.println("Archived equipment " + id);
                    } else if (context.equals("rental")) {
                        // RENTAL LOGIC //
                        System.out.print("Rental ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // delete rental if it exists
                        Optional<Rental> values = rentalHandler.findRental(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Rental with id " + id + " does not exist. Try again.");
                            return;
                        }

                        rentalHandler.deleteRental(values.get());
                        System.out.println("Deleted rental record with id " + id);
                    } else if (context.equals("lesson")) {
                        // LESSON LOGIC //
                        System.out.print("Order ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // delete lesson order if it exists
                        Optional<LessonOrder> values = lessonHandler.findLessonOrderById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Lesson order with id " + id + " does not exist. Try again.");
                            return;
                        }

                        lessonHandler.deleteLessonOrder(values.get());
                        System.out.println("Deleted lesson order with id " + id);
                    }
                    break;
                case "3":
                    // update
                    if (context.equals("member")) {
                        // MEMBER LOGIC //
                        System.out.print("Member ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // grab the member in question
                        Optional<Member> values = memberHandler.findMemberById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Member with id " + id + " does not exist. Try again.");
                            return;
                        }

                        System.out.println("What would you like to update?");
                        System.out.println("1 | Email");
                        System.out.println("2 | Telephone #");
                        System.out.println("3 | Emergency Telephone #");

                        String field = userin.nextLine();
                        Member toUpdate;

                        if (field.equals("1")) {
                            // update email
                            System.out.print("New email: ");
                            String value = userin.nextLine();

                            toUpdate = new Member(id, values.get().name(), values.get().telephoneNumber(), value,
                                    values.get().dateOfBirth(), values.get().emergencyContact());
                        } else if (field.equals("2")) {
                            // update phone number
                            System.out.print("New telephone #: ");
                            String value = userin.nextLine();
                            toUpdate = new Member(id, values.get().name(), value, values.get().email(),
                                    values.get().dateOfBirth(), values.get().emergencyContact());
                        } else if (field.equals("3")) {
                            // update emergency phone number
                            System.out.print("New emergency telephone #: ");
                            Long value = userin.nextLong();
                            userin.nextLine();
                            toUpdate = new Member(id, values.get().name(), values.get().telephoneNumber(),
                                    values.get().email(), values.get().dateOfBirth(), value);
                        } else {
                            System.out.println("ERROR: Invalid option selected. Please try again.");
                            return;
                        }

                        // execute modification
                        memberHandler.updateMember(toUpdate);
                        System.out.println("Update on member " + id + " succesful.");
                    } else if (context.equals("ski")) {
                        // SKI PASS LOGIC //
                        System.out.print("Pass ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // update remaining uses
                        Optional<SkiPass> values = passHandler.findSkiPassById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Ski pass with id " + id + " does not exist. Try again.");
                            return;
                        }

                        System.out.print("New remaining uses: ");
                        int uses = userin.nextInt();
                        userin.nextLine();

                        passHandler.updatePassDetails(new SkiPass(values.get().passId(), values.get().memberId(),
                                values.get().purchaseDate(), values.get().totalUses(), uses, values.get().passType(),
                                values.get().price(), values.get().archivedStatus()));
                        System.out.print("Succesfully updated remaining number of uses on ski pass with id " + id);
                    } else if (context.equals("equipment")) {
                        // EQUIPMENT LOGIC //
                        System.out.print("Equipment ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        System.out.println("What would you like to update?");
                        System.out.println("1 | Type");
                        System.out.println("2 | Size");
                        System.out.println("3 | Archival Status");

                        // grab the equipment information
                        Optional<Equipment> values = equipmentHandler.findEquipment(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Equipment with id " + id + " does not exist. Try again.");
                            return;
                        }

                        String field = userin.nextLine();
                        Equipment toUpdate;

                        if (field.equals("1")) {
                            // update type
                            System.out.print("New type: ");
                            String value = userin.nextLine();

                            toUpdate = new Equipment(values.get().equipment_id(), value, values.get().dimension(),
                                    values.get().archived_status());
                        } else if (field.equals("2")) {
                            // update size
                            System.out.print("New size: ");
                            String value = userin.nextLine();

                            toUpdate = new Equipment(values.get().equipment_id(), values.get().type(), value,
                                    values.get().archived_status());
                        } else if (field.equals("3")) {
                            // update archival status
                            System.out.print("New status ");
                            int value = userin.nextInt();
                            userin.nextLine();

                            if (value != 1 && value != 0) {
                                System.out.println("ERROR: Archival status must be 1 or 0. Try again.");
                                return;
                            }

                            toUpdate = new Equipment(values.get().equipment_id(), values.get().type(),
                                    values.get().dimension(), value);
                        } else {
                            System.out.println("ERROR: Invalid option selected. Please try again.");
                            return;
                        }

                        equipmentHandler.updateEquipment(toUpdate);
                        System.out.println("Update on equipment " + id + " succesful.");
                    } else if (context.equals("rental")) {
                        // RENTAL LOGIC //
                        System.out.print("Rental ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // update the return status of the rental to returned
                        Optional<Rental> values = rentalHandler.findActiveRental(id);
                        Date today = Date.valueOf(LocalDate.now());
                        if (values.isPresent()) {
                            rentalHandler.updateRental(new Rental(values.get().rental_id(), values.get().pass_id(),
                                    values.get().equipment_id(), values.get().dateTime_From(), today));
                        } else {
                            System.out.println(
                                    "Rental status cannot be updated because it either does not exist or is not active.");
                            return;
                        }
                    } else if (context.equals("lesson")) {
                        // LESSON LOGIC //
                        System.out.print("Order ID: ");
                        long id = userin.nextLong();
                        userin.nextLine();

                        // update remaining sessions
                        Optional<LessonOrder> values = lessonHandler.findLessonOrderById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Lesson order with id " + id + " does not exist. Try again.");
                            return;
                        }

                        System.out.println("New remaining sessions: ");
                        int sessions = userin.nextInt();
                        userin.nextLine();

                        lessonHandler
                                .updateLessonOrder(new LessonOrder(values.get().order_id(), values.get().lesson_id(),
                                        values.get().member_id(), values.get().total_sessions(), sessions));
                        System.out.println("Succesfully updated remaining session on lesson order with id " + id);
                    }
                    break;
                default:
                    break;
            }
        } catch (

        Exception e) {
            System.err.println(
                    "ERROR: An error ocurred while attempting to execute the desired action. Please try again.");
            System.out.print("DB: ");
            e.printStackTrace();
            System.out.println();
            return;
        }
    }

    public static void main(String[] args) {
        // initialize database connection
        if (args.length != 2) {
            System.err.println("USAGE: Prog4.java <username> <password>");
            System.exit(1);
        }

        conn = new DatabaseConnection(args[0], args[1]);

        // initialize DB services
        QueryService.setConnection(conn);
        memberHandler = new MemberService(conn);
        equipmentHandler = new EquipmentService(conn);
        rentalHandler = new RentalService(conn);
        lessonHandler = new LessonOrderService(conn);
        passHandler = new SkiPassService(conn);

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
            try {
                switch (input) {
                    case "1":
                        // member
                        actionState = actionMenu("member");
                        context = "member";

                        executionMenu(actionState, context);
                        break;
                    case "2":
                        // ski pass
                        actionState = actionMenu("ski pass");
                        context = "ski";

                        executionMenu(actionState, context);
                        break;
                    case "3":
                        // equipment
                        actionState = actionMenu("equipment record");
                        context = "equipment";

                        executionMenu(actionState, context);
                        break;
                    case "4":
                        // rental
                        actionState = actionMenu("rental record");
                        context = "rental";

                        executionMenu(actionState, context);
                        break;
                    case "5":
                        // lesson
                        actionState = actionMenu("lesson purchase record");
                        context = "lesson";

                        executionMenu(actionState, context);
                        break;
                    case "query":
                        // queries
                        queryMenu();

                        break;
                    default:
                        break;
                }

            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid argument provided. Try again.");
            }

            System.out.println();

            input = "";
            // main menu print
            System.out.println("\n\n//- SKI RESORT DATABASE -\\\\");
            System.out.println("1 | Add, delete, or update a member.");
            System.out.println("2 | Add, delete, or update a ski pass.");
            System.out.println("3 | Add, delete, or update an equipment record.");
            System.out.println("4 | Add, update, or delete a rental record.");
            System.out.println("5 | Add, update, or delete a lesson purchase record.");
            System.out.println("Type 'query' to access the queries submenu.");
            System.out.println("Type 'exit' to close the application.");
        }
        
        // clean up and exit without error
        userin.close();
        QueryService.close();
        conn.close();

        System.exit(0);
    }
}
