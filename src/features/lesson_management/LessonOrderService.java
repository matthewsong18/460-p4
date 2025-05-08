package features.lesson_management;

import java.util.Optional;
import common.DatabaseConnection;
import features.member_management.*;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonOrderService
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Service layer to provide an interface for CRUD 
 ||                  operations and validation for referential integrity
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  LessonOrderService(DatabaseConnection connection)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  
 ||     Long createLessonOrder(long lesson_id, long member_id, int sessions)
 ||     Optional<LessonOrder> findLessonOrderById(Long order_id)
 ||     void updateLessonOrder(LessonOrder lessonOrder)
 ||     void deleteLessonOrder(LessonOrder lessonOrder)
 ||
 ++-----------------------------------------------------------------------*/

public class LessonOrderService {
    private LessonOrderDatabaseAccess databaseAccess;
    private DatabaseConnection databaseConnection;

    public LessonOrderService(DatabaseConnection connection) {
        databaseAccess = new LessonOrderDatabaseAccess(connection);
        this.databaseConnection = connection;
    }

    /*---------------------------------------------------------------------
     |  Method createLessonOrder(long lesson_id, long member_id, int sessions)
     |
     |  Purpose:  Creates a lesson order with the given parameters in the 
     |      database.
     |
     |  Pre-condition:  lesson_id and member_id correspond to existing 
     |      database records, sessions > 0
     |
     |  Post-condition: Lesson order is added to the LessonOrder database
     |      relation.
     |
     |  Parameters: 
     |      long lesson_id -- lesson_id of existing Lesson record
     |      long member_id -- member_id of existing Member record
     |      int sessions -- Total number of sessions for the lesson order
     |
     |  Returns:  Primary key of new LessonOrder record
     *-------------------------------------------------------------------*/
    
    public Long createLessonOrder(long lesson_id, long member_id, int sessions) {
        if (sessions <= 0)
            throw new IllegalArgumentException("Sessions cannot be less than 1");
        
        try {
            checkLesson(lesson_id);
            checkMember(member_id);

            return databaseAccess.createLessonOrder(new LessonOrder(lesson_id, member_id, sessions, sessions));

        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating lesson order", e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkLesson(long lesson_id)
     |
     |  Purpose:  Checks if a Lesson record exists in the database.
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: An error is thrown if no corresponding lesson was
     |      found.
     |
     |  Parameters:
     |      long lesson_id -- lesson_id to find in Lesson relation
     |
     |  Returns:  None
     *-------------------------------------------------------------------*/
    
    private void checkLesson(long lesson_id) {
        LessonService lessonService = new LessonService(databaseConnection);
        
        Optional<Lesson> lessonOpt = lessonService.findLessonById(lesson_id);
        if (lessonOpt.isEmpty()) {
            throw new RuntimeException("Lesson with id " + lesson_id + " not found");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkMember(long member_id)
     |
     |  Purpose:  Checks if a Member record exists in the database.
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: An error is thrown if no corresponding member was 
     |      found.
     |
     |  Parameters: 
     |      long member_id -- member_id to find the in Member relation
     |
     |  Returns:  None
     *-------------------------------------------------------------------*/
    
    private void checkMember(long member_id) {
        MemberService memberService = new MemberService(databaseConnection);
        
        Optional<Member> memberOpt = memberService.findMemberById(member_id);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Member with id " + member_id + " not found");
        }
    }

    /*---------------------------------------------------------------------
     |  Method findLessonOrderById(Long order_id)
     |
     |  Purpose:  [Explain what this method does to support the correct
     |      operation of its class, and how it does it.]
     |
     |  Pre-condition:  order_id is > 0 and not null.
     |
     |  Post-condition: An error is thrown if an SQL error was given.
     |
     |  Parameters: 
     |      Long order_id -- order_id to find in Lesson relation
     |
     |  Returns:  Optional class with the LessonOrder record if found, 
     |      otherwise empty.
     *-------------------------------------------------------------------*/
    
    public Optional<LessonOrder> findLessonOrderById(Long order_id) {
        if (order_id == null || order_id <= 0) {
            throw new IllegalArgumentException("Lesson order id cannot be null");
        }

        try {
            return databaseAccess.findLessonOrderById(order_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find lesson order with id = " + order_id, e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method updateLessonOrder(LessonOrder lessonOrder)
     |
     |  Purpose:  Updates an existing LessonOrder record in the database.
     |
     |  Pre-condition:  lessonOrder matches a LessonOrder record on 
     |      order_id in the database.
     |
     |  Post-condition: Corresponding LessonOrder record contains the 
     |      updated attributes from lessonOrder parameter.
     |
     |  Parameters: 
     |      LessonOrder lessonOrder -- contains the order_id to find and 
     |          updated attributes
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
    public void updateLessonOrder(LessonOrder lessonOrder) {
        if (lessonOrder == null || lessonOrder.order_id() == null) {
            throw new IllegalArgumentException("Lesson order cannot be null");
        }

        try {
            databaseAccess.updateLessonOrder(lessonOrder);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update lesson order with id = " + lessonOrder.order_id(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteLessonOrder(LessonOrder lessonOrder)
     |
     |  Purpose:  Deletes a LessonOrder record from the database.
     |
     |  Pre-condition:  lessonOrder matches a LessonOrder record on
     |      order_id in the database, corresponding record does not have
     |      any used sessions (remaining_sessions = total_sessions)
     |
     |  Post-condition: Corresponding LessonOrder is deleted from the 
     |      database.
     |
     |  Parameters: 
     |      LessonOrder lessonOrder -- contains the order_id to find and 
     |          updated attributes
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
    public void deleteLessonOrder(LessonOrder lessonOrder) {
        if (lessonOrder == null || lessonOrder.order_id() == null) {
            throw new IllegalArgumentException("Lesson order cannot be null");
        }

        try {
            if (lessonOrder.remaining_sessions() < lessonOrder.total_sessions()) {  // inside try so that "failed to delete" is also thrown
                throw new RuntimeException("Lesson order must be unused");
            }
            databaseAccess.deleteLessonOrder(lessonOrder);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete lesson order with id = " + lessonOrder.order_id(), e);
        }
    }
}
