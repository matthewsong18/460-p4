package features.ski_pass;

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
import common.DatabaseConnection;

/*+----------------------------------------------------------------------
 ||
 ||  Class SkiPass
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  [A description of why this class exists.  For what
 ||                   reason was it written?  Which jobs does it perform?]
 ||
 ||  Inherits From:  [If this class is a subclass of another, name it.
 ||                   If not, just say "None."]
 ||
 ||     Interfaces:  [If any predefined interfaces are implemented by
 ||                   this class, name them.  If not, ... well, you know.]
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  [Name all public class constants, and provide a very
 ||                   brief (but useful!) description of each.]
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  [List the names and arguments of all defined
 ||                   constructors.]
 ||
 ||  Class Methods:  [List the names, arguments, and return types of all
 ||                   public class methods.]
 ||
 ||  Inst. Methods:  [List the names, arguments, and return types of all
 ||                   public instance methods.]
 ||
 ++-----------------------------------------------------------------------*/

public class SkiPass {
    private static Connection dbconn;

    private int passId;
    private int memberId;
    private Timestamp purchaseDate;
    private int totalUses;
    private int remainingUses;
    private String type;
    private int price;
    private boolean archived;

    // Setter for static SQL Connection for all SkiPass objects
    public static void setConnection(DatabaseConnection connection) {
        dbconn = connection.getConnection();
    }

    // Create a new ski pass and add it to the database
    public SkiPass(int memberId, String type, int uses, int price) {
        if (this.dbconn == null) {
            System.out.println("Error: Database connection not initialized.");
            System.exit(-1);
        }

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
            this.remainingUses = uses;
            this.type = type;
            this.price = price;
            this.archived = false;

            stmt.executeUpdate("INSERT INTO SkiPass VALUES (" +
                    this.passId + "," +
                    this.memberId + "," +
                    "SYSTIMESTAMP," +
                    this.totalUses + "," +
                    this.remainingUses + "," +
                    "\'" + this.type + "\'" + "," +
                    this.price + "," +
                    "\'active\')"
            );

            stmt.close();
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not create a new pass.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    private SkiPass() {

    }

    // Get an existing pass using the pass ID.
    public static SkiPass getSkiPassById(int passId) {
        if (dbconn == null) {
            System.out.println("Error: Database connection not initialized.");
            return null;
        }

        SkiPass newSkiPass = new SkiPass();

        newSkiPass.passId = passId;

        return newSkiPass.updateFromDatabase() ? newSkiPass : null;
    }
    
    // Getters
    public int getPassId() {
        return this.passId;
    }

    public int getMemberId() {
        return this.memberId;
    }

    public Timestamp getPurchaseDate() {
        return this.purchaseDate;
    }

    public int getTotalUses() {
        this.updateFromDatabase();
        return this.totalUses;
    }

    public int getRemainingUses() {
        this.updateFromDatabase();
        return this.remainingUses;
    }

    public String getType() {
        return this.type;
    }

    public int getPrice() {
        return this.price;
    }

    public boolean isArchived() {
        this.updateFromDatabase();
        return this.archived;
    }

    /*---------------------------------------------------------------------
     |  Method isExpired()
     |
     |  Purpose:  Determines if the pass is expired or not. Throws a 
     |      RuntimeException if the pass was not found, or a SQLException 
     |      if 
     |
     |  Pre-condition:  Database connection (dbconn) is initialized and 
     |      the pass exists in the database.
     |
     |  Post-condition: No non-obvious post-conditions.
     |
     |  Parameters:
     |
     |  Returns:  True if pass is expired, false otherwise.
     *-------------------------------------------------------------------*/
    
    public boolean isExpired()
    {
        try {
            Statement stmt = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultset = stmt.executeQuery(
                    "SELECT TRUNC(SYSDATE - purchase_date, 0) AS time_diff FROM SkiPass " +
                    "WHERE pass_id = " + this.passId
            );

            if (resultset.first()) {
                int daysSincePurchase = resultset.getInt("time_diff");

                System.out.println("Days since purchase: " + daysSincePurchase);

                boolean expired;

                switch (type) {
                    case "1-day":
                        expired = daysSincePurchase >= 1;
                        break;
                    case "2-day":
                        expired = daysSincePurchase >= 2;
                        break;
                    case "4-day":
                        expired = daysSincePurchase >= 4;
                        break;
                    case "season":
                        expired = daysSincePurchase >= 180;
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid pass type.");
                }

                return expired;
            }
            else {
                throw new RuntimeException("Error: No matching pass.");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(
                "SQLException: Could not fetch query results.\n" +
                "\tMessage:   " + e.getMessage() + "\n" +
                "\tSQLState:  " + e.getSQLState() + "\n" +
                "\tErrorCode: " + e.getErrorCode()
            );
        }
    }

    /*---------------------------------------------------------------------
     |  Method addUses(uses)
     |
     |  Purpose:  ADMIN USE ONLY. Adds more remaining uses to a pass in 
     |      case of a mistake. Not allowed if the pass is archived, the new
     |      value will go over total_uses, or uses (to be added) <= 0.
     |
     |  Pre-condition:  Database connection (dbconn) is initialized and 
     |      the pass exists in the database.
     |
     |  Post-condition: The database and current instance reflects the 
     |      change, if action was allowed.
     |
     |  Parameters:
     |      uses -- number of uses to add to remaining_uses
     |
     |  Returns:  True if successful, false if not allowed or database 
     |      update failed.
     *-------------------------------------------------------------------*/
    
    public boolean addUses(int uses)
    {
        if (!updateFromDatabase())
            return false;

        if (uses <= 0) {
            System.out.println("Error: Invalid value.");
            return false;
        }

        if (this.remainingUses + uses > this.totalUses) {
            System.out.println("Error: Too many new uses.");
            return false;
        }

        if (this.archived) {
            System.out.println("Error: This pass is archived.");
            return false;
        }

        this.remainingUses += uses;

        return updateDatabase();
    }

    /*---------------------------------------------------------------------
     |  Method archivePass()
     |
     |  Purpose:  Manually archives the current pass. This should usually 
     |      be called only if the pass was refunded.
     |
     |  Pre-condition:  Database connection (dbconn) is initialized and 
     |      the pass exists in the database.
     |
     |  Post-condition: The database and current instance reflects the 
     |      change, if action was allowed.
     |
     |  Parameters:
     |
     |  Returns:  True if successful, false if not allowed or database 
     |      update failed.
     *-------------------------------------------------------------------*/
    
    public boolean archivePass()
    {
        if (!updateFromDatabase())
            return false;

        if (this.archived) {
            System.out.println("Error: Already archived.");
            return false;
        }

        this.archived = true;

        return updateDatabase();
    }

    /*---------------------------------------------------------------------
     |  Method updateDatabase()
     |
     |  Purpose:  Updates the values in the SkiPass relation to match that 
     |      of the object instance on which this method is called on. Only 
     |      remaining_uses and archived_status are subject to change.
     |
     |  Pre-condition:  Database connection (dbconn) is initialized and 
     |      the pass exists in the database.
     |
     |  Post-condition: The updated field values for remaining_uses, and 
     |      archived_status for the matching pass in the database 
     |      corresponds to the current object instance.
     |
     |  Parameters: None.
     |
     |  Returns:  True if update was successful, false otherwise.
     *-------------------------------------------------------------------*/
    
    private boolean updateDatabase()
    {
        try {
            Statement stmt = dbconn.createStatement();

            stmt.executeUpdate(
                    "UPDATE SkiPass SET " +
                    "remaining_uses = " + this.remainingUses + ", " +
                    "archived_status = \'" + (this.archived ? "archived" : "active") + "\' " +
                    "WHERE pass_id = " + this.passId
            );

            System.out.println("Successfully updated values in database.");

            stmt.close();

            return true;
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not update pass in database.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());

            return false;
        }
    }

    /*---------------------------------------------------------------------
     |  Method updateFromDatabase()
     |
     |  Purpose:  Updates all values in the current instance using values 
     |      retrieved from the matching pass in the SkiPass database.
     |      Throws an error if the pass doesn't exist.
     |
     |  Pre-condition:  Database connection (dbconn) is initialized and 
     |      the pass exists in the database.
     |
     |  Post-condition: All field values for the matching pass in the DB 
     |      correspond to the current object instance.
     |
     |  Parameters: None.
     |
     |  Returns:  True if update was successful, false otherwise.
     *-------------------------------------------------------------------*/
    
    private boolean updateFromDatabase()
    {
        try {
            Statement stmt = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultset = stmt.executeQuery(
                    "SELECT *\n" + 
                    "FROM SkiPass\n" +
                    "WHERE pass_id = " + passId);

            if (resultset.first()) {
                this.memberId = resultset.getInt("member_id");
                this.purchaseDate = resultset.getTimestamp("purchase_date");
                this.totalUses = resultset.getInt("total_uses");
                this.remainingUses = resultset.getInt("remaining_uses");
                this.type = resultset.getString("pass_type");
                this.price = resultset.getInt("price");
                this.archived = resultset.getString("archived_status").equals("archived");

                System.out.println("Successfully retrieved values from database.");

                stmt.close();

                return true;
            }
            else {
                System.err.println("Error: No matching pass.");

                stmt.close();

                return false;
            }
        }
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not update pass object instance.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());

            return false;
        }
    }

    public String toString() {
        this.updateFromDatabase();
        return "SkiPass{" +
                "passId=" + passId +
                ", memberId=" + memberId +
                ", purchaseDate=" + purchaseDate +
                ", totalUses=" + totalUses +
                ", remainingUses=" + remainingUses +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", archived=" + archived +
                '}';
    }
}
