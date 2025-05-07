package features.property_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PropertyDatabaseAccess extends DatabaseAccess {

    public PropertyDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createProperty(Property property) {
        String sql = "INSERT INTO Property (property_id, property_type, property_monthly_income) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newPropertyId = getNextId("Property", "property_id");

            preparedStatement.setLong(1, newPropertyId);
            preparedStatement.setString(2, property.property_type());
            preparedStatement.setInt(3, property.property_monthly_income());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to create property; No rows affected.");
            }

            return newPropertyId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Property> findPropertyById(Long id) {
        String sql = "SELECT * FROM Property WHERE property_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Property property = mapRowToProperty(resultSet);
                return Optional.of(property);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Property mapRowToProperty(ResultSet resultSet) throws SQLException {
        Long property_id = resultSet.getLong(1);
        String property_type = resultSet.getString(2);
        int property_monthly_income = resultSet.getInt(3);

        return new Property(property_id, property_type, property_monthly_income);
    }

    public void updateProperty(Property property) {
        String sql = "UPDATE Property SET property_type = ?, property_monthly_income = ? WHERE property_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, property.property_type());
            preparedStatement.setInt(2, property.property_monthly_income());
            preparedStatement.setLong(3, property.property_id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to update property; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProperty(Property property) {
        String sql = "DELETE FROM Property WHERE property_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, property.property_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to delete property; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
