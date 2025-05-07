package features.employee_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class EmployeeDatabaseAccess extends DatabaseAccess {

    public EmployeeDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createEmployee(Employee employee) {
        String sql = "INSERT into Employee (employee_id, name, age, gender, position, salary, start_date, property_id VALUES (?, ?, ?, ?, ?, ?, SYSTIMESTAMP, ?)";
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

    public Optional<Employee> findEmployeeById(Long id) {
        String sql = "SELECT * FROM Employee WHERE id = ?";
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

    public void updateEmployee(Employee employee) {
        String sql = "UPDATE Employee SET name = ?, age = ?, gender = ?, position = ?, salary = ?, start_date = ?, property_id = ? WHERE id = ?";

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

    public void deleteEmployee(Employee employee) {
        String sql = "DELETE FROM Employee WHERE id = ?";
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
