package features.employee_management;

import common.DatabaseConnection;
import features.property_management.Property;
import features.property_management.PropertyService;

import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class EmployeeService
 ||
 ||      Author:  Matthew Song
 ||
 ||      Purpose:  Service layer for employee management. Handles business
 ||                logic for adding, finding, updating, and deleting employees.
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
 ||   Constructors:  EmployeeService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long addEmployee(String name, String age, String gender, String position, int salary, Long property_id)
 ||                  Optional<Employee> getEmployeeById(Long employee_id)
 ||                  void updateEmployee(Employee updatedEmployee)
 ||                  void deleteEmployee(Employee employee)
 ||                  private void checkProperty(Long property_id)
 ||
 ++-----------------------------------------------------------------------*/
public class EmployeeService {
    private DatabaseConnection databaseConnection;
    private EmployeeDatabaseAccess employeeDatabaseAccess;

    /*---------------------------------------------------------------------
     |  Method EmployeeService (Constructor)
     |
     |  Purpose:  Initializes service with DB connection and EmployeeDatabaseAccess.
     |
     |  Pre-condition:  Valid databaseConnection.
     |
     |  Post-condition: Service ready for employee operations.
     |
     |  Parameters:
     |      databaseConnection -- Database connection.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public EmployeeService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.employeeDatabaseAccess = new EmployeeDatabaseAccess(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method addEmployee
     |
     |  Purpose:  Adds a new employee after checking associated property validity.
     |
     |  Pre-condition:  Valid employee details & property_id. Active DB connection.
     |                  Property referenced by property_id must exist.
     |
     |  Post-condition: Employee added, ID returned. Throws RuntimeException
     |                  if property not found or on database error.
     |
     |  Parameters:
     |      name -- Employee's name.
     |      age -- Employee's age (as String).
     |      gender -- Employee's gender.
     |      position -- Employee's job position.
     |      salary -- Employee's salary.
     |      property_id -- ID of property associated with employee.
     |
     |  Returns:  ID (Long) of the newly added employee.
     *-------------------------------------------------------------------*/
    public Long addEmployee(String name, String age, String gender, String position, int salary, Long property_id) {
        Employee employee = new Employee(name, age, gender, position, salary, property_id);

        // Check property
        checkProperty(property_id);

        try {
            return employeeDatabaseAccess.createEmployee(employee);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to add the employee with id " + employee.employee_id(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkProperty
     |
     |  Purpose:  Checks if a property with the given ID exists.
     |
     |  Pre-condition:  Valid property_id. Database connection is active.
     |
     |  Post-condition: Completes if property exists. Throws RuntimeException
     |                  if property is not found.
     |
     |  Parameters:
     |      property_id -- The ID of the property to check.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkProperty(Long property_id) {
        PropertyService propertyService = new PropertyService(databaseConnection);
        Optional<Property> propertyOptional = propertyService.findPropertyById(property_id);
        if (propertyOptional.isEmpty()) {
            throw new RuntimeException("Property with id " + property_id + " not found");
        }
    }

    /*---------------------------------------------------------------------
     |  Method getEmployeeById
     |
     |  Purpose:  Retrieves an employee by their ID.
     |
     |  Pre-condition:  Active DB connection. Employee ID may be null.
     |
     |  Post-condition: Returns Optional of employee. Throws IllegalArgumentException
     |                  if ID is null, RuntimeException on DB error.
     |
     |  Parameters:
     |      employee_id -- Employee ID to find.
     |
     |  Returns:  Optional<Employee>. Empty if not found.
     *-------------------------------------------------------------------*/
    public Optional<Employee> getEmployeeById(Long employee_id) {
        if (employee_id == null) {
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        try {
            return employeeDatabaseAccess.findEmployeeById(employee_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find the employee with id " + employee_id, e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method updateEmployee
     |
     |  Purpose:  Updates an existing employee's details.
     |
     |  Pre-condition:  Valid employee object with ID. Active DB connection.
     |
     |  Post-condition: Employee updated. Throws IllegalArgumentException if
     |                  employee or ID is null, RuntimeException on DB error.
     |
     |  Parameters:
     |      updatedEmployee -- Employee data for update.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void updateEmployee(Employee updatedEmployee) {
        if (updatedEmployee == null || updatedEmployee.employee_id() == null) {
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        try {
            employeeDatabaseAccess.updateEmployee(updatedEmployee);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update the employee with id " + updatedEmployee.employee_id(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteEmployee
     |
     |  Purpose:  Deletes an employee.
     |
     |  Pre-condition:  Valid employee object with ID. Active DB connection.
     |
     |  Post-condition: Employee deleted. Throws IllegalArgumentException if
     |                  employee or ID is null, RuntimeException on DB error.
     |
     |  Parameters:
     |      employee -- Employee to delete.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void deleteEmployee(Employee employee) {
        if (employee == null || employee.employee_id() == null) {
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        try {
            employeeDatabaseAccess.deleteEmployee(employee);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete the employee with id " + employee.employee_id(), e);
        }
    }
}