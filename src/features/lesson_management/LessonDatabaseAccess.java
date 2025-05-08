package features.lesson_management;

import common.DatabaseAccess;
import common.DatabaseConnection;
import java.sql.*;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonDatabaseAccess
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  [A description of why this class exists.  For what
 ||                   reason was it written?  Which jobs does it perform?]
 ||
 ||  Inherits From:  DatabaseAccess
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  
 ||     LessonDatabaseAccess(databaseConnection) -- calls inherited 
 ||                 constructor
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:
 ||     Long createLesson(Lesson lesson)
 ||     Optional<Lesson> findLessonById(Long id)
 ||     int hasLessonByMember(Long memberId)
 ||     Lesson mapToLesson(ResultSet resultSet)
 ||     void updateLesson(Lesson lesson)
 ||     void deleteLesson(Lesson lesson)
 ||
 ++-----------------------------------------------------------------------*/

public class LessonDatabaseAccess extends DatabaseAccess {

    public LessonDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createLesson(Lesson lesson)
     |
     |  Purpose:  Creates a Lesson record in the database.
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: A new Lesson record exists with the data passed in
     |      by lesson
     |
     |  Parameters: 
     |      Lesson lesson -- Lesson record to be inserted
     |
     |  Returns:  Primary key (lesson_id) of new Lesson record
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method findLessonById(Long id)
     |
     |  Purpose:  Finds a lesson by its lesson_id
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: Returned Optional contains the corresponding Lesson
     |      or is empty.
     |
     |  Parameters: 
     |      Long id -- lesson_id to find
     |
     |  Returns:  Optional object that contains the corresponding Lesson
     |      if found, otherwise empty
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method hasLessonByMember(Long memberId)
     |
     |  Purpose:  Checks if a member has bought a lesson.
     |
     |  Pre-condition:  memberId represents a valid Member in the database.
     |
     |  Post-condition: Database is unchanged.
     |
     |  Parameters: 
     |      Long memberId -- member_id of member to find lessons for
     |
     |  Returns:  1 if the member has bought lessons, otherwise 0
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method mapToLesson(ResultSet resultSet)
     |
     |  Purpose:  Creates a Lesson object from a row returned in an SQL 
     |      query.
     |
     |  Pre-condition:  resultSet points to a valid row in the query result.
     |
     |  Post-condition: Returned Lesson contains all corresponding fields.
     |
     |  Parameters: 
     |      ResultSet resultSet -- points to a row in an SQL query result
     |
     |  Returns:  Lesson object with all corresponding fields.
     *-------------------------------------------------------------------*/
    
    private Lesson mapToLesson(ResultSet resultSet) throws SQLException {
        Long lesson_id = resultSet.getLong("lesson_id");
        Long instructor_id = resultSet.getLong("instructor_id");
        Timestamp lesson_time = resultSet.getTimestamp("lesson_time");

        return new Lesson(lesson_id, instructor_id, lesson_time);
    }

    /*---------------------------------------------------------------------
     |  Method updateLesson(Lesson lesson)
     |
     |  Purpose:  Updates an existing Lesson record with data passed in by
     |      lesson parameter.
     |
     |  Pre-condition:  lesson object matches a Lesson record on lesson_id
     |
     |  Post-condition: Corresponding Lesson record contains updated 
     |      attributes from lesson.
     |
     |  Parameters: 
     |      Lesson lesson -- holds updated attributes
     |
     |  Returns:  [If this method sends back a value via the return
     |      mechanism, describe the purpose of that value here, otherwise
     |      state 'None.']
     *-------------------------------------------------------------------*/
    
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