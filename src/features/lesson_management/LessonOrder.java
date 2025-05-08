package features.lesson_management;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonOrder
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Data carrier for LessonOrder records
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  LessonOrder(Long lesson_id, Long member_id, int total_sessions, int remaining_sessions)
 ||                  LessonOrder(Long order_id, Long lesson_id, Long member_id, int total_sessions, int remaining_sessions)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  Long order_id()
 ||                  Long lesson_id()
 ||                  Long member_id()
 ||                  int total_sessions()
 ||                  int remaining_sessions()
 ||
 ++-----------------------------------------------------------------------*/

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
        if (total_sessions <= 0)
            throw new IllegalArgumentException("Total sessions must be positive");
        if (remaining_sessions < 0)
            throw new IllegalArgumentException("Remaining sessions must be non-negative");
    }
}
