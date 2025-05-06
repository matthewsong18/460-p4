package features.lesson_management;

import common.DatabaseConnection;
import java.util.Optional;
import java.sql.Timestamp;

public class LessonService {
    private LessonDatabaseAccess lessonDatabaseAccess;

    public LessonService(DatabaseConnection connection) {
        lessonDatabaseAccess = new LessonDatabaseAccess(connection.getConnection());
    }

    public Long createLesson(Long instructor_id, Timestamp lesson_time) {
        Lesson lesson = new Lesson(instructor_id, lesson_time);

        Long newId = lessonDatabaseAccess.createLesson(lesson);
        if (newId == null) {
            throw new RuntimeException("Failed to add a new lesson");
        }

        return newId;
    }

    public Optional<Lesson> findLessonById(Long id) {
        return lessonDatabaseAccess.findLessonById(id);
    }

    public void updateLesson(Lesson lesson) {
        if (lesson == null || lesson.lesson_id() == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        boolean status = lessonDatabaseAccess.updateLesson(lesson);

        if (!status) {
            throw new RuntimeException("Failed to update lesson with id " + lesson.lesson_id());
        }
    }

    public void deleteLesson(Lesson lesson) {
        if (lesson == null || lesson.lesson_id() == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        boolean status = lessonDatabaseAccess.deleteLesson(lesson);

        if (!status) {
            throw new RuntimeException("Failed to delete lesson with id " + lesson.lesson_id());
        }
    }
}