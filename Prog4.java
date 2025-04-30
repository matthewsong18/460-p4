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
    private static DatabaseConnection conn;
    private static Scanner userin;
    public static void main(String[] args){
        // initialize database connection
        if (args.length != 2) {
            System.err.println("USAGE: Prog4.java <username> <password>");
            System.exit(1);
        } 
        conn = new DatabaseConnection(args[0], args[1]);

        userin = new Scanner(System.in);
        String input = "";
        while (userin.hasNextLine() && !(input = userin.nextLine().toLowerCase()).equals("exit")) {
            // TO DO: implement interface navigation
            /*
             * this loop should contain high level interface navigation
             * ie(1: manage members 2: manage ski passes 3: manage rentals 4: query reports)
             */

            // informational menu print
            System.out.println("//- SKI RESORT DATABASE -\\\\");
            System.out.println("Type 'exit' to close the application.");
        }

        userin.close();
        // TO DO: close database connection when finished

        System.exit(0);
    }
}
