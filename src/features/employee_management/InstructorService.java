/**
 * @author Ethan Huang
 */

package features.employee_management;

import java.util.Optional;

import common.DatabaseConnection;

public class InstructorService {
    private InstructorDatabaseAccess instructorDatabaseAccess;

    public InstructorService(DatabaseConnection databaseConnection) {
        instructorDatabaseAccess = new InstructorDatabaseAccess(databaseConnection);
    }
    
    public Optional<Instructor> findInstructorById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Instructor id cannot be null");
        }
        return instructorDatabaseAccess.findInstructorById(id);
    }
}
