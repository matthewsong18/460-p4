package features.employee_management;

import common.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class EmployeeDatabaseAccess {
    Connection connection;

    public EmployeeDatabaseAccess(DatabaseConnection databaseConnection) {
        connection = databaseConnection.getConnection();
    }

    public Long createEmployee(Employee employee) {
        String sql = "INSERT into employees (name, age, gender, position, salary, start_date property_id VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setEmployeePreparedStatement(employee, preparedStatement);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed; No rows affected.");
            }

            // Get the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating employee failed; No ID obtained.");
                }
                return generatedKeys.getLong(1);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setEmployeePreparedStatement(Employee employee,
                                              PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, employee.name());
        preparedStatement.setString(2, employee.age());
        preparedStatement.setString(3, employee.gender());
        preparedStatement.setString(4, employee.position());
        preparedStatement.setInt(5, employee.salary());
        preparedStatement.setTimestamp(6, employee.start_date());
        preparedStatement.setLong(7, employee.property_id());
    }

    public Optional<Employee> findEmployeeById(Long id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
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
        String sql = "UPDATE employees SET name = ?, age = ?, gender = ?, position = ?, salary = ?, start_date = ?, property_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setEmployeePreparedStatement(employee, preparedStatement);
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
        String sql = "DELETE FROM employees WHERE id = ?";
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
