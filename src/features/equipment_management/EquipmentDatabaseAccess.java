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
        String sql = "INSERT into stephintomson.Equipment (equipment_id, equipment_type, dimension, archived_status) VALUES (?,?,?,?)";
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
        String sql = "SELECT equipment_type, dimension, archived_status " +
                "FROM stephintomson.Equipment WHERE equipment_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEquipment(rs,id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // Helper method to map a ResultSet row to an Equipment object
    private Equipment mapRowToEquipment(ResultSet resultSet,Long id1) throws SQLException {
        long id = id1;
        String type = resultSet.getString("EQUIPMENT_TYPE");
        String dimension = resultSet.getString("DIMENSION");
        int archived_status = resultSet.getInt("ARCHIVED_STATUS");

        return new Equipment(id, type, dimension, archived_status);
    }

    public void updateEquipment(Equipment equipment) {
        String sql = "UPDATE stephintomson.Equipment SET equipment_type = ?, dimension = ?, archived_status = ? WHERE equipment_id = ?";

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
