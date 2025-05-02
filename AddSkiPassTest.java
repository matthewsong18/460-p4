import java.util.Scanner;

public class AddSkiPassTest {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java AddSkiPassTest <username> <password>");
            return;
        }

        DatabaseConnection dbconn = new DatabaseConnection(args[0], args[1]);

        Scanner input = new Scanner(System.in);

        System.out.print("Member ID: ");
        int memberId = input.nextInt();
        input.nextLine();

        System.out.print("Type: ");
        String type = input.nextLine();

        System.out.print("Uses: ");
        int uses = input.nextInt();
        input.nextLine();

        System.out.print("Price: ");
        int price = input.nextInt();
        input.nextLine();

        input.close();

        SkiPass pass = new SkiPass(dbconn.getConnection(), memberId, type, uses, price);

        SkiPass pass2 = new SkiPass(dbconn.getConnection(), pass.getPassId());

        assert pass.getPassId() == pass2.getPassId();
        assert pass.getMemberId() == pass2.getMemberId();
        assert pass.getPurchaseDate() == pass2.getPurchaseDate();
        assert pass.getTotalUses() == pass2.getTotalUses();
        assert pass.getRemUses() == pass2.getRemUses();
        assert pass.getType() == pass2.getType();
        assert pass.getPrice() == pass2.getPrice();

        System.out.println("Successfully added a Ski Pass.");
    }
}
