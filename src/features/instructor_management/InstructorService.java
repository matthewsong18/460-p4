package features.instructor_management;

import java.util.Optional;

public class InstructorService {
    private final IInstructorDatabaseAccess instructorDatabaseAccess;

    public InstructorService(IInstructorDatabaseAccess instructorDatabaseAccess) {
        this.instructorDatabaseAccess = instructorDatabaseAccess;
    }

    public long registerInstructor(Long employeeId, String certificationLevel) {
        Instructor newInstructor = new Instructor(employeeId, certificationLevel);
        checkEmployeeIdDuplicate(employeeId);
        return instructorDatabaseAccess.createInstructor(newInstructor);
    }

    private void checkEmployeeIdDuplicate(Long employeeId) {
        if (instructorDatabaseAccess.findInstructorByEmployeeId(employeeId).isPresent()) {
            throw new IllegalArgumentException("Instructor with employee ID " + employeeId + " already exists.");
        }
    }

    public Optional<Instructor> findInstructorById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Instructor ID cannot be null.");
        }
        try {
            return instructorDatabaseAccess.findInstructorById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find instructor with ID " + id, e);
        }
    }


    public Optional<Instructor> findInstructorByEmployeeId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Instructor ID cannot be null.");
        }
        try {
            return instructorDatabaseAccess.findInstructorByEmployeeId(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find instructor with ID " + id, e);
        }
    }

    public void updateInstructor(Instructor instructor) {
        if (instructor == null || instructor.id() == null) {
            throw new IllegalArgumentException("Instructor or Instructor ID cannot be null for update.");
        }

        instructorDatabaseAccess.findInstructorById(instructor.id())
                .orElseThrow(
                        () -> new IllegalArgumentException("Instructor with ID " + instructor.id() + " not found."));

        Optional<Instructor> existingInstructorWithSameEmployeeId = instructorDatabaseAccess.findInstructorByEmployeeId(
                instructor.employee_id());
        if (existingInstructorWithSameEmployeeId.isPresent() && !existingInstructorWithSameEmployeeId.get().id()
                .equals(instructor.id())) {
            throw new IllegalArgumentException(
                    "Another instructor with employee ID " + instructor.employee_id() + " already exists.");
        }

        try {
            instructorDatabaseAccess.updateInstructor(instructor);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update instructor with ID " + instructor.id(), e);
        }
    }

    public void deleteInstructor(Instructor instructor) {
        if (instructor == null || instructor.id() == null) {
            throw new IllegalArgumentException("Instructor or Instructor ID cannot be null for deletion.");
        }
        instructorDatabaseAccess.findInstructorById(instructor.id())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Instructor with ID " + instructor.id() + " not found. Cannot delete."));
        try {
            instructorDatabaseAccess.deleteInstructor(instructor);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete instructor with ID " + instructor.id(), e);
        }
    }
}
