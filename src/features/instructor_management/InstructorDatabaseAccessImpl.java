package features.instructor_management;

import java.sql.*;
import java.util.Optional;

public class InstructorDatabaseAccessImpl implements IInstructorDatabaseAccess {

    private final Connection connection;

    public InstructorDatabaseAccessImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Long createInstructor(Instructor instructor) {
        String sql = "INSERT INTO instructors (employee_id, certification_level) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, instructor.employee_id());
            preparedStatement.setString(2, instructor.certification_level());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating instructor failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating instructor failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Instructor> findInstructorById(Long instructorId) {
        String sql = "SELECT id, employee_id, certification_level FROM instructors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, instructorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToInstructor(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding instructor by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void updateInstructor(Instructor instructor) {
        String sql = "UPDATE instructors SET employee_id = ?, certification_level = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, instructor.employee_id());
            preparedStatement.setString(2, instructor.certification_level());
            preparedStatement.setLong(3, instructor.id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating instructor failed, no rows affected. Instructor ID: " + instructor.id());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteInstructor(Instructor instructor) {
        String sql = "DELETE FROM instructors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, instructor.id());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting instructor failed, no rows affected. Instructor ID: " + instructor.id());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Instructor> findInstructorByEmployeeId(Long employeeId) {
        String sql = "SELECT id, employee_id, certification_level FROM instructors WHERE employee_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToInstructor(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding instructor by employee ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Instructor mapRowToInstructor(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long employeeId = resultSet.getLong("employee_id");
        String certificationLevel = resultSet.getString("certification_level");
        return new Instructor(id, employeeId, certificationLevel);
    }
}
