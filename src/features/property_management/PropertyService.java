/*
 * File: PropertyService.java
 * 
 * Author: Matthew Song
 *         Ethan Huang
 * 
 * Description: Client-facing API for database services on the property table.
 * 
 */

package features.property_management;

import common.DatabaseConnection;

import java.util.Optional;

public class PropertyService {
    private final PropertyDatabaseAccess propertyDatabaseAccess;

    public PropertyService(DatabaseConnection databaseConnection) {
        this.propertyDatabaseAccess = new PropertyDatabaseAccess(databaseConnection);
    }

    public Long addProperty(String property_type, int property_monthly_income) {
        Property property = new Property(property_type, property_monthly_income);

        try {
            return propertyDatabaseAccess.createProperty(property);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to add property", e);
        }
    }

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
