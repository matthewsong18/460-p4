/*
 * Prog4.java
 * 
 * Main class for ski resort database user interface.
 * 
 * Authors: Elias Zheng, Ethan Huang, Matthew Song, Stephin Tomson
 * 
 * Usage: Prog4.java <username> <password>
 */
package main;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.management.Query;

import java.lang.*;
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

    private static void printResults(ResultSet results){
        try{
            if (results == null) {
                System.out.println("| NO RESULTS |");
                return;
            }

            ResultSetMetaData meta = results.getMetaData();
            
            // print attribute names
            StringBuilder builder = new StringBuilder("|");
            for (int i = 1; i <= meta.getColumnCount(); i++){
                builder.append(String.format("| %-25s |", meta.getColumnName(i)));
            }
            builder.append("|");
            System.out.println(builder.toString());

            // print all tuples
            int count = 0;
            while (results.next()) {
                count++;
                builder = new StringBuilder("|");
                for (int i = 1; i <= meta.getColumnCount(); i++){
                    builder.append(String.format("| %-25s |", results.getString(i)));
                }
                builder.append("|");
                System.out.println(builder.toString());
            }

            if (count == 0) {
                System.out.println(String.format("|| %-25s || ", "NO RESULTS"));
            }
        } catch (SQLException e){
            throw new RuntimeException("ERROR: " + e.getMessage());
        }
    }

    private static void printResults(ResultSet results, boolean track){
        try{
            if (results == null) {
                System.out.println("| NO RESULTS |");
                return;
            }

            ResultSetMetaData meta = results.getMetaData();
            
            // print attribute names
            StringBuilder builder = new StringBuilder("|");
            for (int i = 1; i <= meta.getColumnCount(); i++){
                builder.append(String.format("| %-25s |", meta.getColumnName(i)));
            }
            builder.append("|");
            System.out.println(builder.toString());

            // print all tuples
            int count = 0;
            while (results.next()) {
                count++;
                builder = new StringBuilder("|");
                for (int i = 1; i <= meta.getColumnCount(); i++){
                    builder.append(String.format("| %-25s |", results.getString(i)));
                }
                builder.append("|");
                tracker.add(results.getString(1));
                System.out.println(builder.toString());
            }

            if (count == 0) {
                System.out.println(String.format("|| %-25s ||b ", "NO RESULTS"));
            }
        } catch (SQLException e){
            throw new RuntimeException("ERROR: " + e.getMessage());
        }
    }
  
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

        if (input.equals("back")) {
            return "";
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
        System.out.println("\n\n//- AVAILABLE QUERIES -\\\\");
        System.out.println("1 | List all ski lessons purchased for a member");
        System.out.println("2 | List all lift and rental records for a ski pass");
        System.out.println("3 | List all intermediate-level trails and associated information.");
        System.out.println("4 | List the total wage expense for a given property");
        System.out.println("5 | List all members");
        System.out.println("6 | List all ski passes");
        System.out.println("7 | List all equipment");
        System.out.println("8 | List all rentals");
        System.out.println("9 | List all lesson orders");
        System.out.println("Type 'back' to return to the main menu.");

        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("back")){
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
                        for (String trail : tracker){
                            System.out.println("LIFTS GOING TO INTERMEDIATE TRIAL WTH ID " + trail);
                            results3 = QueryService.queryLifts(trail);
                            printResults(results3);
                        }
                        break;
                    case "4":
                        System.out.print("\nEnter Property ID: ");
                        arg1 = userin.nextLine();
                        //execute
                        ResultSet results4 = QueryService.queryWageExpense(arg1);
                        printResults(results4);
                        break;
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
                    case "back":
                        return;
                    default:
                        System.out.println("ERROR: Invalid option selected.");
                        break;
                }
            } catch (InputMismatchException e){
                System.out.println("ERROR: Invalid argument provided. Please try again.");
                input = "";
            } catch (RuntimeException e){
                System.err.println("ERROR: Something went wrong. Please try again.");
                System.out.println("DB: " + e.getMessage());
            }

            arg1 = "";
            input = "";

            System.out.println("\n\n//- AVAILABLE QUERIES -\\\\");
            System.out.println("1 | List all ski lessons purchased for a member");
            System.out.println("2 | List all lift and rental records for a ski pass");
            System.out.println("3 | List all intermediate-level trails and associated information.");
            System.out.println("4 | List the total wage expense for a given property");
            System.out.println("5 | List all members");
            System.out.println("6 | List all ski passes");
            System.out.println("7 | List all equipment");
            System.out.println("8 | List all rentals");
            System.out.println("9 | List all lesson orders");
            System.out.println("Type 'back' to return to the main menu.");
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
                        long memid = userin.nextLong();
                        System.out.print("Pass type: ");
                        String type = userin.next();
                        System.out.print("Price: ");
                        int price = userin.nextInt();
                        System.out.print("Uses: ");
                        int uses = userin.nextInt();

                        // add pass
                        long id = passHandler.createSkiPass(memid, new Timestamp(System.currentTimeMillis()), uses, type, price);
                        System.out.println("Added new ski pass: ");
                    } else if (context.equals("equipment")){
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
                        System.out.print("Ski Pass ID: ");
                        long skiid = userin.nextLong();
                        
                        long id = rentalHandler.registerRental(skiid, equpid, 0);
                        System.out.println("Added new equipment rental: " + id);
                    } else if (context.equals("lesson")){
                        // LESSON LOGIC //
                        System.out.print("Member ID: ");
                        long memid = userin.nextLong();
                        System.out.print("Lesson ID: ");
                        long lesid = userin.nextLong();
                        System.out.print("Sessions: ");
                        int sess = userin.nextInt();
                        
                        // add lesson
                        long id = lessonHandler.createLessonOrder(lesid, memid, sess);
                        System.out.println("Added new lesson order: " + id);
                    }
                    break;
                case "2":
                    // delete
                    if (context.equals("member")) {
                        // MEMBER LOGIC //
                        System.out.print("Member ID: ");
                        long id = userin.nextLong();

                        Optional<Member> values = memberHandler.findMemberById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Member with id " + id + " does not exist. Try again.");
                            return;
                        }

                        // delete member
                        memberHandler.deleteMemberById(values.get());
                        System.out.println("Deleted member " + id);
                    } else if (context.equals("ski")) {
                        // SKI PASS LOGIC //
                        System.out.print("Pass ID: ");
                        long id = userin.nextLong();

                        passHandler.archivePass(id, false);

                        System.out.println("Archived pass " + id);
                    } else if (context.equals("equipment")) {
                        // EQUIPMENT LOGIC //
                        System.out.print("Equipment ID: ");
                        long id = userin.nextLong();
                        
                        equipmentHandler.archiveEquipment(id);
                
                        System.out.println("Archived equipment " + id);
                    } else if (context.equals("rental")){
                        // RENTAL LOGIC //
                        System.out.print("Rental ID: ");
                        long id = userin.nextLong();

                        Optional<Rental> values = rentalHandler.findRental(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Rental with id " + id + " does not exist. Try again.");
                            return;
                        }

                        rentalHandler.deleteRental(values.get());
                        System.out.println("Deleted rental record with id " + id);
                    } else if (context.equals("lesson")){
                        // LESSON LOGIC //
                        System.out.print("Order ID: ");
                        long id = userin.nextLong();
                        
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
                    } else if (context.equals("ski")) {
                        // SKI PASS LOGIC //
                        System.out.print("Pass ID: ");
                        long id = userin.nextLong();

                        Optional<SkiPass> values = passHandler.findSkiPassById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Ski pass with id " + id + " does not exist. Try again.");
                            return;
                        }

                        System.out.print("New remaining uses: " );
                        int uses = userin.nextInt();
                        
                        passHandler.updatePassDetails(new SkiPass(values.get().passId(), values.get().memberId(), values.get().purchaseDate(), values.get().totalUses(), uses, values.get().passType(), values.get().price(), values.get().archivedStatus()));
                        System.out.print("Succesfully updated remaining number of uses on ski pass with id " + id);
                    } else if (context.equals("equipment")) {
                        // EQUIPMENT LOGIC //
                        System.out.print("Equipment ID: ");
                        long id = userin.nextLong();

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
                            // update email
                            System.out.print("New type: ");
                            String value = userin.nextLine();

                            toUpdate = new Equipment(values.get().equipment_id(), value, values.get().dimension(), values.get().archived_status());
                        } else if (field.equals("2")){
                            // update phone number
                            System.out.print("New size: ");
                            String value = userin.nextLine();

                            toUpdate = new Equipment(values.get().equipment_id(), values.get().type(), value, values.get().archived_status());
                        } else if (field.equals("3")) {
                            // update emergency phone number
                            System.out.print("New status ");
                            int value = userin.nextInt();
                            
                            if (value != 1 && value != 0) {
                                System.out.println("ERROR: Archival status must be 1 or 0. Try again.");
                                return;
                            }

                            toUpdate = new Equipment(values.get().equipment_id(), values.get().type(), values.get().dimension(), value);
                        } else {
                            System.out.println("ERROR: Invalid option selected. Please try again.");
                            return;
                        }
                    
                        equipmentHandler.updateEquipment(toUpdate);
                        System.out.println("Update on equipment " + id + " succesful.");
                    } else if (context.equals("rental")){
                        // RENTAL LOGIC //
                        System.out.print("Rental ID: ");
                        long id = userin.nextLong();

                        Optional<Rental> values = rentalHandler.findActiveRental(id);
                        if (!values.isPresent()) {
                            System.out.println("Rental status cannot be updated because it either does not exist or is not active.");
                            return;
                        }

                        rentalHandler.updateRental(new Rental(values.get().rental_id(), values.get().ski_pass_id(), values.get().equipment_id(), values.get().dateTimeFrom(), 1));
                    } else if (context.equals("lesson")){
                        // LESSON LOGIC //
                        System.out.print("Order ID: ");
                        long id = userin.nextLong();
                        
                        Optional<LessonOrder> values = lessonHandler.findLessonOrderById(id);
                        if (!values.isPresent()) {
                            System.out.println("WARNING: Lesson order with id " + id + " does not exist. Try again.");
                            return;
                        }
                        
                        System.out.println("New remaining sessions: ");
                        int sessions = userin.nextInt();

                        lessonHandler.updateLessonOrder(new LessonOrder(values.get().order_id(), values.get().lesson_id(), values.get().member_id(), values.get().total_sessions(), sessions));
                        System.out.println("Succesfully updated remaining session on lesson order with id " + id);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            System.err.println("ERROR: An error ocurred while attempting to execute the desired action. Please try again.");
            System.out.println("DB: " + e.getMessage());
            System.out.println();
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
        QueryService.setConnection(conn);
        memberHandler = new MemberService(conn);
        equipmentHandler = new EquipmentService(conn);
        rentalHandler = new RentalService(conn);
        lessonHandler = new LessonOrderService(conn);
        passHandler  = new SkiPassService(conn);

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

        userin.close();
        conn.close();

        System.exit(0);
    }
}
