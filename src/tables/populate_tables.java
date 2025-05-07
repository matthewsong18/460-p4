package tables;
/*
 * FixedCsvSeederNoLib – loads a fixed set of CSVs (no external libs)
 *
 * Compile:
 *    export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:$CLASSPATH
 *   javac -cp "$CP" DatabaseConnection.java FixedCsvSeederNoLib.java
 *
 * Run:
 *   java  -cp "$CP" FixedCsvSeederNoLib
 */

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

    /* ------------------------------------------------------------------ */
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
