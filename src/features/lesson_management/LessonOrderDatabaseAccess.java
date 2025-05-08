package features.lesson_management;

import common.DatabaseAccess;
import common.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonOrderDatabaseAccess
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  [A description of why this class exists.  For what
 ||                   reason was it written?  Which jobs does it perform?]
 ||
 ||  Inherits From:  DatabaseAccess
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  
 ||     LessonOrderDatabaseAccess(DatabaseConnection connection)
 ||                 -- calls inherited constructor
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  
 ||     Long createLessonOrder(LessonOrder lessonOrder)
 ||     Optional<LessonOrder> findLessonOrderById(Long order_id)
 ||     void updateLessonOrder(LessonOrder lessonOrder)
 ||     void deleteLessonOrder(LessonOrder lessonOrder)
 ||
 ++-----------------------------------------------------------------------*/

public class LessonOrderDatabaseAccess extends DatabaseAccess {
    public LessonOrderDatabaseAccess(DatabaseConnection connection) {
        super(connection);
    }

    /*---------------------------------------------------------------------
     |  Method createLessonOrder(LessonOrder lessonOrder)
     |
     |  Purpose:  Directly interfaces with the database to create a 
     |      LessonOrder record using an insert statement
     |
     |  Pre-condition:  All lessonOrder attributes except order_id are not 
     |      null. lesson_id and member_id fields correspond to existing
     |      records.
     |
     |  Post-condition: A new LessonOrder record exists with the data 
     |      passed in by lessonOrder
     |
     |  Parameters:
     |      LessonOrder lessonOrder -- LessonOrder record to be inserted
     |
     |  Returns:  Primary key (order_id) of new LessonOrder record.
     *-------------------------------------------------------------------*/
    
    public Long createLessonOrder(LessonOrder lessonOrder) {
        String sql = "INSERT into stephintomson.LessonOrder (order_id, lesson_id, member_id, total_sessions, remaining_sessions) VALUES (?, ?, ?, ?, ?)";

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
            throw new RuntimeException("Failed to insert. Check that Lesson ID and Member ID are valid.");
        }
    }

    /*---------------------------------------------------------------------
     |  Method findLessonOrderById(Long order_id)
     |
     |  Purpose:  Finds a lesson record using its order_id
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: Returned Optional contains a populated LessonOrder
     |      object or is empty
     |
     |  Parameters: 
     |      Long order_id -- order_id of the LessonOrder to find
     |
     |  Returns:  Optional object that contains the LessonOrder record if 
     |      found.
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method updateLessonOrder(LessonOrder lessonOrder)
     |
     |  Purpose:  Updates a LessonOrder record in the database.
     |
     |  Pre-condition:  All lessonOrder attributes are not null. lesson_id
     |      and member_id correspond to existing records.
     |
     |  Post-condition: The LessonOrder database record has been updated 
     |      with the new values.
     |
     |  Parameters: 
     |      LessonOrder lessonOrder -- contains the order_id to find and 
     |          updated attributes
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method deleteLessonOrder(LessonOrder lessonOrder)
     |
     |  Purpose:  Deletes a LessonOrder from the database.
     |
     |  Pre-condition:  LessonOrder record exists in database. lessonOrder
     |      has a non-null order_id.
     |
     |  Post-condition: LessonOrder record with corresponding order_id has
     |      been deleted from the database.
     |
     |  Parameters:
     |      LessonOrder lessonOrder -- LessonOrder record to delete from 
     |          the database.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
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