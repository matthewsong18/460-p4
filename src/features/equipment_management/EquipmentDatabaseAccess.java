package features.equipment_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EquipmentDatabaseAccess extends DatabaseAccess {

    public EquipmentDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createEquipment(Equipment equipment) {
        String sql = "INSERT into Equipment (equipment_id, type, dimension, archived_status) VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newEquipmentId = getNextId("Equipment", "equipment_id");

            preparedStatement.setLong(1, newEquipmentId);
            preparedStatement.setString(2, equipment.type());
            preparedStatement.setString(3, equipment.dimension());
            preparedStatement.setInt(4, equipment.archived_status());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating equipment failed; No rows affected.");
            }

            return newEquipmentId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Equipment> findEquipmentById(Long id) {
        String sql = "SELECT type, dimension, archived_status FROM Equipment WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Equipment equipment = mapRowToEquipment(resultSet);
                    return Optional.of(equipment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    // Helper method to map a ResultSet row to an Equipment object
    private Equipment mapRowToEquipment(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String type = resultSet.getString("type");
        String dimension = resultSet.getString("dimension");
        int archived_status = resultSet.getInt("archived_status");

        return new Equipment(id, type, dimension, archived_status);
    }


    public void updateEquipment(Equipment equipment) {
        String sql = "UPDATE Equipment SET type = ?, dimension = ?, archived_status = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, equipment.type());
            preparedStatement.setString(2, equipment.dimension());
            preparedStatement.setInt(3, equipment.archived_status());
            preparedStatement.setLong(4, equipment.equipment_id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating equipment failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
