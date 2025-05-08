package features.employee_management;

import java.sql.*;
import java.util.Optional;

import common.DatabaseAccess;
import common.DatabaseConnection;

/*+----------------------------------------------------------------------
 ||
 ||  Class InstructorDatabaseAccess
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Interfaces with the Instructor database to provide
 ||                  methods for retrieving Instructor records
 ||
 ||  Inherits From:  DatabaseAccess
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  EmployeeDatabaseAccess employeeDatabaseAccess -- provides
 ||                  access to Employee database
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  InstructorDatabaseAccess(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  Optional<Instructor> findInstructorById(Long id)
 ||
 ++-----------------------------------------------------------------------*/

public class InstructorDatabaseAccess extends DatabaseAccess {
    private final EmployeeDatabaseAccess employeeDatabaseAccess;

    public InstructorDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
        employeeDatabaseAccess = new EmployeeDatabaseAccess(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method findInstructorById(Long id)
     |
     |  Purpose:  Finds an Instructor record using its employee_id
     |
     |  Pre-condition:  id matches an Employee record on employee_id
     |
     |  Post-condition: Returned Optional contains the corresponding 
     |      Instructor or is empty
     |
     |  Parameters: Long id -- instructor_id to find
     |
     |  Returns:  Optional object with Instructor if found, otherwise empty
     *-------------------------------------------------------------------*/
    
    public Optional<Instructor> findInstructorById(Long id) {
        String sql = "SELECT * FROM stephintomson.Instructor WHERE employee_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    Long employee_id = resultSet.getLong("employee_id");
                    String certification_level = resultSet.getString("certification_level");
    
                    Optional<Employee> employeeOptional = employeeDatabaseAccess.findEmployeeById(employee_id);
                    if (employeeOptional.isEmpty() || !employeeOptional.get().position().equals("Instructor")) {
                        throw new SQLException("Failed to find instructor with id = " + employee_id);
                    }
    
                    return Optional.of(new Instructor(employeeOptional.get(), certification_level));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}