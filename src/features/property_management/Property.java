/*
 * File: Property.java
 * 
 * Author: Matthew Song
 * 
 * Description: Local representation of a property tuple in the database.
 * 
 */

package features.property_management;

import common.InputValidation;

public record Property(
        Long property_id,
        String property_type,
        int property_monthly_income
) {
    public Property(String property_type, int property_monthly_income) {
        this(null, property_type, property_monthly_income);
    }

    public Property {
        checkPropertyType(property_type);
        checkPropertyIncome(property_monthly_income);
    }

    private void checkPropertyType(String propertyType) {
        if (InputValidation.checkStringBlank(propertyType)) {
            throw new IllegalArgumentException("Property type cannot be blank");
        }
    }

    private void checkPropertyIncome(int propertyMonthlyIncome) {
        if (propertyMonthlyIncome < 0) {
            throw new IllegalArgumentException("Property Monthly income cannot be negative");
        }
    }
}
