package features.rental_management;

import common.DatabaseAccess;
import common.DatabaseConnection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*+---------------------------------------------------------------------- 
 ||  Class RentalDatabaseAccess
 ||
 ||         Author:  Matthew Song, Ethan Huang, Stephin Tomson
 ||
 ||        Purpose:  Handles all direct interactions with the Rental table in the
 ||                  Ski Resort database. This includes CRUD operations and utility
 ||                  queries to support business logic.
 ||
 ||  Inherits From:  DatabaseAccess
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:
 ||      - RentalDatabaseAccess(DatabaseConnection databaseConnection)
 ||          Initializes a new instance using the given database connection.
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:
 ||      - Long createRental(Rental rental)
 ||      - Optional<Rental> findRental(Long id)
 ||      - void updateRental(Rental rental)
 ||      - void deleteRental(Rental rental)
 ||      - Optional<Rental> findActiveRental(Long rental_id)
 ||      - int hasRentalByMember(Long memberId)
 ||
 ++-----------------------------------------------------------------------*/

public class RentalDatabaseAccess extends DatabaseAccess {
    /*---------------------------------------------------------------------
    |  Constructor RentalDatabaseAccess
    |
    |  Purpose:  Initializes the DAO with an active database connection.
    |
    |  Pre-condition:  The provided DatabaseConnection object must be valid and open.
    |
    |  Post-condition:  This DAO is ready to execute Rental table operations.
    |
    |  Parameters:
    |      databaseConnection -- Connection object for accessing the Oracle DB.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public RentalDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
    |  Method createRental
    |
    |  Purpose:  Inserts a new rental record into the database using the given Rental object.
    |
    |  Pre-condition:  The Rental object must be valid (non-null pass_id and equipment_id).
    |
    |  Post-condition:  A new rental record is inserted, and the new rental ID is returned.
    |
    |  Parameters:
    |      rental -- Rental object containing pass and equipment IDs.
    |
    |  Returns:  Long -- The generated rental_id of the newly inserted rental.
    *-------------------------------------------------------------------*/
    public Long createRental(Rental rental) {
        String sql = "INSERT into stephintomson.Rental values (?,?,?,SYSTIMESTAMP,null)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newRentalId = getNextId("Rental", "rental_id"); // Helper to get a new ID

            preparedStatement.setLong(1, newRentalId);
            preparedStatement.setLong(2, rental.pass_id());
            preparedStatement.setLong(3, rental.equipment_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to create rental; No rows affected.");
            }

            return newRentalId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method findRental
    |
    |  Purpose:  Retrieves a rental by its rental ID from the database.
    |
    |  Pre-condition:  A valid rental_id must be provided.
    |
    |  Post-condition:  If found, returns a Rental object wrapped in Optional; else Optional.empty().
    |
    |  Parameters:
    |      id -- Rental ID to search for.
    |
    |  Returns:  Optional<Rental> -- The matched rental or empty if not found.
    *-------------------------------------------------------------------*/
    public Optional<Rental> findRental(Long id) {
        String sql = "SELECT rental_id, pass_id, equipment_id, datetime_from, datetime_to FROM stephintomson.Rental WHERE rental_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Rental rental = mapRowToRental(resultSet);
                return Optional.of(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method mapRowToRental
    |
    |  Purpose:  Helper method that converts a ResultSet row into a Rental object.
    |
    |  Pre-condition:  The ResultSet must be positioned at a valid row.
    |
    |  Post-condition:  A new Rental object is returned with data from the row.
    |
    |  Parameters:
    |      resultSet -- A ResultSet pointing to a row with rental data.
    |
    |  Returns:  Rental -- A fully populated Rental object.
    *-------------------------------------------------------------------*/
    private Rental mapRowToRental(ResultSet resultSet) throws SQLException {
        Long rental_id = resultSet.getLong(1);
        Long ski_pass_id = resultSet.getLong(2);
        Long equipment_id = resultSet.getLong(3);
        Date date_time_from = resultSet.getDate(4);
        Date date_time_to = resultSet.getDate(5);

        return new Rental(rental_id, ski_pass_id, equipment_id, date_time_from, date_time_to);
    }

    /*---------------------------------------------------------------------
    |  Method updateRental
    |
    |  Purpose:  Updates an existing rental record in the database.
    |
    |  Pre-condition:  The Rental object must include a valid rental_id.
    |
    |  Post-condition:  The matching rental record is updated with new values.
    |
    |  Parameters:
    |      rental -- Rental object containing updated rental information.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public void updateRental(Rental rental) {
        String sql = "UPDATE stephintomson.Rental SET pass_id = ?, equipment_id = ?, datetime_from = ?, datetime_to = ?  WHERE rental_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, rental.pass_id());
            preparedStatement.setLong(2, rental.equipment_id());
            preparedStatement.setDate(3, rental.dateTime_From());
            preparedStatement.setDate(4, rental.dateTime_To());
            preparedStatement.setLong(5, rental.rental_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to update rental; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method deleteRental
    |
    |  Purpose:  Deletes a rental record from the database based on the rental ID.
    |
    |  Pre-condition:  The Rental object must include a valid rental_id.
    |
    |  Post-condition:  The corresponding record is deleted if it exists.
    |
    |  Parameters:
    |      rental -- Rental object whose rental_id is used to delete the row.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public void deleteRental(Rental rental) {
        String sql = "DELETE FROM stephintomson.Rental WHERE rental_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, rental.rental_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to delete rental; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method findActiveRental
    |
    |  Purpose:  Returns a rental only if it is currently active (not returned).
    |
    |  Pre-condition:  A valid rental_id must be provided.
    |
    |  Post-condition:  If the rental is active, it is returned; else Optional.empty().
    |
    |  Parameters:
    |      rental_id -- The ID of the rental to check.
    |
    |  Returns:  Optional<Rental> -- The active rental or empty if not active or not found.
    *-------------------------------------------------------------------*/
    public Optional<Rental> findActiveRental(Long rental_id) {
        String sql = """
                SELECT rental_id, pass_id, equipment_id, datetime_from, datetime_to
                  FROM stephintomson.Rental
                 WHERE rental_id = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, rental_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Rental rental = mapRowToRental(resultSet);

                    if (rental.isActive()) {
                        return Optional.of(rental);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    /*---------------------------------------------------------------------
    |  Method hasRentalByMember
    |
    |  Purpose:  Checks if a given member has any rentals associated via SkiPass.
    |
    |  Pre-condition:  The memberId must be valid.
    |
    |  Post-condition:  Returns 1 if at least one rental exists, otherwise returns 0.
    |
    |  Parameters:
    |      memberId -- ID of the member to check.
    |
    |  Returns:  int -- 1 if rentals exist, 0 otherwise.
    *-------------------------------------------------------------------*/
    public int hasRentalByMember(Long memberId) {
        String sql = """
                SELECT 1
                  FROM stephintomson.Rental r
                  JOIN stephintomson.SkiPass s ON r.pass_id = s.pass_id
                 WHERE s.member_id = ?
                   AND ROWNUM = 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? 1 : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking rental for member: " + memberId, e);
        }
    }

}
