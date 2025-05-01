package Manage_DB;

/*
* delete_tables.java
*
* To compile and execute this program on lectura:
*
*   export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
*   javac delete_tables.java
*   java delete_tables
*
* Author: Stephin Tomson
* Date:   Spring 2025
*/

import java.sql.*;
import java.util.Scanner;

public class delete_tables {
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Usage: java delete_tables");
            System.exit(1);
        }

        String username, password;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Oracle username: ");
        username = scanner.nextLine();
        System.out.print("Enter Oracle password: ");
        password = scanner.nextLine();
        scanner.close();
        final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading Oracle JDBC driver.");
            System.exit(1);
        }

        try (Connection conn = DriverManager.getConnection(oracleURL, username, password)) {
            Statement stmt = conn.createStatement();

            System.out.println("Dropping tables...");

            String[] tables = {
                    "Lesson",
                    "Instructor",
                    "EntryLog",
                    "Lift",
                    "Trail",
                    "Rental",
                    "Equipment",
                    "SkiPass",
                    "Member",
                    "Employee",
                    "ParkingLot",
                    "Property"
            };

            for (String table : tables) {
                try {
                    stmt.executeUpdate("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                    System.out.println("Dropped table: " + table);
                } catch (SQLException e) {
                    System.err.println("Could not drop table " + table + ": " + e.getMessage());
                }
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }
}
