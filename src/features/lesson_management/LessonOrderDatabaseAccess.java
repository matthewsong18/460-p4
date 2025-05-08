/**
 * @author Ethan Huang
 */

package features.lesson_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LessonOrderDatabaseAccess extends DatabaseAccess {
    public LessonOrderDatabaseAccess(DatabaseConnection connection) {
        super(connection);
    }

    public Long createLessonOrder(LessonOrder lessonOrder) {
        String sql = "INSERT into stephintomson.LessonOrder VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            long id = getNextId("LessonOrder", "order_id");
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, lessonOrder.lesson_id());
            preparedStatement.setLong(3, lessonOrder.member_id());
            preparedStatement.setInt(4, lessonOrder.total_sessions());
            preparedStatement.setInt(5, lessonOrder.remaining_sessions());

            int affectRows = preparedStatement.executeUpdate();
            if (affectRows == 0) {
                throw new SQLException("Creating lesson order failed; No rows affected.");
            }

            return id;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<LessonOrder> findLessonOrderById(Long order_id) {
        String sql = "SELECT * FROM stephintomson.LessonOrder WHERE order_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, order_id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new LessonOrder(
                            resultSet.getLong("order_id"),
                            resultSet.getLong("lesson_id"),
                            resultSet.getLong("member_id"),
                            resultSet.getInt("total_sessions"),
                            resultSet.getInt("remaining_sessions")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public void updateLessonOrder(LessonOrder lessonOrder) {
        String sql = "UPDATE stephintomson.LessonOrder SET lesson_id = ?, member_id = ?, total_sessions = ?, " +
                "remaining_sessions = ? WHERE order_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, lessonOrder.lesson_id());
            preparedStatement.setLong(2, lessonOrder.member_id());
            preparedStatement.setInt(3, lessonOrder.total_sessions());
            preparedStatement.setInt(4, lessonOrder.remaining_sessions());
            preparedStatement.setLong(5, lessonOrder.order_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update lesson order failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLessonOrder(LessonOrder lessonOrder) {
        String sql = "DELETE from stephintomson.LessonOrder WHERE order_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, lessonOrder.order_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Delete lesson order failed; No rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}