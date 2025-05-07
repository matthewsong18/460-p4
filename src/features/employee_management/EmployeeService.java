package features.employee_management;

import common.DatabaseConnection;
import features.property_management.Property;
import features.property_management.PropertyService;

import java.sql.Timestamp;
import java.util.Optional;

public class EmployeeService {
    private DatabaseConnection databaseConnection;
    private EmployeeDatabaseAccess employeeDatabaseAccess;

    public EmployeeService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.employeeDatabaseAccess = new EmployeeDatabaseAccess(databaseConnection);
    }

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

    private void checkProperty(Long property_id) {
        PropertyService propertyService = new PropertyService(databaseConnection);
        Optional<Property> propertyOptional = propertyService.findPropertyById(property_id);
        if (propertyOptional.isEmpty()) {
            throw new RuntimeException("Property with id " + property_id + " not found");
        }
    }

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
