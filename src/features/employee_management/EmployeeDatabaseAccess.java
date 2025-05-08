package features.employee_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class EmployeeDatabaseAccess
 ||
 ||      Author:  Matthew Song
 ||
 ||      Purpose:  Handles database operations for employees (CRUD).
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
 ||   Constructors:  EmployeeDatabaseAccess(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long createEmployee(Employee employee)
 ||                  Optional<Employee> findEmployeeById(Long id)
 ||                  void updateEmployee(Employee employee)
 ||                  void deleteEmployee(Employee employee)
 ||                  private Employee mapRowToEmployee(ResultSet resultSet) throws SQLException
 ||
 ++-----------------------------------------------------------------------*/
public class EmployeeDatabaseAccess extends DatabaseAccess {

    /*---------------------------------------------------------------------
     |  Method EmployeeDatabaseAccess (Constructor)
     |
     |  Purpose:  Initializes with a database connection.
     |
     |  Pre-condition:  Valid databaseConnection.
     |
     |  Post-condition: Instance ready for database operations.
     |
     |  Parameters:
     |      databaseConnection -- Database connection.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public EmployeeDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createEmployee
     |
     |  Purpose:  Creates a new employee record in the database.
     |            Uses SYSTIMESTAMP for start_date.
     |
     |  Pre-condition:  Valid employee object; active DB connection.
     |                  Assumes 'Employee' table and 'employee_id' sequence exist.
     |
     |  Post-condition: Employee created, new ID returned. Throws RuntimeException
     |                  on SQL error or if no rows affected.
     |
     |  Parameters:
     |      employee -- Employee data to create. 'start_date' in object is ignored.
     |
     |  Returns:  ID (Long) of the new employee.
     *-------------------------------------------------------------------*/
    public Long createEmployee(Employee employee) {
        String sql = "INSERT into stephintomson.Employee (employee_id, name, age, gender, position, salary, start_date, property_id VALUES (?, ?, ?, ?, ?, ?, SYSTIMESTAMP, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newEmployeeId = getNextId("Employee", "employee_id");

            preparedStatement.setLong(1, newEmployeeId);
            preparedStatement.setString(2, employee.name());
            preparedStatement.setString(3, employee.age());
            preparedStatement.setString(4, employee.gender());
            preparedStatement.setString(5, employee.position());
            preparedStatement.setInt(6, employee.salary());
            // SYSTIMESTAMP
            preparedStatement.setLong(8, employee.property_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed; No rows affected.");
            }

            return newEmployeeId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method findEmployeeById
     |
     |  Purpose:  Finds an employee by ID.
     |
     |  Pre-condition:  Valid ID; active DB connection.
     |                  SQL uses 'id' for column name; ensure this matches table schema.
     |
     |  Post-condition: Returns Optional of employee if found. Throws
     |                  RuntimeException on SQL error.
     |
     |  Parameters:
     |      id -- Employee ID to find.
     |
     |  Returns:  Optional<Employee>. Empty if not found.
     *-------------------------------------------------------------------*/
    public Optional<Employee> findEmployeeById(Long id) {
        String sql = "SELECT * FROM stephintomson.Employee WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Employee employee = mapRowToEmployee(resultSet);
                return Optional.of(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method mapRowToEmployee
     |
     |  Purpose:  Maps a ResultSet row to an Employee object.
     |
     |  Pre-condition:  ResultSet pointing to a valid row with employee data.
     |                  Column names in ResultSet match expected values.
     |
     |  Post-condition: Returns mapped Employee object. Throws SQLException
     |                  if column access fails.
     |
     |  Parameters:
     |      resultSet -- ResultSet with employee data.
     |
     |  Returns:  Employee object.
     *-------------------------------------------------------------------*/
    private Employee mapRowToEmployee(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("employee_id");
        String name = resultSet.getString("name");
        String age = resultSet.getString("age");
        String gender = resultSet.getString("gender");
        String position = resultSet.getString("position");
        int salary = resultSet.getInt("salary");
        Timestamp start_date = resultSet.getTimestamp("start_date");
        long property_id = resultSet.getLong("property_id");

        return new Employee(id, name, age, gender, position, salary, start_date, property_id);
    }

    /*---------------------------------------------------------------------
     |  Method updateEmployee
     |
     |  Purpose:  Updates an existing employee's record.
     |
     |  Pre-condition:  Valid employee object with ID; active DB connection.
     |                  SQL uses 'id' for WHERE clause; ensure matches schema.
     |
     |  Post-condition: Employee updated. Throws RuntimeException on SQL
     |                  error or if no rows affected.
     |
     |  Parameters:
     |      employee -- Employee data to update. employee_id identifies record.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void updateEmployee(Employee employee) {
        String sql = "UPDATE stephintomson.Employee SET name = ?, age = ?, gender = ?, position = ?, salary = ?, start_date = ?, property_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.name());
            preparedStatement.setString(2, employee.age());
            preparedStatement.setString(3, employee.gender());
            preparedStatement.setString(4, employee.position());
            preparedStatement.setInt(5, employee.salary());
            preparedStatement.setTimestamp(6, employee.start_date());
            preparedStatement.setLong(7, employee.property_id());
            preparedStatement.setLong(8, employee.employee_id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteEmployee
     |
     |  Purpose:  Deletes an employee's record by ID.
     |
     |  Pre-condition:  Valid employee object with ID; active DB connection.
     |                  SQL uses 'id' for WHERE clause; ensure matches schema.
     |
     |  Post-condition: Employee deleted. Throws RuntimeException on SQL
     |                  error or if no rows affected.
     |
     |  Parameters:
     |      employee -- Employee to delete (uses employee_id).
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void deleteEmployee(Employee employee) {
        String sql = "DELETE FROM stephintomson.Employee WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, employee.employee_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting employee failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}