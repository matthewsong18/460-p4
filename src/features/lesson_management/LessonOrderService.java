/**
 * @author Ethan Huang
 */

package features.lesson_management;

import java.util.Optional;
import common.DatabaseConnection;
import features.member_management.*;

public class LessonOrderService {
    private LessonOrderDatabaseAccess databaseAccess;
    private DatabaseConnection databaseConnection;

    public LessonOrderService(DatabaseConnection connection) {
        databaseAccess = new LessonOrderDatabaseAccess(connection);
        this.databaseConnection = connection;
    }

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

    private void checkLesson(long lesson_id) {
        LessonService lessonService = new LessonService(databaseConnection);
        
        Optional<Lesson> lessonOpt = lessonService.findLessonById(lesson_id);
        if (lessonOpt.isEmpty()) {
            throw new RuntimeException("Lesson with id " + lesson_id + " not found");
        }
    }

    private void checkMember(long member_id) {
        MemberService memberService = new MemberService(databaseConnection);
        
        Optional<Member> memberOpt = memberService.findMemberById(member_id);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Member with id " + member_id + " not found");
        }
    }

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

    public void deleteLessonOrder(LessonOrder lessonOrder) {
        if (lessonOrder == null || lessonOrder.order_id() == null) {
            throw new IllegalArgumentException("Lesson order cannot be null");
        }

        try {
            databaseAccess.deleteLessonOrder(lessonOrder);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete lesson order with id = " + lessonOrder.order_id(), e);
        }
    }
}
