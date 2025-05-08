package features.employee_management;

import common.InputValidation;

/*+----------------------------------------------------------------------
 ||
 ||  Class Instructor
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Data carrier for Instructor records
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  Instructor(Employee employee, String certification_level)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  Long employee_id()
 ||
 ++-----------------------------------------------------------------------*/



public record Instructor(Employee employee, String certification_level) {
    
    // Checks that instance variables are not blank
    public Instructor {
        if (employee == null || employee.employee_id() == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (InputValidation.checkStringBlank(certification_level)) {
            throw new IllegalArgumentException("Certification level cannot be null");
        }
    }

    // Getter for instructor_id (references matching Employee's employee_id)
    public Long employee_id() {
        return employee.employee_id();
    }
}