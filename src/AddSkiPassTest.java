// Test script with hard-coded values for adding and retrieving a ski pass

import common.DatabaseConnection;

public class AddSkiPassTest {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java AddSkiPassTest <username> <password>");
            return;
        }

        DatabaseConnection dbconn = new DatabaseConnection(args[0], args[1]);

        int memberId = 1;   // assumes that there is a member with member_id = 1
        String type = "season";
        int uses = 100;
        int price = 50;

        SkiPass pass = new SkiPass(dbconn.getConnection(), memberId, type, uses, price);

        SkiPass pass2 = new SkiPass(dbconn.getConnection(), pass.getPassId());

        assert pass.getPassId() == pass2.getPassId();
        assert pass.getMemberId() == pass2.getMemberId();
        assert pass.getPurchaseDate() == pass2.getPurchaseDate();
        assert pass.getTotalUses() == pass2.getTotalUses();
        assert pass.getRemainingUses() == pass2.getRemainingUses();
        assert pass.getType() == pass2.getType();
        assert pass.getPrice() == pass2.getPrice();

        System.out.println("Successfully added a Ski Pass.");
    }
}
