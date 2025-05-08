package features.lesson_management;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonOrder
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  [A description of why this class exists.  For what
 ||                   reason was it written?  Which jobs does it perform?]
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  [Name all public class constants, and provide a very
 ||                   brief (but useful!) description of each.]
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  [List the names and arguments of all defined
 ||                   constructors.]
 ||
 ||  Class Methods:  [List the names, arguments, and return types of all
 ||                   public class methods.]
 ||
 ||  Inst. Methods:  
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
