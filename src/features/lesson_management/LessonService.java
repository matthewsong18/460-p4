package features.lesson_management;

import common.DatabaseConnection;
import java.util.Optional;
import java.sql.Timestamp;
import features.employee_management.*;

/*+----------------------------------------------------------------------
 ||
 ||  Class LessonService
 ||
 ||         Author:  Matthew Song
 ||                  Ethan Huang
 ||
 ||        Purpose:  Client-facing API for database services on the lesson table.
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None 
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  LessonService(DatabaseConnection connection)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  
 ||     Long createLesson(Long instructor_id, Timestamp lesson_time)
 ||     Optional<Lesson> findLessonById(Long id)
 ||     void updateLesson(Lesson lesson)
 ||     void deleteLesson(Lesson lesson)
 ||
 ++-----------------------------------------------------------------------*/



public class LessonService {
    private LessonDatabaseAccess lessonDatabaseAccess;
    private DatabaseConnection connection;

    public LessonService(DatabaseConnection connection) {
        lessonDatabaseAccess = new LessonDatabaseAccess(connection);
        this.connection = connection;
    }

    /*---------------------------------------------------------------------
     |  Method createLesson(Long instructor_id, Timestamp lesson_time)
     |
     |  Purpose:  Creates a lesson in the database with the given 
     |      parameters.
     |
     |  Pre-condition:  instructor_id matches instructor_id field of an
     |      existing Instructor, lesson_time is a valid time.
     |
     |  Post-condition: Lesson has been inserted into the Lesson relation.
     |
     |  Parameters: 
     |      Long instructor_id -- instructor_id of the Lesson's Instructor
     |      Timestamp lesson_time -- time of lesson
     |
     |  Returns:  Primary key (lesson_id) of new Lesson record
     *-------------------------------------------------------------------*/
    
    public Long createLesson(Long instructor_id, Timestamp lesson_time) {
        checkInstructorId(instructor_id);

        Lesson lesson = new Lesson(instructor_id, lesson_time);

        Long newId = lessonDatabaseAccess.createLesson(lesson);
        if (newId == null) {
            throw new RuntimeException("Failed to add a new lesson");
        }

        return newId;
    }

    /*---------------------------------------------------------------------
     |  Method checkInstructorId(Long instructor_id)
     |
     |  Purpose:  Checks if instructor_id matches an existing Instructor
     |      in the database.
     |
     |  Pre-condition:  Nothing that is not obvious.
     |
     |  Post-condition: An error is thrown if there is no matching
     |      Instructor.
     |
     |  Parameters: 
     |      Long instructor_id -- instructor_id to match in the Instructor
     |          relation
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
    private void checkInstructorId(Long instructor_id) {
        InstructorService instructorService = new InstructorService(connection);
        Optional<Instructor> instructorOptional = instructorService.findInstructorById(instructor_id);
        if (instructorOptional.isEmpty()) {
            throw new RuntimeException("Instructor with id " + instructor_id + " does not exist.");
        }
    }

    /*---------------------------------------------------------------------
     |  Method findLessonById(Long id)
     |
     |  Purpose:  Finds a Lesson matching lesson_id = id parameter
     |
     |  Pre-condition:  id is not null and id > 0.
     |
     |  Post-condition: Returned Optional object contains the matching 
     |      Lesson or is empty.
     |
     |  Parameters: 
     |      Long id -- lesson_id to match in Lesson relation
     |
     |  Returns:  Optional object with matching Lesson or empty if not 
     |      found.
     *-------------------------------------------------------------------*/
    
    public Optional<Lesson> findLessonById(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Lesson id cannot be null");
        }
        return lessonDatabaseAccess.findLessonById(id);
    }

    /*---------------------------------------------------------------------
     |  Method updateLesson(Lesson lesson)
     |
     |  Purpose:  Updates a Lesson record in the database with data passed 
     |      in by lesson parameter.
     |
     |  Pre-condition:  lesson matches a Lesson record on lesson_id
     |
     |  Post-condition: Corresponding lesson record in database contains 
     |      the new attributes
     |
     |  Parameters:
     |      Lesson lesson -- Data carrier with new attributes
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
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

    /*---------------------------------------------------------------------
     |  Method deleteLesson(Lesson lesson)
     |
     |  Purpose:  Deletes a lesson from the database.
     |
     |  Pre-condition:  lesson matches a Lesson record on lesson_id
     |
     |  Post-condition: Corresponding lesson record in database is removed.
     |
     |  Parameters:
     |      Lesson lesson -- Data carrier with lesson_id to find
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    
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