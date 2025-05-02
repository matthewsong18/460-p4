
/*
* SkiResortDBSetup.java
*
* To compile and execute this program on lectura:
*
*   Add the Oracle JDBC driver to your CLASSPATH environment variable:
*         export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
*
*   Compile:
*         javac create_tables.java
*
*   Run:
*         java create_tables
*
* Author: Stephin Tomson
* Date:   Spring 2025
*/

import java.sql.*;
import java.util.Scanner;

public class create_tables {
    public static void main(String[] args) {
        // No command-line arguments needed
        if (args.length != 0) {
            System.out.println("Usage: java SkiResortDBSetup");
            System.exit(1);
        }

        String username;
        String password;
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

            // Start creating tables
            System.out.println("Creating tables...");

            stmt.executeUpdate("CREATE TABLE Property (" +
                    "property_id NUMBER PRIMARY KEY," +
                    "type VARCHAR2(30)," +
                    "monthly_income NUMBER," +
                    "location VARCHAR2(100))");

            stmt.executeUpdate("CREATE TABLE ParkingLot (" +
                    "lot_id NUMBER PRIMARY KEY," +
                    "property_id NUMBER REFERENCES Property(property_id)," +
                    "fee NUMBER," +
                    "total_capacity NUMBER," +
                    "occupancy NUMBER)");

            stmt.executeUpdate("CREATE TABLE Member (" +
                    "member_id NUMBER PRIMARY KEY," +
                    "name VARCHAR2(50)," +
                    "telephone VARCHAR2(20)," +
                    "email VARCHAR2(50)," +
                    "date_of_birth DATE," +
                    "emergency_contact VARCHAR2(50))");

            stmt.executeUpdate("CREATE TABLE SkiPass (" +
                    "pass_id NUMBER PRIMARY KEY," +
                    "member_id NUMBER REFERENCES Member(member_id)," +
                    "purchase_datetime TIMESTAMP," +
                    "total_uses NUMBER," +
                    "remaining_uses NUMBER," +
                    "type VARCHAR2(30)," +
                    "price NUMBER)");

            stmt.executeUpdate("CREATE TABLE Equipment (" +
                    "equipment_id NUMBER PRIMARY KEY," +
                    "type VARCHAR2(30)," +
                    "length_size NUMBER," +
                    "archived CHAR(1))");

            stmt.executeUpdate("CREATE TABLE Rental (" +
                    "rental_id NUMBER PRIMARY KEY," +
                    "pass_id NUMBER REFERENCES SkiPass(pass_id)," +
                    "equipment_id NUMBER REFERENCES Equipment(equipment_id)," +
                    "datetime_from TIMESTAMP," +
                    "datetime_to TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE Trail (" +
                    "trail_id NUMBER PRIMARY KEY," +
                    "trail_name VARCHAR2(50)," +
                    "start_location VARCHAR2(100)," +
                    "end_location VARCHAR2(100)," +
                    "status VARCHAR2(30)," +
                    "difficulty_level VARCHAR2(20)," +
                    "category VARCHAR2(30))");

            stmt.executeUpdate("CREATE TABLE Lift (" +
                    "lift_id NUMBER PRIMARY KEY," +
                    "lift_name VARCHAR2(50)," +
                    "ability_level NUMBER," +
                    "trail_id NUMBER REFERENCES Trail(trail_id)," +
                    "open_time VARCHAR2(20)," +
                    "close_time VARCHAR2(20))");

            stmt.executeUpdate("CREATE TABLE EntryLog (" +
                    "entry_id NUMBER PRIMARY KEY," +
                    "lift_id NUMBER REFERENCES Lift(lift_id)," +
                    "pass_id NUMBER REFERENCES SkiPass(pass_id)," +
                    "datetime TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE Employee (" +
                    "employee_id NUMBER PRIMARY KEY," +
                    "age NUMBER," +
                    "gender VARCHAR2(10)," +
                    "position VARCHAR2(30)," +
                    "property_id NUMBER REFERENCES Property(property_id)," +
                    "salary NUMBER," +
                    "start_date DATE)");

            stmt.executeUpdate("CREATE TABLE Instructor (" +
                    "employee_id NUMBER PRIMARY KEY REFERENCES Employee(employee_id)," +
                    "psia_aasi_certification_level VARCHAR2(20))");

            stmt.executeUpdate("CREATE TABLE Lesson (" +
                    "instructor_id NUMBER REFERENCES Instructor(employee_id)," +
                    "student_id NUMBER REFERENCES Member(member_id)," +
                    "datetime TIMESTAMP," +
                    "PRIMARY KEY (instructor_id, student_id, datetime))");

            System.out.println("All tables created successfully.");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }
}
