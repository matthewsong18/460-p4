package features.property_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class PropertyDatabaseAccess
 ||
 ||      Author:  Matthew Song
 ||
 ||      Purpose:  This class is responsible for all database interactions
 ||                related to property data. It handles creating, retrieving,
 ||                updating, and deleting property records in the
 ||                'stephintomson.Property' table. It serves as the backend
 ||                data access layer for property management features.
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
 ||   Constructors:  PropertyDatabaseAccess(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long createProperty(Property property)
 ||                  Optional<Property> findPropertyById(Long id)
 ||                  void updateProperty(Property property)
 ||                  void deleteProperty(Property property)
 ||                  private Property mapRowToProperty(ResultSet resultSet) throws SQLException // Key private helper
 ||
 ++-----------------------------------------------------------------------*/
public class PropertyDatabaseAccess extends DatabaseAccess {

    /*---------------------------------------------------------------------
     |  Method PropertyDatabaseAccess (Constructor)
     |
     |  Purpose:  Constructs a new PropertyDatabaseAccess object, initializing
     |            it with a database connection. This connection is managed
     |            by the parent DatabaseAccess class and will be used for all
     |            database operations performed by this instance.
     |
     |  Pre-condition:  The databaseConnection object must be a valid, open
     |                  connection to the database.
     |
     |  Post-condition: A new PropertyDatabaseAccess instance is created and
     |                  ready to interact with the property table in the database.
     |
     |  Parameters:
     |      databaseConnection -- An instance of DatabaseConnection that provides
     |                            the active connection to the database.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public PropertyDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createProperty
     |
     |  Purpose:  Inserts a new property record into the 'stephintomson.Property'
     |            table. It first retrieves the next available unique ID for
     |            the property, then executes an INSERT SQL statement with the
     |            provided property details.
     |
     |  Pre-condition:  The 'property' object contains valid data for all
     |                  required fields (property_type, property_monthly_income).
     |                  The database connection must be active.
     |                  The `getNextId` method must be functional and return a unique ID.
     |
     |  Post-condition: If successful, a new row is inserted into the
     |                  'stephintomson.Property' table, and the ID of the newly
     |                  created property is returned. If the insertion fails
     |                  (e.g., no rows affected), an SQLException is thrown
     |                  (wrapped in a RuntimeException).
     |
     |  Parameters:
     |      property -- A Property object containing the details of the property
     |                  to be created. The 'property_id' field of this object
     |                  is ignored as a new one will be generated.
     |
     |  Returns:  A Long representing the unique ID assigned to the newly
     |            created property.
     *-------------------------------------------------------------------*/
    public Long createProperty(Property property) {
        String sql = "INSERT INTO stephintomson.Property VALUES (?, ?, ?)";

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

    /*---------------------------------------------------------------------
     |  Method findPropertyById
     |
     |  Purpose:  Retrieves a property from the database based on its unique ID.
     |            It executes a SELECT SQL query and maps the result to a
     |            Property object if a record is found.
     |
     |  Pre-condition:  The 'id' parameter is a valid Long. The database
     |                  connection must be active.
     |
     |  Post-condition: If a property with the given ID exists, an Optional
     |                  containing the Property object is returned. Otherwise,
     |                  an empty Optional is returned. If a database error
     |                  occurs, a RuntimeException is thrown.
     |
     |  Parameters:
     |      id -- The unique identifier (Long) of the property to find.
     |
     |  Returns:  An Optional<Property>. It contains the Property object if found,
     |            otherwise it is empty.
     *-------------------------------------------------------------------*/
    public Optional<Property> findPropertyById(Long id) {
        String sql = "SELECT * FROM stephintomson.Property WHERE property_id = ?";

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

    /*---------------------------------------------------------------------
     |  Method mapRowToProperty
     |
     |  Purpose:  A private helper method that converts a row from a ResultSet
     |            into a Property object. This method encapsulates the logic for
     |            extracting data (property_id, property_type,
     |            property_monthly_income) from the ResultSet and instantiating
     |            a Property record.
     |
     |  Pre-condition:  The 'resultSet' parameter is a valid ResultSet, and its
     |                  cursor is positioned on a row containing property data.
     |                  The ResultSet must contain columns corresponding to
     |                  property_id, property_type, and property_monthly_income
     |                  in that order or accessible by their names.
     |
     |  Post-condition: A Property object is created and populated with data
     |                  from the current row of the ResultSet.
     |
     |  Parameters:
     |      resultSet -- The ResultSet from which to extract property data.
     |                   It is expected to be pointing to a valid row.
     |
     |  Returns:  A Property object initialized with the data from the current
     |            ResultSet row.
     *-------------------------------------------------------------------*/
    private Property mapRowToProperty(ResultSet resultSet) throws SQLException {
        Long property_id = resultSet.getLong(1);
        String property_type = resultSet.getString(2);
        int property_monthly_income = resultSet.getInt(3);

        return new Property(property_id, property_type, property_monthly_income);
    }

    /*---------------------------------------------------------------------
     |  Method updateProperty
     |
     |  Purpose:  Updates an existing property's details in the database.
     |            It uses the property's ID to identify the record to update
     |            and modifies its type and monthly income.
     |
     |  Pre-condition:  The 'property' object must have a non-null 'property_id'
     |                  and contain the updated information for 'property_type'
     |                  and 'property_monthly_income'. The database connection
     |                  must be active. A property with the given ID must exist
     |                  in the database for any rows to be affected.
     |
     |  Post-condition: If a property with the given ID exists, its record in
     |                  the 'stephintomson.Property' table is updated with the
     |                  new details. If no rows are affected (e.g., property not
     |                  found), an SQLException is thrown (wrapped in a
     |                  RuntimeException). If another database error occurs, a
     |                  RuntimeException is thrown.
     |
     |  Parameters:
     |      property -- A Property object containing the ID of the property to
     |                  update and its new details (type and monthly income).
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void updateProperty(Property property) {
        String sql = "UPDATE stephintomson.Property SET property_type = ?, property_monthly_income = ? WHERE property_id = ?";

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

    /*---------------------------------------------------------------------
     |  Method deleteProperty
     |
     |  Purpose:  Deletes a property from the database based on its ID,
     |            which is obtained from the provided Property object.
     |
     |  Pre-condition:  The 'property' object must have a non-null 'property_id'.
     |                  The database connection must be active.
     |
     |  Post-condition: If a property with the given ID exists, its record is
     |                  deleted from the 'stephintomson.Property' table. If no
     |                  rows are affected (e.g., property not found), an
     |                  SQLException is thrown (wrapped in a RuntimeException).
     |                  If another database error occurs, a RuntimeException
     |                  is thrown.
     |
     |  Parameters:
     |      property -- A Property object whose 'property_id' is used to
     |                  identify the record to be deleted. Other fields in
     |                  the object are not used by this method.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void deleteProperty(Property property) {
        String sql = "DELETE FROM stephintomson.Property WHERE property_id = ?";

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