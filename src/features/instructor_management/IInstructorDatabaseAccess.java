package features.instructor_management;

import java.util.Optional;

public interface IInstructorDatabaseAccess {
    Long createInstructor(Instructor instructor);
    Optional<Instructor> findInstructorById(Long instructorId);
    void updateInstructor(Instructor instructor);
    void deleteInstructor(Instructor instructor);
    Optional<Instructor> findInstructorByEmployeeId(Long employeeId);
}
