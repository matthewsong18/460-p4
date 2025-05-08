package features.rental_management;

import common.DatabaseConnection;
import features.equipment_management.Equipment;
import features.equipment_management.EquipmentService;
import features.ski_pass.SkiPass;
import features.ski_pass.SkiPassService;
import java.util.Optional;

/*+---------------------------------------------------------------------- 
 ||  Class RentalService
 ||
 ||         Author:  Matthew Song, Ethan Huang, Stephin Tomson
 ||
 ||        Purpose:  Provides a service layer between the application and 
 ||                  database access classes for the Rental entity. Ensures
 ||                  that all necessary checks and validations are performed 
 ||                  before database operations are executed.
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
 ||      - RentalService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  
 ||      - Long registerRental(Long, Long)
 ||      - Optional<Rental> findRental(Long)
 ||      - void updateRental(Rental)
 ||      - void deleteRental(Rental)
 ||      - Optional<Rental> findActiveRental(Long)
 ||
 ++-----------------------------------------------------------------------*/
public class RentalService {
    private final DatabaseConnection databaseConnection;
    private final RentalDatabaseAccess rentalDatabaseAccess;

    /*---------------------------------------------------------------------
    |  Constructor RentalService
    |
    |  Purpose:  Initializes the RentalService with a database connection and
    |           constructs the underlying RentalDatabaseAccess instance.
    |
    |  Pre-condition:  The given databaseConnection must be valid.
    |
    |  Post-condition:  Service is ready to delegate validated operations.
    |
    |  Parameters:
    |      databaseConnection -- an active database connection object.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public RentalService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        rentalDatabaseAccess = new RentalDatabaseAccess(databaseConnection);
    }

    /*---------------------------------------------------------------------
    |  Method registerRental
    |
    |  Purpose:  Validates the provided ski pass and equipment IDs and registers 
    |            a new rental in the database.
    |
    |  Pre-condition:  Both pass_id and equipment_id must be non-null and must exist 
    |                  in the database.
    |
    |  Post-condition:  A new rental record is inserted into the Rental table.
    |
    |  Parameters:
    |      pass_id      -- ID of the ski pass used for the rental.
    |      equipment_id -- ID of the equipment being rented.
    |
    |  Returns:  Long -- ID of the newly created rental record.
    *-------------------------------------------------------------------*/
    public Long registerRental(Long pass_id, Long equipment_id) {
        // Check ids
        checkSkiPass(pass_id);
        checkEquipment(equipment_id);

        Rental rental = new Rental(pass_id, equipment_id);

        try {
            return rentalDatabaseAccess.createRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating Rental", e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method checkSkiPass
    |
    |  Purpose:  Validates that the given ski pass ID is not null and exists 
    |            in the database.
    |
    |  Pre-condition:  ski_pass_id must not be null.
    |
    |  Post-condition:  Throws an exception if validation fails.
    |
    |  Parameters:
    |      ski_pass_id -- ID of the ski pass to be validated.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private void checkSkiPass(Long ski_pass_id) {
        if (ski_pass_id == null) {
            throw new IllegalArgumentException("SkiPass id cannot be null");
        }

        SkiPassService skiPassService = new SkiPassService(databaseConnection);

        Optional<SkiPass> skiPassOptional = skiPassService.findSkiPassById(ski_pass_id);
        if (skiPassOptional.isEmpty()) {
            throw new IllegalArgumentException("SkiPass id does not exist");
        }
    }

    /*---------------------------------------------------------------------
    |  Method checkEquipment
    |
    |  Purpose:  Validates that the given equipment ID is not null and exists 
    |            in the database.
    |
    |  Pre-condition:  equipment_id must not be null.
    |
    |  Post-condition:  Throws an exception if validation fails.
    |
    |  Parameters:
    |      equipment_id -- ID of the equipment to be validated.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private void checkEquipment(Long equipment_id) {
        if (equipment_id == null) {
            throw new IllegalArgumentException("Equipment id cannot be null");
        }

        EquipmentService equipmentService = new EquipmentService(databaseConnection);

        Optional<Equipment> equipmentOptional = equipmentService.findEquipment(equipment_id);
        if (equipmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Equipment with id " + equipment_id + " not found");
        }
    }

    /*---------------------------------------------------------------------
    |  Method findRental
    |
    |  Purpose:  Finds and returns a rental by its ID.
    |
    |  Pre-condition:  rental_id must not be null.
    |
    |  Post-condition:  Returns a Rental object if found, otherwise Optional.empty().
    |
    |  Parameters:
    |      rental_id -- ID of the rental to retrieve.
    |
    |  Returns:  Optional<Rental> -- The rental found, or empty if not found.
    *-------------------------------------------------------------------*/
    public Optional<Rental> findRental(Long rental_id) {
        if (rental_id == null) {
            throw new IllegalArgumentException("Rental id is null");
        }

        try {
            return rentalDatabaseAccess.findRental(rental_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed while finding rental with id " + rental_id, e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method updateRental
    |
    |  Purpose:  Updates an existing rental record.
    |
    |  Pre-condition:  The rental object must not be null and must include a valid rental_id.
    |
    |  Post-condition:  Rental record in the database is updated.
    |
    |  Parameters:
    |      rental -- Rental object containing updated rental info.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public void updateRental(Rental rental) {
        if (rental == null || rental.rental_id() == null) {
            throw new IllegalArgumentException("Rental id cannot be null");
        }

        try {
            rentalDatabaseAccess.updateRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update rental with id" + rental.rental_id(), e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method deleteRental
    |
    |  Purpose:  Deletes a rental record from the database.
    |
    |  Pre-condition:  The rental object must not be null and must include a valid rental_id.
    |
    |  Post-condition:  The corresponding rental record is deleted.
    |
    |  Parameters:
    |      rental -- Rental object to delete.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public void deleteRental(Rental rental) {
        if (rental == null || rental.rental_id() == null) {
            throw new IllegalArgumentException("Rental id cannot be null");
        }

        try {
            rentalDatabaseAccess.deleteRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete rental with id" + rental.rental_id(), e);
        }
    }

    /*---------------------------------------------------------------------
    |  Method findActiveRental
    |
    |  Purpose:  Returns the rental record for an equipment if it is currently active.
    |
    |  Pre-condition:  equipmentId must not be null.
    |
    |  Post-condition:  If an active rental is found, it is returned; otherwise empty.
    |
    |  Parameters:
    |      equipmentId -- ID of the equipment to check.
    |
    |  Returns:  Optional<Rental> -- The active rental or empty if none found.
    *-------------------------------------------------------------------*/
    public Optional<Rental> findActiveRental(Long equipmentId) {
        return rentalDatabaseAccess.findActiveRental(equipmentId);
    }

}
