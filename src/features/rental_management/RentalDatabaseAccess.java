package features.rental_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public class RentalDatabaseAccess extends DatabaseAccess {

    public RentalDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createRental(Rental rental) {
        String sql = "INSERT into stephintomson.Rental values (?,?,?,SYSTIMESTAMP,SYSTIMESTAMP+1)";

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

    private Rental mapRowToRental(ResultSet resultSet) throws SQLException {
        Long rental_id = resultSet.getLong(1);
        Long ski_pass_id = resultSet.getLong(2);
        Long equipment_id = resultSet.getLong(3);
        Date date_time_from = resultSet.getDate(4);
        Date date_time_to = resultSet.getDate(5);

        return new Rental(rental_id, ski_pass_id, equipment_id, date_time_from, date_time_to);
    }

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

    /*
     * Finds an active rental by equipment ID. An active rental is one that has not
     * been returned yet
     */
    public Optional<Rental> findActiveRental(Long equipmentId) {
        String sql = "SELECT rental_id, pass_id, equipment_id, datetime_from, datetime_to " +
                "FROM stephintomson.Rental WHERE equipment_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, equipmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    if (mapRowToRental(resultSet).isActive()) {
                        return Optional.of(mapRowToRental(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    /*
     * Finds an active rental by ski pass ID. An active rental is one that has not
     * been returned yet
     */
    public boolean hasRentalByMember(Long memberId) {
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
                return rs.next(); // true if at least one matching row exists
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking rental for member: " + memberId, e);
        }
    }

}
