package features.lesson_management;

import java.sql.Timestamp;

/*+----------------------------------------------------------------------
 ||
 ||  Class Lesson
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Holds lesson record fields for either an exising
 ||                  lesson or one to be added
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
 ||   Constructors:  
 ||     Lesson(instructor_id, lesson_time) -- creates a Lesson to be added
 ||                 to the database
 ||     Lesson(lesson_id, instructor_id, lesson_time) -- creates a Lesson
 ||                 that represents an existing lesson record
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long lesson_id()
 ||                  Long instructor_id()
 ||                  Timestamp lesson_time()
 ||
 ++-----------------------------------------------------------------------*/

public record Lesson(
        Long lesson_id,
        Long instructor_id,
        Timestamp lesson_time
) {
    public Lesson(Long instructor_id, Timestamp lesson_time) {
        this(null, instructor_id, lesson_time);
    }

    public Lesson {
        if (lesson_time == null)
            throw new IllegalArgumentException("Lesson time cannot be null");
    }
}