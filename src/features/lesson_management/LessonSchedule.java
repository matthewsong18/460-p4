package features.lesson_management;

import java.sql.Timestamp;

public record LessonSchedule(
        Long lesson_id,
        Long instructor_id,
        Timestamp lesson_time
) {
    public LessonSchedule(Long instructor_id, Timestamp lesson_time) {
        this(null, instructor_id, lesson_time);
    }

    public LessonSchedule {
        validateInputs(instructor_id, lesson_time);
    }

    private void validateInputs(Long instructor_id, Timestamp lesson_time) {
        checkInstructorId(instructor_id);
        checkLessonTime(lesson_time);
    }

    private void checkLessonId(Long lesson_id) {
        if (lesson_id == null || lesson_id <= 0)
            throw new IllegalArgumentException("Lesson ID cannot be null");
    }

    private void checkInstructorId(Long instructor_id) {
        if (instructor_id == null || instructor_id <= 0)
            throw new IllegalArgumentException("Instructor ID cannot be null");
    }

    private void checkLessonTime(Timestamp lesson_time) {
        if (lesson_time == null)
            throw new IllegalArgumentException("Lesson time cannot be null");
    }
}