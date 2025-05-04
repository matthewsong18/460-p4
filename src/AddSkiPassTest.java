/**
 * Test script with hard-coded values for adding and retrieving a ski pass
 * 
 * Prerequisites:
 *  - Member table has a member with member_id = 1
*/

import common.DatabaseConnection;
import features.ski_pass.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class AddSkiPassTest {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java AddSkiPassTest <username> <password>");
            return;
        }

        DatabaseConnection dbconn = new DatabaseConnection(args[0], args[1]);

        try {
            Connection sqlconn = dbconn.getConnection();

            SkiPass.setConnection(dbconn);

            int memberId = 1;   // assumes that there is a member with member_id = 1
            String type = "season";
            int uses = 100;
            int price = 50;


            System.out.println("Testing adding a pass...");

            SkiPass pass = new SkiPass(memberId, type, uses, price);


            System.out.println("Testing retrieving a pass...");

            SkiPass pass2 = new SkiPass(pass.getPassId());

            System.out.println(pass2);

            // Tests if both passes retrieved the same values
            assert pass.getPassId() == pass2.getPassId();
            assert pass.getMemberId() == pass2.getMemberId();
            assert pass.getPurchaseDate().equals(pass2.getPurchaseDate());
            assert pass.getTotalUses() == pass2.getTotalUses();
            assert pass.getRemainingUses() == pass2.getRemainingUses();
            assert pass.getType().equals(pass2.getType());
            assert pass.getPrice() == pass2.getPrice();
            assert !pass.isArchived() && !pass2.isArchived();
            assert !pass.isExpired() && !pass2.isExpired();


            System.out.println("Testing retrieving nonexistant pass...");
            try {
                pass2 = new SkiPass(0);
                assert false;   // Should not reach here
            }
            catch (RuntimeException e) {
                // Continue execution
            }


            System.out.println("Testing retrieving expiration status...");

            System.out.println("Updating purchase date to 180 days ago...");

            Statement stmt = sqlconn.createStatement();

            stmt.executeUpdate(
                    "UPDATE SkiPass SET purchase_date = purchase_date - 180 " +
                    "WHERE pass_id = " + pass.getPassId()
            );

            System.out.println(pass);

            assert pass.isExpired();


            System.out.println("Testing manually adding remaining uses...");

            stmt.executeUpdate(
                    "UPDATE SkiPass SET remaining_uses = 1 " +
                    "WHERE pass_id = " + pass.getPassId()
            );

            assert pass.addUses(10);

            assert !pass.addUses(90);

            ResultSet rs = stmt.executeQuery("SELECT remaining_uses FROM SkiPass " +
                            "WHERE pass_id = " + pass.getPassId());
            assert rs.next() && rs.getInt("remaining_uses") == 11;

            rs.close();


            System.out.println("Testing manual archiving...");

            pass.archivePass();

            assert pass.isArchived();

            rs = stmt.executeQuery("SELECT archived_status FROM SkiPass " +
                            "WHERE pass_id = " + pass.getPassId());
            assert rs.next() && rs.getString("archived_status").equals("archived");

            rs.close();

            assert !pass.addUses(1);    // This should return false and not occur.
            rs = stmt.executeQuery("SELECT remaining_uses FROM SkiPass " +
                            "WHERE pass_id = " + pass.getPassId());
            assert rs.next() && rs.getInt("remaining_uses") == 11;


            System.out.println("Deleting pass from database");

            stmt.executeUpdate("DELETE FROM SkiPass WHERE pass_id = " + pass.getPassId());

            stmt.close();
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not update pass in database.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        System.out.println("All tests passed.");
    }
}
