/**
 * @author Ethan Huang
 */

package features.lesson_management;

public record LessonOrder(
    Long order_id,
    Long lesson_id,
    Long member_id,
    int total_sessions,
    int remaining_sessions
) {
    public LessonOrder(Long lesson_id, Long member_id, int total_sessions, int remaining_sessions) {
        this(null, lesson_id, member_id, total_sessions, remaining_sessions);
    }

    public LessonOrder {
        checkTotalSessions(total_sessions);
        checkRemainingSessions(remaining_sessions);
    }

    public void checkTotalSessions(int total_sessions) {
        if (total_sessions <= 0)
            throw new IllegalArgumentException("Total sessions must be positive");
    }

    public void checkRemainingSessions(int remaining_sessions) {
        if (remaining_sessions < 0)
            throw new IllegalArgumentException("Remaining sessions must be non-negative");
    }
}
