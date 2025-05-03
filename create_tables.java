/*+----------------------------------------------------------------------
 ||
 ||  Class create_tables
 ||
 ||        Author:  Stephin Tomson
 ||
 ||     Purpose:  Drops any existing Ski‑Resort tables and rebuilds the full
 ||               schema.  Relies on the {@code DatabaseConnection} helper to
 ||               establish a JDBC connection to Oracle.
 ||
 ||     Compile on lectura:
 ||         export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:$CLASSPATH
 ||         javac create_tables.java DatabaseConnection.java
 ||
 ||     Execute:
 ||         java create_tables
 ||
 ||     Notes:  • Idempotent – safe to run multiple times.
 ||             • Uses child→parent drop order to avoid FK violations.
 ||
 ++----------------------------------------------------------------------*/

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class create_tables {

    /*------------------------------------------------------------------
     |  Method dropTables
     |
     |  Purpose:  Removes existing tables so that the build is repeatable
     |            without manual cleanup.  Tables are dropped in strict
     |            child‑to‑parent order to satisfy foreign‑key constraints.
     |
     |  Pre‑condition:  Caller holds an active {@code Statement} object.
     |  Post‑condition:  Any listed tables that exist are gone.
     *-----------------------------------------------------------------*/
    private static void dropTables(Statement stmt) throws SQLException {
        String[] tables = {
                "LessonOrder", "Lesson", "Instructor", "Employee", "EntryLog",
                "LiftTrail", "Lift", "Rental", "Equipment", "SkiPass", "Member",
                "ParkingLot", "Property", "Trail"
        };
        System.out.println("Dropping old tables (ignore errors)…");
        for (String tbl : tables) {
            try {
                stmt.executeUpdate("DROP TABLE " + tbl + " CASCADE CONSTRAINTS");
                System.out.println("Dropped " + tbl);
            } catch (SQLException e) {
                // Table might not exist – ignore.
            }
        }
        System.out.println("Drop phase complete.\n");
    }

    /*------------------------------------------------------------------
     |  Method main
     |
     |  Purpose:  Entry‑point.  Prompts for Oracle credentials, obtains a
     |            connection, drops any existing schema, and rebuilds all
     |            tables with the correct foreign‑key relationships.
     *-----------------------------------------------------------------*/
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Usage: java create_tables");
            System.exit(1);
        }

        /* —— Collect credentials from user —— */
        Scanner sc = new Scanner(System.in);
        System.out.print("Oracle username: ");
        String user = sc.nextLine();
        System.out.print("Oracle password: ");
        String pass = sc.nextLine();
        sc.close();

        /* —— Connect through helper —— */
        DatabaseConnection dbConn = new DatabaseConnection(user, pass);

        try (Connection conn = dbConn.getConnection();
                Statement stmt = conn.createStatement()) {

            // deletes tables before creating new ones
            dropTables(stmt);
            System.out.println("Creating tables …\n");

            /* ———————— Property & ParkingLot ———————— */
            try {
                stmt.executeUpdate("CREATE TABLE Property (" +
                        "property_id NUMBER PRIMARY KEY, " +
                        "property_type VARCHAR2(30) NOT NULL, " +
                        "property_monthly_income NUMBER(20,2), " +
                        "location VARCHAR2(100))");
            } catch (SQLException e) {
                System.out.println("Error creating Property table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE ParkingLot (" +
                        "lot_id NUMBER PRIMARY KEY, " +
                        "property_id NUMBER NOT NULL, " +
                        "fee NUMBER(10,2), total_capacity NUMBER, occupancy NUMBER, " +
                        "FOREIGN KEY(property_id) REFERENCES Property(property_id))");
            } catch (SQLException e) {
                System.out.println("Error creating ParkingLot table: " + e.getMessage());
            }
            /* ———————— Member & SkiPass ———————— */
            try {
                stmt.executeUpdate("CREATE TABLE Member (" +
                        "member_id NUMBER PRIMARY KEY, member_name VARCHAR2(50) NOT NULL, " +
                        "telephone VARCHAR2(20), email VARCHAR2(50), date_of_birth DATE, " +
                        "emergency_contact NUMBER(10))");
            } catch (SQLException e) {
                System.out.println("Error creating Member table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE SkiPass (" +
                        "pass_id NUMBER PRIMARY KEY, member_id NUMBER NOT NULL, purchase_date DATE, " +
                        "total_uses NUMBER, remaining_uses NUMBER, pass_type VARCHAR2(30), price NUMBER(10,2), archived_status VARCHAR2(30), "
                        +
                        "FOREIGN KEY(member_id) REFERENCES Member(member_id) ON DELETE CASCADE)");
            } catch (SQLException e) {
                System.out.println("Error creating SkiPass table: " + e.getMessage());
            }
            /* ———————— Equipment & Rental ———————— */
            try {
                stmt.executeUpdate("CREATE TABLE Equipment (" +
                        "equipment_id NUMBER PRIMARY KEY, " +
                        "equipment_type VARCHAR2(30) NOT NULL, " +
                        "dimension VARCHAR2(10), " +
                        "archived_status NUMBER(1))");

            } catch (SQLException e) {
                System.out.println("Error creating Equipment table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE Rental (" +
                        "rental_id NUMBER PRIMARY KEY, pass_id NUMBER NOT NULL, equipment_id NUMBER NOT NULL, " +
                        "datetime_from TIMESTAMP, datetime_to TIMESTAMP, " +
                        "FOREIGN KEY(pass_id) REFERENCES SkiPass(pass_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(equipment_id) REFERENCES Equipment(equipment_id))");
            } catch (SQLException e) {
                System.out.println("Error creating Rental table: " + e.getMessage());
            }
            /* ———————— Trail / Lift / LiftTrail / EntryLog —— —————— */
            try {
                stmt.executeUpdate("CREATE TABLE Trail (" +
                        "trail_id NUMBER PRIMARY KEY, trail_name VARCHAR2(50) NOT NULL, " +
                        "start_location VARCHAR2(100), end_location VARCHAR2(100), status VARCHAR2(20), " +
                        "difficulty NUMBER(1), category VARCHAR2(30))");
            } catch (SQLException e) {
                System.out.println("Error creating Trail table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE Lift (" +
                        "lift_id NUMBER PRIMARY KEY, lift_name VARCHAR2(50) NOT NULL, " +
                        "open_time VARCHAR2(20),difficulty_level NUMBER(1), close_time VARCHAR2(20), location VARCHAR2(100))");
            } catch (SQLException e) {
                System.out.println("Error creating Lift table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE LiftTrail (" +
                        "lift_id NUMBER NOT NULL, trail_id NUMBER NOT NULL, " +
                        "PRIMARY KEY (lift_id, trail_id), " +
                        "FOREIGN KEY(lift_id) REFERENCES Lift(lift_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(trail_id) REFERENCES Trail(trail_id) ON DELETE CASCADE)");
            } catch (SQLException e) {
                System.out.println("Error creating LiftTrail table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE EntryLog (" +
                        "entry_id NUMBER PRIMARY KEY, lift_id NUMBER NOT NULL, pass_id NUMBER NOT NULL, " +
                        "datetime TIMESTAMP, " +
                        "FOREIGN KEY(lift_id) REFERENCES Lift(lift_id), " +
                        "FOREIGN KEY(pass_id) REFERENCES SkiPass(pass_id) ON DELETE CASCADE)");
            } catch (SQLException e) {
                System.out.println("Error creating EntryLog table: " + e.getMessage());
            }
            /* ———————— Employee / Instructor ———————— */
            try {
                stmt.executeUpdate("CREATE TABLE Employee (" +
                        "employee_id NUMBER PRIMARY KEY, employee_name VARCHAR2(50) NOT NULL, " +
                        "employee_age NUMBER, employee_gender VARCHAR2(10), employee_position VARCHAR2(30), " +
                        "property_id NUMBER NOT NULL, salary NUMBER, start_date DATE, " +
                        "FOREIGN KEY(property_id) REFERENCES Property(property_id))");
            } catch (SQLException e) {
                System.out.println("Error creating Employee table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE Instructor (" +
                        "employee_id NUMBER PRIMARY KEY, certification_level VARCHAR2(20), " +
                        "FOREIGN KEY(employee_id) REFERENCES Employee(employee_id) ON DELETE CASCADE)");
            } catch (SQLException e) {
                System.out.println("Error creating Instructor table: " + e.getMessage());
            }
            /* ———————— Lesson & LessonOrder ———————— */
            try {
                stmt.executeUpdate("CREATE TABLE Lesson (" +
                        "lesson_id NUMBER PRIMARY KEY, instructor_id NUMBER NOT NULL, lesson_time TIMESTAMP NOT NULL, "
                        +
                        "FOREIGN KEY(instructor_id) REFERENCES Instructor(employee_id))");
            } catch (SQLException e) {
                System.out.println("Error creating Lesson table: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("CREATE TABLE LessonOrder (" +
                        "order_id NUMBER PRIMARY KEY, lesson_id NUMBER NOT NULL, member_id NUMBER NOT NULL, " +
                        "total_sessions NUMBER, remaining_sessions NUMBER, " +
                        "FOREIGN KEY(lesson_id) REFERENCES Lesson(lesson_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(member_id) REFERENCES Member(member_id) ON DELETE CASCADE)");
            } catch (SQLException e) {
                System.out.println("Error creating LessonOrder table: " + e.getMessage());
            }
            System.out.println("All tables created successfully.");

            /* Triggers */
            // Trigger 1: On Trail changes
            stmt.executeUpdate(
                    "CREATE OR REPLACE TRIGGER trg_update_lift_difficulty_from_trail " +
                            "AFTER INSERT OR UPDATE OF difficulty OR DELETE ON Trail " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "  FOR r IN (SELECT lt.lift_id FROM LiftTrail lt WHERE lt.trail_id = NVL(:NEW.trail_id, :OLD.trail_id)) LOOP "
                            +
                            "    UPDATE Lift " +
                            "    SET difficulty_level = (SELECT ROUND(AVG(t.difficulty), 0) " +
                            "                            FROM LiftTrail lt JOIN Trail t ON lt.trail_id = t.trail_id " +
                            "                            WHERE lt.lift_id = r.lift_id) " +
                            "    WHERE lift_id = r.lift_id; " +
                            "  END LOOP; " +
                            "END;");

            // Trigger 2: On LiftTrail changes
            stmt.executeUpdate(
                    "CREATE OR REPLACE TRIGGER trg_update_lift_difficulty_from_lifttrail " +
                            "AFTER INSERT OR DELETE ON LiftTrail " +
                            "FOR EACH ROW " +
                            "DECLARE " +
                            "  v_lift_id NUMBER; " +
                            "BEGIN " +
                            "  v_lift_id := NVL(:NEW.lift_id, :OLD.lift_id); " +
                            "  UPDATE Lift " +
                            "  SET difficulty_level = (SELECT ROUND(AVG(t.difficulty), 0) " +
                            "                          FROM LiftTrail lt JOIN Trail t ON lt.trail_id = t.trail_id " +
                            "                          WHERE lt.lift_id = v_lift_id) " +
                            "  WHERE lift_id = v_lift_id; " +
                            "END;");
            // Trigger 3: On SkiPass changes
            stmt.executeUpdate("CREATE OR REPLACE TRIGGER trg_decrement_uses_after_rental\r\n" + //
                    "AFTER INSERT ON Rental\r\n" + //
                    "FOR EACH ROW\r\n" + //
                    "BEGIN\r\n" + //
                    "    UPDATE SkiPass\r\n" + //
                    "    SET remaining_uses = remaining_uses - 1\r\n" + //
                    "    WHERE pass_id = :NEW.pass_id;\r\n" + //
                    "END;");

            // Trigger 4: SkiPass archived status
            stmt.executeUpdate("CREATE OR REPLACE TRIGGER trg_archive_skipass_when_empty\r\n" + //
                    "AFTER UPDATE ON SkiPass\r\n" + //
                    "FOR EACH ROW\r\n" + //
                    "WHEN (NEW.remaining_uses = 0)\r\n" + //
                    "BEGIN\r\n" + //
                    "    UPDATE SkiPass\r\n" + //
                    "    SET archived_status = 'archived'\r\n" + //
                    "    WHERE pass_id = :NEW.pass_id;\r\n" + //
                    "END;");
            // Trigger 4: SkiPass exp
            stmt.executeUpdate("CREATE OR REPLACE TRIGGER trg_archive_skipass_when_empty\r\n" + //
                    "AFTER UPDATE ON SkiPass\r\n" + //
                    "FOR EACH ROW\r\n" + //
                    "WHEN (NEW.remaining_uses = 0 AND OLD.remaining_uses != 0)\r\n" + //
                    "BEGIN\r\n" + //
                    "    UPDATE SkiPass\r\n" + //
                    "    SET archived_status = 'archived'\r\n" + //
                    "    WHERE pass_id = :NEW.pass_id;\r\n" + //
                    "END;\r\n" + //
                    "/\r\n" + //
                    "");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

    }
}