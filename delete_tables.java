
/*------------------------------------------------------------------
* delete_tables.java – Ski Resort DB teardown
*
* Compilation on lectura:
*     export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:$CLASSPATH
*     javac delete_tables.java
*
* Execution:
*     java delete_tables
*
* Author:  Stephin Tomson
*------------------------------------------------------------------*/

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class delete_tables {

    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Usage: java delete_tables");
            System.exit(1);
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Oracle username: ");
        String user = sc.nextLine();
        System.out.print("Oracle password: ");
        String pass = sc.nextLine();
        sc.close();

        DatabaseConnection dbConn = new DatabaseConnection(user, pass);

        String[] tables = {
                "LessonOrder",
                "Lesson",
                "Instructor",
                "Employee",
                "EntryLog",
                "LiftTrail",
                "Lift",
                "Rental",
                "Equipment",
                "SkiPass",
                "Member",
                "ParkingLot",
                "Property",
                "Trail"
        };

        try (Connection conn = dbConn.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("\nDropping tables......................... \n");
            for (String tbl : tables) {
                try {
                    stmt.executeUpdate("DROP TABLE " + tbl + " CASCADE CONSTRAINTS");
                    System.out.println("Dropped " + tbl);
                } catch (SQLException ex) {
                    System.out.println("  • " + tbl + "  (skipped: " + ex.getMessage() + ")");
                }
            }
            System.out.println("Complete.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }
}
