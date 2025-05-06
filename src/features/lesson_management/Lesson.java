package features.lesson_management;

import java.sql.Timestamp;

public record Lesson(
        Long lesson_id,
        Long instructor_id,
        Timestamp lesson_time
) {
    public Lesson(Long instructor_id, Timestamp lesson_time) {
        this(null, instructor_id, lesson_time);
    }

    public Lesson {
        validateInputs(instructor_id, lesson_time);
    }

    private void validateInputs(Long instructor_id, Timestamp lesson_time) {
        checkLessonTime(lesson_time);
    }

    private void checkLessonTime(Timestamp lesson_time) {
        if (lesson_time == null)
            throw new IllegalArgumentException("Lesson time cannot be null");
    }
}