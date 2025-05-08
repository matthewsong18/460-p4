package features.lesson_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class LessonDatabaseAccess extends DatabaseAccess {

    public LessonDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createLesson(Lesson lesson) {
        String sql = "INSERT into stephintomson.Lesson VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newId = getNextId("Lesson", "lesson_id");

            preparedStatement.setLong(1, newId);
            preparedStatement.setLong(2, lesson.instructor_id());
            preparedStatement.setTimestamp(3, lesson.lesson_time());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            return newId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Lesson> findLessonById(Long id) {
        String sql = "SELECT * FROM stephintomson.Lesson WHERE lesson_id = ?";

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

    // This method is used to check if a lesson exists for a given member ID.
    // It returns true if at least one lesson exists for the member, false
    // otherwise.
    // Active lesson return 1, else 0
    public int hasLessonByMember(Long memberId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS lesson_count
                  FROM stephintomson.LessonOrder
                 WHERE member_id = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, memberId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt("lesson_count") > 0 ? 1 : 0;
            } else {
                throw new SQLException("Member not found for lesson check, ID: " + memberId);
            }
        }
    }

    private Lesson mapToLesson(ResultSet resultSet) throws SQLException {
        Long lesson_id = resultSet.getLong("lesson_id");
        Long instructor_id = resultSet.getLong("instructor_id");
        Timestamp lesson_time = resultSet.getTimestamp("lesson_time");

        return new Lesson(lesson_id, instructor_id, lesson_time);
    }

    public void updateLesson(Lesson lesson) {
        String sql = "UPDATE stephintomson.Lesson SET instructor_id = ?, lesson_time = ? WHERE lesson_id = ?";

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
        String sql = "DELETE from stephintomson.Lesson WHERE lesson_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, lesson.lesson_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating lesson failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}