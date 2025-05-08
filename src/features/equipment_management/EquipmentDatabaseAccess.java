package features.equipment_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class EquipmentDatabaseAccess
 ||
 ||      Author:  Matthew Song, Stephin Tomson, Elias Zheng
 ||
 ||      Purpose:  Handles database operations for equipment, including
 ||                creation, retrieval, and updates.
 ||
 ||  Inherits From:  common.DatabaseAccess
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  EquipmentDatabaseAccess(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long createEquipment(Equipment equipment)
 ||                  Optional<Equipment> findEquipmentById(Long id)
 ||                  void updateEquipment(Equipment equipment)
 ||                  private Equipment mapRowToEquipment(ResultSet resultSet, Long id1) throws SQLException
 ||
 ++-----------------------------------------------------------------------*/
public class EquipmentDatabaseAccess extends DatabaseAccess {

    /*---------------------------------------------------------------------
     |  Method EquipmentDatabaseAccess (Constructor)
     |
     |  Purpose:  Initializes with a database connection.
     |
     |  Pre-condition:  Valid databaseConnection.
     |
     |  Post-condition: Instance ready for DB operations.
     |
     |  Parameters:
     |      databaseConnection -- Database connection.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public EquipmentDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createEquipment
     |
     |  Purpose:  Creates a new equipment record in the database.
     |
     |  Pre-condition:  Valid equipment object; active DB connection.
     |                  'Equipment' table and 'equipment_id' sequence exist.
     |
     |  Post-condition: Equipment created, new ID returned. Throws
     |                  RuntimeException on SQL error or if no rows affected.
     |
     |  Parameters:
     |      equipment -- Equipment data to create.
     |
     |  Returns:  ID (Long) of the new equipment.
     *-------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------
     |  Method findEquipmentById
     |
     |  Purpose:  Finds equipment by its ID.
     |
     |  Pre-condition:  Valid ID; active DB connection.
     |
     |  Post-condition: Returns Optional of equipment if found, else empty.
     |                  Throws RuntimeException on SQL error.
     |
     |  Parameters:
     |      id -- Equipment ID to find.
     |
     |  Returns:  Optional<Equipment>.
     *-------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------
     |  Method mapRowToEquipment
     |
     |  Purpose:  Maps ResultSet row and an ID to an Equipment object.
     |
     |  Pre-condition:  ResultSet on valid row (data for type, dimension,
     |                  status); valid equipment ID `id1` provided.
     |
     |  Post-condition: Returns mapped Equipment object. Throws SQLException
     |                  if column access fails.
     |
     |  Parameters:
     |      resultSet -- ResultSet with equipment data.
     |      id1 -- The equipment ID.
     |
     |  Returns:  Equipment object.
     *-------------------------------------------------------------------*/
    private Equipment mapRowToEquipment(ResultSet resultSet,Long id1) throws SQLException {
        long id = id1;
        String type = resultSet.getString("EQUIPMENT_TYPE");
        String dimension = resultSet.getString("DIMENSION");
        int archived_status = resultSet.getInt("ARCHIVED_STATUS");

        return new Equipment(id, type, dimension, archived_status);
    }

    /*---------------------------------------------------------------------
     |  Method updateEquipment
     |
     |  Purpose:  Updates an existing equipment's record.
     |
     |  Pre-condition:  Valid equipment object with ID; active DB connection.
     |
     |  Post-condition: Equipment updated. Throws RuntimeException on SQL
     |                  error or if no rows affected (equipment not found).
     |
     |  Parameters:
     |      equipment -- Equipment data for update. equipment_id is used in WHERE.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
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