package features.ski_pass;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class SkiPassDatabaseAccess
 ||
 ||         Author:  Ethan Huang
 ||                  Matthew Song
 ||
 ||        Purpose:  Backend for ski pass database services. Directly 
 ||                  interfaces with the SkiPass relation.
 ||
 ||  Inherits From:  DatabaseAccess
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  SkiPassDatabaseAccess(DatabaseConnection dbConnection)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  Long createSkiPass(SkiPass skiPass)
 ||                  Optional<SkiPass> findSkiPassById(long passId)
 ||                  SkiPass mapRowToSkiPassData(ResultSet rs)
 ||                  void updateSkiPass(SkiPass skiPass)
 ||                  void deleteSkiPass(long passId)
 ||                  int getDaysSincePurchase(long passId)
 ||                  int hasActiveSkiPassByMember(Long memberId)
 ||
 ++-----------------------------------------------------------------------*/

public class SkiPassDatabaseAccess extends DatabaseAccess {

    public SkiPassDatabaseAccess(DatabaseConnection dbConnection) {
        super(dbConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createSkiPass(SkiPass skiPass)
     |
     |  Purpose:  Creates a ski pass in the database.
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: Database contains a new SkiPass tuple with the 
     |      provided fields
     |
     |  Parameters: SkiPass skiPass -- Data carrier for all attributes
     |
     |  Returns:  Primary key (pass_id) of new ski pass
     *-------------------------------------------------------------------*/
    
    public Long createSkiPass(SkiPass skiPass) throws SQLException {

        String insertSql = "INSERT INTO stephintomson.SkiPass VALUES (?, ?, SYSDATE, ?, ?, ?, ?, 0)";
        // SYSDATE is used for consistency (database server time)
        // all new passes are not archived (archived_status = 0)

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            Long newPassId = getNextId("SkiPass", "pass_id"); // Helper to get a new ID
            preparedStatement.setLong(1, newPassId);
            preparedStatement.setLong(2, skiPass.memberId());
            preparedStatement.setInt(3, skiPass.totalUses());
            preparedStatement.setInt(4, skiPass.remainingUses());
            preparedStatement.setString(5, skiPass.passType());
            preparedStatement.setInt(6, skiPass.price());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ski pass failed, no rows affected.");
            }
            return newPassId;
        }
    }

    /*---------------------------------------------------------------------
     |  Method findSkiPassById(long passId)
     |
     |  Purpose:  Finds a SkiPass tuple that matches passId on pass_id
     |
     |  Pre-condition:  Nothing that is not obvious
     |
     |  Post-condition: Returned Optional contains a valid SkiPass record 
     |      or is empty
     |
     |  Parameters: long passId -- pass_id to find in the relation
     |
     |  Returns:  Optional with matching SkiPass record or empty if not found
     *-------------------------------------------------------------------*/
    
    public Optional<SkiPass> findSkiPassById(long passId) throws SQLException {
        String sql = "SELECT * FROM stephintomson.SkiPass WHERE pass_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToSkiPassData(rs));
            }
        }
        return Optional.empty();
    }

    /*---------------------------------------------------------------------
     |  Method mapRowToSkiPassData(ResultSet rs)
     |
     |  Purpose:  Returns the SkiPass representation of a tuple in an SQL 
     |      query result on SkiPass
     |
     |  Pre-condition:  rs contains all of the columns listed in the method
     |
     |  Post-condition: All SkiPass object attributes match the database 
     |      tuple values
     |
     |  Parameters: ResultSet rs -- points to a row in an SQL query result
     |
     |  Returns:  SkiPass object with all attributes represented
     *-------------------------------------------------------------------*/
    
    private SkiPass mapRowToSkiPassData(ResultSet rs) throws SQLException {
        return new SkiPass(
                rs.getLong("pass_id"),
                rs.getLong("member_id"),
                rs.getDate("purchase_date"),
                rs.getInt("total_uses"),
                rs.getInt("remaining_uses"),
                rs.getString("pass_type"),
                rs.getInt("price"),
                rs.getInt("archived_status"));
    }

    /*---------------------------------------------------------------------
     |  Method updateSkiPass(SkiPass skiPass)
     |
     |  Purpose:  Updates a SkiPass tuple in the database.
     |
     |  Pre-condition:  A SkiPass tuple exists with matching pass_id
     |
     |  Post-condition: Same SkiPass tuple now contains the new values from
     |      skiPass
     |
     |  Parameters: SkiPass skiPass -- data carrier for new values
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
    public void updateSkiPass(SkiPass skiPass) throws SQLException {
        String sql = "UPDATE stephintomson.SkiPass SET member_id = ?, purchase_date = ?, total_uses = ?, remaining_uses = ?, pass_type = ?, price = ?, archived_status = ? WHERE pass_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, skiPass.memberId());
            preparedStatement.setDate(2, skiPass.purchaseDate());
            preparedStatement.setInt(3, skiPass.totalUses());
            preparedStatement.setInt(4, skiPass.remainingUses());
            preparedStatement.setString(5, skiPass.passType());
            preparedStatement.setInt(6, skiPass.price());
            preparedStatement.setInt(7, skiPass.archivedStatus());
            preparedStatement.setLong(8, skiPass.passId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ski pass failed, no rows affected. Pass ID: " + skiPass.passId());
            }
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteSkiPass(long passId)
     |
     |  Purpose:  Deletes a tuple from the SkiPass relation
     |
     |  Pre-condition:  A SkiPass tuple exists with matching pass_id
     |
     |  Post-condition: Same tuple is removed from the database
     |
     |  Parameters: long passId -- pass_id of tuple to delete
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
    public void deleteSkiPass(long passId) throws SQLException {
        String sql = "DELETE FROM stephintomson.SkiPass WHERE pass_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting ski pass failed, no rows affected. Pass ID: " + passId);
            }
        }
    }

    /*---------------------------------------------------------------------
     |  Method getDaysSincePurchase(long passId)
     |
     |  Purpose:  Calculates the days (rounded down) since a ski pass was 
     |      purchased
     |
     |  Pre-condition:  SkiPass tuple exists with pass_id = passId
     |
     |  Post-condition: Database is unaffected
     |
     |  Parameters: long passId -- pass_id to search for
     |
     |  Returns:  Days as integer since ski pass was purchased
     *-------------------------------------------------------------------*/
    
    public int getDaysSincePurchase(long passId) throws SQLException {
        String sql = "SELECT TRUNC(SYSDATE - purchase_date, 0) AS time_diff FROM stephintomson.SkiPass WHERE pass_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("time_diff");
            } else {
                throw new SQLException("Pass not found for calculating days since purchase, ID: " + passId);
            }
        }
    }

    /*---------------------------------------------------------------------
     |  Method hasActiveSkiPassByMember(Long memberId)
     |
     |  Purpose:  Determines if a member owns active (unarchived) ski passes
     |
     |  Pre-condition:  memberId represents a valid Member
     |
     |  Post-condition: Database is unaffected
     |
     |  Parameters: Long memberId -- member_id of Member
     |
     |  Returns:  1 if member has an active ski pass, else 0
     *-------------------------------------------------------------------*/
    
    public int hasActiveSkiPassByMember(Long memberId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS active_pass_count
                  FROM stephintomson.SkiPass
                 WHERE member_id = ?
                   AND archived_status = 0
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, memberId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt("active_pass_count") > 0 ? 1 : 0;
            } else {
                throw new SQLException("Member not found for ski pass check, ID: " + memberId);
            }
        }
    }

}