/*+----------------------------------------------------------------------
 ||
 ||  Class populate_tables 
 ||
 ||         Author:  Stephin Tomson
 ||
 ||        Purpose:  This class loads initial data into the Ski Resort database 
 ||                  tables from corresponding CSV files. It handles clearing 
 ||                  tables, reading CSV data, and inserting records using JDBC.
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  
 ||          - CSV_DIR: Path to the directory containing all CSV seed files.
 ||          - TABLES: A 2D array specifying the FK-safe loading order of
 ||                     tables and their associated CSV filenames.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  
 ||          - None. All methods and fields are static.
 ||
 ||  Class Methods:  
 ||      - main(String[] args): void  
 ||            Connects to the database, clears all table contents, and seeds
 ||            each table from its corresponding CSV file.
 ||
 ||  Inst. Methods:  
 ||          - None.
 ||
 ++-----------------------------------------------------------------------*/

package tables;

import java.nio.file.*;
import java.sql.*;
import java.util.*;
import common.DatabaseConnection;

/** Simple, no‑dependency CSV seeder (commas inside values not supported). */
public class populate_tables {

    /* Where the CSVs live */
    private static final Path CSV_DIR = Paths.get("../data");

    /* FK‑safe load order: { TABLE , fileName } */
    private static final String[][] TABLES = {
            { "PROPERTY", "property.csv" },
            { "PARKINGLOT", "parkinglot.csv" },
            { "TRAIL", "trail.csv" },
            { "LIFT", "lift.csv" },
            { "LIFTTRAIL", "lifttrail.csv" },
            { "MEMBER", "member.csv" },
            { "SKIPASS", "skipass.csv" },
            { "EQUIPMENT", "equipment.csv" },
            { "RENTAL", "rental.csv" },
            { "EMPLOYEE", "employee.csv" },
            { "INSTRUCTOR", "instructor.csv" },
            { "LESSON", "lesson.csv" },
            { "LESSONORDER", "lessonorder.csv" }
    };
    /*---------------------------------------------------------------------
     |  Method clearTables
     |
     |  Purpose:  Deletes all existing data from the specified database tables
     |            in reverse order of their creation to maintain foreign key
     |            integrity during deletion.
     |
     |  Pre-condition: The database connection must be valid and open. The tables
     |                 specified in TABLES must already exist in the database.
     |
     |  Post-condition: All records in the specified tables are deleted and the
     |                  transaction is committed.
     |
     |  Parameters:
     |      conn -- Active JDBC Connection to the Oracle database.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    private static void clearTables(Connection conn) throws SQLException {
        System.out.println("Clearing existing table contents...");
        for (int i = TABLES.length - 1; i >= 0; i--) { // Reverse order for FK safety
            String table = TABLES[i][0];
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM " + table);
                System.out.println("Cleared: " + table);
            } catch (SQLException e) {
                System.err.println("Error clearing " + table + ": " + e.getMessage());
            }
        }
        conn.commit();
    }
    /*---------------------------------------------------------------------
     |  Method main
     |
     |  Purpose:  Collects user credentials, establishes a connection to the
     |            Oracle database, clears all tables, and populates them
     |            with data from CSV files in the correct order.
     |
     |  Pre-condition: All CSV files must exist in the specified data directory,
     |                 and the database must be accessible with the given credentials.
     |
     |  Post-condition: All listed tables are cleared and then repopulated
     |                  with data from their respective CSV files.
     |
     |  Parameters:
     |      args -- Command-line arguments (not used).
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.print("Oracle username: ");
        String user = sc.nextLine();
        System.out.print("Oracle password: ");
        String pass = sc.nextLine();
        sc.close();

        DatabaseConnection db = new DatabaseConnection(user, pass);

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            clearTables(conn);
            for (String[] pair : TABLES) {
                seedTable(conn, pair[0], CSV_DIR.resolve(pair[1]));
            }
        }
    }

    /*---------------------------------------------------------------------
     |  Method seedTable
     |
     |  Purpose:  Reads a CSV file line-by-line and inserts its contents into
     |            the corresponding table using a prepared SQL INSERT statement.
     |            Uses batching for efficient data loading.
     |
     |  Pre-condition: The CSV file must exist and follow a simple comma-separated
     |                 format with the first line as the header. No embedded commas
     |                 or quotes in values are allowed.
     |
     |  Post-condition: The target table is populated with all valid rows from
     |                  the CSV file, and the transaction is committed.
     |
     |  Parameters:
     |      conn -- Active JDBC Connection to the Oracle database.
     |      table -- The name of the table to be populated.
     |      csvPath -- The file system path to the CSV file containing the data.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    private static void seedTable(Connection conn,
            String table,
            Path csvPath) throws Exception {

        List<String> lines = Files.readAllLines(csvPath);
        if (lines.isEmpty()) {
            System.out.printf("• %-11s  (empty file)%n", table);
            return;
        }

        /* header -> column list */
        String[] headers = lines.get(0).trim().split(",");
        String placeholders = "?,".repeat(headers.length);
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        System.out.println("Seeding table: " + table);
        System.out.println("Headers: " + Arrays.toString(headers));
        System.out.println(
                "SQL: INSERT INTO " + table + " (" + String.join(",", headers) + ") VALUES (" + placeholders + ")");

        String sql = "INSERT INTO " + table + " (" +
                String.join(",", headers) + ") VALUES (" + placeholders + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            int batch = 0, total = 0;
            for (int i = 1; i < lines.size(); i++) { // skip header
                String[] vals = lines.get(i).split(",", -1); // keep empties
                for (int j = 0; j < headers.length; j++) {
                    String v = (j < vals.length) ? vals[j].trim() : "";
                    if (v.isEmpty())
                        ps.setNull(j + 1, Types.VARCHAR);
                    else
                        ps.setString(j + 1, v);
                }
                ps.addBatch();
                if (++batch == 500) {
                    ps.executeBatch();
                    conn.commit();
                    total += batch;
                    batch = 0;
                }
            }
            if (batch > 0) {
                ps.executeBatch();
                conn.commit();
                total += batch;
            }

            System.out.printf("%-11s  %d rows%n", table, total);
        }
    }
}
