package features.employee_management;

import java.util.Optional;
import common.DatabaseConnection;

/*+----------------------------------------------------------------------
 ||
 ||  Class InstructorService
 ||
 ||         Author:  Ethan Huang
 ||
 ||        Purpose:  Provides a method to find Instructor records
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
 ||   Constructors:  InstructorService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  Optional<Instructor> findInstructorById(Long id)
 ||
 ++-----------------------------------------------------------------------*/

public class InstructorService {
    private InstructorDatabaseAccess instructorDatabaseAccess;

    public InstructorService(DatabaseConnection databaseConnection) {
        instructorDatabaseAccess = new InstructorDatabaseAccess(databaseConnection);
    }
    
    /*---------------------------------------------------------------------
     |  Method findInstructorById(Long id)
     |
     |  Purpose:  Finds an Instructor record using its employee_id
     |
     |  Pre-condition:  id matches an Employee record on employee_id
     |
     |  Post-condition: Returned Optional contains the corresponding 
     |      Instructor or is empty
     |
     |  Parameters: Long id -- instructor_id to find
     |
     |  Returns:  Optional object with Instructor if found, otherwise empty
     *-------------------------------------------------------------------*/
    
    public Optional<Instructor> findInstructorById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Instructor id cannot be null");
        }

        try {
            return instructorDatabaseAccess.findInstructorById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find instructor with id = " + id, e);
        }
    }
}
