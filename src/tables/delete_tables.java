/*
 * Author: Stephin Tomson
 */
package tables;
/*+----------------------------------------------------------------------
 ||
 ||  Class delete_tables 
 ||
 ||         Author:  Stephin Tomson
 ||
 ||        Purpose:  This class connects to an Oracle database and drops
 ||                  all tables related to the Ski Resort schema. It is
 ||                  used as a quick cleanup utility to reset the schema 
 ||                  before recreation.
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  None. (This class contains only static methods.)
 ||
 ||  Class Methods:  
 ||      - main(String[] args): void
 ||          Handles command-line invocation, collects DB credentials,
 ||          connects to the Oracle database, and deletes all specified tables.
 ||
 ||  Inst. Methods:  None.
 ||
 ++-----------------------------------------------------------------------*/

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import common.DatabaseConnection;

/*---------------------------------------------------------------------
 |  Method [Method Name]
 |
 |  Purpose:  [Explain what this method does to support the correct
 |      operation of its class, and how it does it.]
 |
 |  Pre-condition:  [Any non-obvious conditions that must exist
 |      or be true before we can expect this method to function
 |      correctly.]
 |
 |  Post-condition: [What we can expect to exist or be true after
 |      this method has executed under the pre-condition(s).]
 |
 |  Parameters:
 |      parameter_name -- [Explanation of the purpose of this
 |          parameter to the method.  Write one explanation for each
 |          formal parameter of this method.]
 |
 |  Returns:  [If this method sends back a value via the return
 |      mechanism, describe the purpose of that value here, otherwise
 |      state 'None.']
 *-------------------------------------------------------------------*/
public class delete_tables {

    /*---------------------------------------------------------------------
     |  Method main
     |
     |  Purpose:  Connects to the Oracle database using user credentials,
     |            and iterates through a predefined list of Ski Resort
     |            tables, attempting to drop each one with CASCADE CONSTRAINTS.
     |
     |  Pre-condition:  The Oracle JDBC driver must be available and 
     |                  the user must have sufficient privileges to drop
     |                  the tables listed in the schema.
     |
     |  Post-condition: All existing Ski Resort tables will be dropped if
     |                  present. Any tables not found will be skipped.
     |
     |  Parameters:
     |      args -- A string array from the command-line arguments. Must be empty;
     |              otherwise, the program exits with a usage message.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
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
