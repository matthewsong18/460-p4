package features.lesson_management;

import java.sql.*;
import java.util.Optional;

class LessonDatabaseAccess {
    private final Connection connection;

    private static Long currentId;

    public LessonDatabaseAccess(Connection connection) {
        this.connection = connection;
    }

    public Long createLesson(Lesson lesson) {
        String sql = "INSERT into Lesson VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, getNextId());
            preparedStatement.setLong(2, lesson.instructor_id());
            preparedStatement.setTimestamp(3, lesson.lesson_time());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            return currentId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCurrentId() {
        String sql = "SELECT max(lesson_id) FROM Lesson";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                currentId = resultSet.next() ? resultSet.getLong("max(lesson_id)") : 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getNextId() {
        if (currentId == null) {
            updateCurrentId();
        }
        return ++currentId;
    }

    public Optional<Lesson> findLessonById(Long id) {
        String sql = "SELECT * FROM Lesson WHERE lesson_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Lesson lesson = mapToLesson(resultSet);
                    return Optional.of(lesson);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Lesson mapToLesson(ResultSet resultSet) throws SQLException {
        Long lesson_id = resultSet.getLong("lesson_id");
        Long instructor_id = resultSet.getLong("instructor_id");
        Timestamp lesson_time = resultSet.getTimestamp("lesson_time");

        return new Lesson(lesson_id, instructor_id, lesson_time);
    }

    public void updateLesson(Lesson lesson) {
        String sql = "UPDATE Lesson SET instructor_id = ?, lesson_time = ? WHERE lesson_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, lesson.instructor_id());
            preparedStatement.setTimestamp(2, lesson.lesson_time());
            preparedStatement.setLong(3, lesson.lesson_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating lesson failed; No rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLesson(Lesson lesson) {
        String sql = "DELETE from Lesson WHERE lesson_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, lesson.lesson_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating lesson failed; No rows affected.");
            }

            if (lesson.lesson_id() == currentId)    // Update the next ID from database if the current max was deleted
                updateCurrentId();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}