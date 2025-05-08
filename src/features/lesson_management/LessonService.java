/*
 * File: LessonService.java
 * 
 * Author: Matthew Song
 *         Ethan Huang
 * 
 * Description: Client-facing API for database services on the lesson table.
 * 
 */

package features.lesson_management;

import common.DatabaseConnection;
import java.util.Optional;
import java.sql.Timestamp;
import features.employee_management.*;

public class LessonService {
    private LessonDatabaseAccess lessonDatabaseAccess;
    private DatabaseConnection connection;

    public LessonService(DatabaseConnection connection) {
        lessonDatabaseAccess = new LessonDatabaseAccess(connection);
        this.connection = connection;
    }

    public Long createLesson(Long instructor_id, Timestamp lesson_time) {
        checkInstructorId(instructor_id);

        Lesson lesson = new Lesson(instructor_id, lesson_time);

        Long newId = lessonDatabaseAccess.createLesson(lesson);
        if (newId == null) {
            throw new RuntimeException("Failed to add a new lesson");
        }

        return newId;
    }

    private void checkInstructorId(Long instructor_id) {
        InstructorService instructorService = new InstructorService(connection);
        Optional<Instructor> instructorOptional = instructorService.findInstructorById(instructor_id);
        if (instructorOptional.isEmpty()) {
            throw new RuntimeException("Instructor with id " + instructor_id + " does not exist.");
        }
    }

    public Optional<Lesson> findLessonById(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Lesson id cannot be null");
        }
        return lessonDatabaseAccess.findLessonById(id);
    }

    public void updateLesson(Lesson lesson) {
        if (lesson == null || lesson.lesson_id() == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        try {
            lessonDatabaseAccess.updateLesson(lesson);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update lesson with id " + lesson.lesson_id(), e);
        }
    }

    public void deleteLesson(Lesson lesson) {
        if (lesson == null || lesson.lesson_id() == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        try {
            lessonDatabaseAccess.deleteLesson(lesson);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete lesson with id " + lesson.lesson_id(), e);
        }
    }
}