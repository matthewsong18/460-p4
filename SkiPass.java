/*=============================================================================
 |   Assignment:  Program #4
 |       Author:  Ethan Huang (ethanhuang@arizona.edu)
 |
 |       Course:  CSc 460 (Database Design), Spring 2025
 |   Instructor:  L. McCann
 | Sect. Leader:  [Your Section Leader's name]
 |     Due Date:  May 6th, 2025, at the beginning of class
 |
 |     Language:  Java (JDK 16.0.2)
 |     Packages:  [Names and Sources of any special code libraries used]
 |  Compile/Run:  Compile: javac SkiPass.java
 |                    Run: java SkiPass
 |
 +-----------------------------------------------------------------------------
 |
 |  Description:  Holds all attributes and methods for a ski pass.
 |                
 |        Input:  [Describe the sources and formats of all user-supplied
 |                 input required by the program.]
 |
 |       Output:  [Describe the output that the user should expect the
 |                 program to produce.]
 |
 |   Techniques:  [Names of standard algorithms employed, explanations
 |                 of why things were done they way they were done, etc.
 |                 This is the place for technical information that another
 |                 programmer would like to know.]
 |
 |   Required Features Not Included:
 |                [If the assignment specifies a feature that you were
 |                 unable to include in the program, mention that omission
 |                 here.  Otherwise, state that you were able to include
 |                 all of the required features in your program.]
 |
 |   Known Bugs:  [If you know of any problems with the code, provide
 |                details here, otherwise clearly state that you know
 |                of know logic errors.]
 |
 *===========================================================================*/

import java.sql.*;

public class SkiPass {
    private int passId;
    private int memberId;
    private Timestamp purchaseDate;
    private int totalUses;
    private int remUses;
    private String type;
    private int price;
    
    public int getPassId() { return this.passId; }
    public int getMemberId() { return this.memberId; }
    public Timestamp getPurchaseDate() { return this.purchaseDate; }
    public int getTotalUses() { return this.totalUses; }
    public int getRemUses() { return this.remUses; }
    public String getType() { return this.type; }
    public int getPrice() { return this.price; }

    // Create a new ski pass and add it to the database
    public SkiPass(Connection dbconn, int memberId, String type, int uses, int price) {
        try {
            Statement stmt = dbconn.createStatement();

            ResultSet passIDquery = stmt.executeQuery("SELECT max(pass_id) AS pass_id FROM SkiPass");

            this.passId = passIDquery.next() ? passIDquery.getInt("pass_id") + 1 : 1;
            this.memberId = memberId;

            ResultSet systimestamp = stmt.executeQuery("SELECT SYSTIMESTAMP FROM DUAL");

            this.purchaseDate = systimestamp.next()
                                ? systimestamp.getTimestamp("SYSTIMESTAMP")
                                : new Timestamp(System.currentTimeMillis());
            this.totalUses = uses;
            this.remUses = uses;
            this.type = type;
            this.price = price;

            stmt.executeUpdate("INSERT INTO SkiPass VALUES (" +
                    this.passId + "," +
                    this.memberId + "," +
                    "SYSTIMESTAMP," +
                    this.totalUses + "," +
                    this.remUses + "," +
                    "\'" + this.type + "\'" + "," +
                    this.price + ")"
            );
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    // Get an existing pass using the pass ID.
    public SkiPass(Connection dbconn, int passId) {
        try {
            Statement stmt = dbconn.createStatement();

            ResultSet resultset = stmt.executeQuery(
                    "SELECT * FROM SkiPass\n" +
                    "WHERE pass_id = " + passId);

            if (resultset.next()) {
                this.passId = passId;
                this.memberId = resultset.getInt("member_id");
                this.purchaseDate = resultset.getTimestamp("purchase_datetime");
                this.totalUses = resultset.getInt("total_uses");
                this.remUses = resultset.getInt("remaining_uses");
                this.type = resultset.getString("type");
                this.price = resultset.getInt("price");
            }
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }
}
