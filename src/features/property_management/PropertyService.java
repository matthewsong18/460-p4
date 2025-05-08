package features.property_management;

import common.DatabaseConnection;

import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class PropertyService
 ||
 ||      Author:  Matthew Song, Ethan Huang
 ||
 ||      Purpose:  This class provides a client-facing API for managing
 ||                properties. It acts as a service layer that abstracts
 ||                the underlying database operations by using an instance
 ||                of PropertyDatabaseAccess. It handles operations such
 ||                as adding, finding, updating, and deleting properties,
 ||                including input validation and exception handling.
 ||
 ||  Inherits From:  None.
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  PropertyService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long addProperty(String property_type, int property_monthly_income)
 ||                  Optional<Property> findPropertyById(Long property_id)
 ||                  void updateProperty(Property property)
 ||                  void deleteProperty(Property property)
 ||
 ++-----------------------------------------------------------------------*/
public class PropertyService {
    private final PropertyDatabaseAccess propertyDatabaseAccess;

    /*---------------------------------------------------------------------
     |  Method PropertyService (Constructor)
     |
     |  Purpose:  Constructs a new PropertyService object, initializing it
     |            with a database connection which is then used to create
     |            an instance of PropertyDatabaseAccess. This setup allows
     |            the service to interact with the property data store.
     |
     |  Pre-condition:  The databaseConnection object must be a valid, open
     |                  connection to the database.
     |
     |  Post-condition: A new PropertyService instance is created with its
     |                  own PropertyDatabaseAccess object, ready to perform
     |                  property-related services.
     |
     |  Parameters:
     |      databaseConnection -- An instance of DatabaseConnection that provides
     |                            the active connection to the database, which will
     |                            be passed to the PropertyDatabaseAccess layer.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public PropertyService(DatabaseConnection databaseConnection) {
        this.propertyDatabaseAccess = new PropertyDatabaseAccess(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method addProperty
     |
     |  Purpose:  Adds a new property to the system. It creates a Property
     |            object from the given parameters and then calls the
     |            PropertyDatabaseAccess layer to persist it.
     |
     |  Pre-condition:  property_type is a valid string (not blank, as per
     |                  Property record validation) and property_monthly_income
     |                  is a non-negative integer. The database connection
     |                  must be active via propertyDatabaseAccess.
     |
     |  Post-condition: A new property is created in the database, and its
     |                  generated ID is returned. If the creation fails at the
     |                  database access layer (e.g., SQL issue, validation
     |                  in Property record), a RuntimeException is thrown
     |                  with a descriptive message.
     |
     |  Parameters:
     |      property_type -- The type of the property (e.g., "Apartment").
     |                       Validation for blankness occurs in Property record.
     |      property_monthly_income -- The monthly income from the property.
     |                                 Validation for non-negativity occurs in Property record.
     |
     |  Returns:  A Long representing the unique ID assigned to the newly
     |            added property.
     *-------------------------------------------------------------------*/
    public Long addProperty(String property_type, int property_monthly_income) {
        Property property = new Property(property_type, property_monthly_income);

        try {
            return propertyDatabaseAccess.createProperty(property);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to add property", e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method findPropertyById
     |
     |  Purpose:  Retrieves a property from the system based on its unique ID.
     |            It first validates that the provided ID is not null, then
     |            delegates the search to the PropertyDatabaseAccess layer.
     |
     |  Pre-condition:  The database connection must be active via
     |                  propertyDatabaseAccess.
     |
     |  Post-condition: If a property with the given ID exists, an Optional
     |                  containing the Property object is returned. Otherwise,
     |                  an empty Optional is returned. Throws an
     |                  IllegalArgumentException if property_id is null.
     |                  If a database error occurs, a RuntimeException is thrown.
     |
     |  Parameters:
     |      property_id -- The unique identifier (Long) of the property to find.
     |                     Cannot be null.
     |
     |  Returns:  An Optional<Property>. It contains the Property object if found,
     |            otherwise it is empty.
     *-------------------------------------------------------------------*/
    public Optional<Property> findPropertyById(Long property_id) {
        if (property_id == null) {
            throw new IllegalArgumentException("property_id cannot be null");
        }

        try {
            return propertyDatabaseAccess.findPropertyById(property_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find property with id " + property_id, e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method updateProperty
     |
     |  Purpose:  Updates the details of an existing property. It validates
     |            that the property object and its ID are not null, then calls
     |            the PropertyDatabaseAccess layer to perform the update.
     |
     |  Pre-condition:  The 'property' object must not be null, and its
     |                  'property_id' field must not be null and must
     |                  correspond to an existing property. The database
     |                  connection must be active via propertyDatabaseAccess.
     |
     |  Post-condition: The property's record in the database is updated.
     |                  Throws an IllegalArgumentException if the property or its
     |                  ID is null. If the update fails (e.g., property not
     |                  found or database error), a RuntimeException is thrown.
     |
     |  Parameters:
     |      property -- A Property object containing the ID of the property to
     |                  update and its new details.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void updateProperty(Property property) {
        if (property == null || property.property_id() == null) {
            throw new IllegalArgumentException("property_id cannot be null");
        }

        try {
            propertyDatabaseAccess.updateProperty(property);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update property with id " + property.property_id(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteProperty
     |
     |  Purpose:  Deletes a property from the system using its Property object
     |            (primarily its ID). It validates that the property object
     |            and its ID are not null, then calls the
     |            PropertyDatabaseAccess layer to perform the deletion.
     |
     |  Pre-condition:  The 'property' object must not be null, and its
     |                  'property_id' field must not be null and correspond
     |                  to an existing property. The database connection must
     |                  be active via propertyDatabaseAccess.
     |
     |  Post-condition: The property's record is deleted from the database.
     |                  Throws an IllegalArgumentException if the property or its
     |                  ID is null. If the deletion fails (e.g., property not
     |                  found or database error), a RuntimeException is thrown.
     |
     |  Parameters:
     |      property -- A Property object whose ID is used to identify the
     |                  record to be deleted.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void deleteProperty(Property property) {
        if (property == null || property.property_id() == null) {
            throw new IllegalArgumentException("property_id cannot be null");
        }

        try {
            propertyDatabaseAccess.deleteProperty(property);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete property with id " + property.property_id(), e);
        }
    }
}