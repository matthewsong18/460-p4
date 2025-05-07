package features.rental_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class RentalDatabaseAccess extends DatabaseAccess {

    public RentalDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createRental(Rental rental) {
        String sql = "INSERT into Rental (rental_id, ski_pass_id, equipment_id, date_time_from, return_status) values (?,?,?,SYSTIMESTAMP,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newPassId = getNextPassId("Rental"); // Helper to get a new ID

            preparedStatement.setLong(1, newPassId);
            preparedStatement.setLong(2, rental.ski_pass_id());
            preparedStatement.setLong(3, rental.equipment_id());
            preparedStatement.setInt(4, rental.return_status());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to create rental; No rows affected.");
            }

            // Get generated id
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Failed to create rental; No ID obtained.");
                }

                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Rental> findRental(Long id) {
        String sql = "SELECT rental_id, ski_pass_id, equipment_id, date_time_from, date_time_to FROM Rental WHERE id = ?";
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
        Timestamp date_time_from = resultSet.getTimestamp(4);
        int return_status = resultSet.getInt(5);

        return new Rental(rental_id, ski_pass_id, equipment_id, date_time_from, return_status);
    }

    public void updateRental(Rental rental) {
        String sql = "UPDATE Rental SET ski_pass_id = ?, equipment_id = ?, date_time_from = ?, date_time_to = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, rental.ski_pass_id());
            preparedStatement.setLong(2, rental.equipment_id());
            preparedStatement.setTimestamp(3, rental.dateTimeFrom());
            preparedStatement.setInt(4, rental.return_status());
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
        String sql = "DELETE FROM Rental WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, rental.rental_id());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Finds an active rental by equipment ID. An active rental is one that has not
     * been returned yet (date_time_to is NULL).
     */
    public Optional<Rental> findActiveRental(Long equipmentId) {
        String sql = "SELECT rental_id, ski_pass_id, equipment_id, date_time_from, date_time_to " +
                "FROM Rental WHERE equipment_id = ? AND date_time_to IS NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, equipmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToRental(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

}
