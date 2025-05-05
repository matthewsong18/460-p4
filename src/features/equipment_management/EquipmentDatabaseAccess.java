package features.equipment_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentDatabaseAccess {
    private final Connection connection;

    public EquipmentDatabaseAccess(Connection connection) {
        this.connection = connection;
    }

    public Long createEquipment(Equipment equipment) {
        String sql = "INSERT into equipment (type, dimension, archived_status) VALUES (?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, equipment.type());
            preparedStatement.setString(2, equipment.dimension());
            preparedStatement.setInt(3, equipment.archived_status());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating equipment failed; No rows affected.");
            }

            // Get the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating member failed; no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEquipment(Equipment equipment) {
        String sql = "UPDATE equipment SET type = ?, dimension = ?, archived_status = ? WHERE id = ?";

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
