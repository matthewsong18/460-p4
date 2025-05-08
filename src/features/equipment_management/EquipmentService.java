package features.equipment_management;

import common.DatabaseConnection;
import features.rental_management.RentalService;

import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class EquipmentService
 ||
 ||      Author:  Matthew Song, Stephin Tomson
 ||
 ||      Purpose:  Service layer for equipment management. Handles creation,
 ||                retrieval, update, and archiving of equipment,
 ||                coordinating with RentalService for archiving logic.
 ||
 ||  Inherits From:  None.
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  EquipmentService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long createEquipment(String type, String dimension, String archived_status)
 ||                  Optional<Equipment> findEquipment(Long equipment_id)
 ||                  void updateEquipment(Equipment equipment)
 ||                  void archiveEquipment(Long equipmentId)
 ||
 ++-----------------------------------------------------------------------*/
public class EquipmentService {
    private final EquipmentDatabaseAccess equipmentDatabaseAccess;
    private final RentalService rentalService;

    /*---------------------------------------------------------------------
     |  Method EquipmentService (Constructor)
     |
     |  Purpose:  Initializes service with DB connection for Equipment
     |            and Rental services.
     |
     |  Pre-condition:  Valid databaseConnection.
     |
     |  Post-condition: Service ready for equipment operations.
     |
     |  Parameters:
     |      databaseConnection -- Database connection.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public EquipmentService(DatabaseConnection databaseConnection) {
        equipmentDatabaseAccess = new EquipmentDatabaseAccess(databaseConnection);
        rentalService = new RentalService(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createEquipment
     |
     |  Purpose:  Creates new equipment.
     |
     |  Pre-condition:  Valid type, dimension. Active DB connection.
     |                  The 'archived_status' String parameter is not directly
     |                  used to set the initial archived status of the Equipment
     |                  object, which defaults to 0.
     |
     |  Post-condition: Equipment created with default archived status (0),
     |                  ID returned. Throws RuntimeException if ID not returned
     |                  or on other database errors.
     |
     |  Parameters:
     |      type -- Equipment type.
     |      dimension -- Equipment dimension.
     |      archived_status -- Intended archived status (as String, currently unused
     |                         for initial object state).
     |
     |  Returns:  ID (Long) of new equipment.
     *-------------------------------------------------------------------*/
    public Long createEquipment(String type, String dimension, String archived_status) {
        Equipment equipment = new Equipment(type, dimension); // archived_status defaults to 0 here

        Long newId = equipmentDatabaseAccess.createEquipment(equipment);
        if (newId == null) {
            throw new RuntimeException("Failed to return the equipment id");
        }

        return newId;
    }

    /*---------------------------------------------------------------------
     |  Method findEquipment
     |
     |  Purpose:  Finds equipment by its ID.
     |
     |  Pre-condition:  Active DB connection. equipment_id can be null.
     |
     |  Post-condition: Returns Optional of equipment. May return empty if
     |                  equipment_id is null or not found.
     |
     |  Parameters:
     |      equipment_id -- Equipment ID to find.
     |
     |  Returns:  Optional<Equipment>.
     *-------------------------------------------------------------------*/
    public Optional<Equipment> findEquipment(Long equipment_id) {
        return equipmentDatabaseAccess.findEquipmentById(equipment_id);
    }

    /*---------------------------------------------------------------------
     |  Method updateEquipment
     |
     |  Purpose:  Updates existing equipment's details.
     |
     |  Pre-condition:  Valid equipment object with ID. Active DB connection.
     |
     |  Post-condition: Equipment updated. Throws IllegalArgumentException if
     |                  equipment or ID is null. Throws RuntimeException on
     |                  database error.
     |
     |  Parameters:
     |      equipment -- Equipment data for update.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void updateEquipment(Equipment equipment) {
        if (equipment == null || equipment.equipment_id() == null) {
            throw new IllegalArgumentException("Equipment id cannot be null");
        }

        equipmentDatabaseAccess.updateEquipment(equipment);
    }

    /*---------------------------------------------------------------------
     |  Method archiveEquipment
     |
     |  Purpose:  Archives equipment if it is not currently rented.
     |            Sets equipment's archived_status to 1.
     |
     |  Pre-condition:  Valid equipmentId. Active DB connection.
     |                  RentalService is functional.
     |
     |  Post-condition: Equipment's archived_status updated to 1. Throws
     |                  IllegalArgumentException if equipment not found.
     |                  Throws IllegalStateException if equipment is rented.
     |                  Throws RuntimeException on database error.
     |
     |  Parameters:
     |      equipmentId -- ID of equipment to archive.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void archiveEquipment(Long equipmentId) {
        Optional<Equipment> equipmentOpt = equipmentDatabaseAccess.findEquipmentById(equipmentId);

        if (equipmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Equipment with id " + equipmentId + " not found.");
        }

        boolean isRented = rentalService.findActiveRental(equipmentId).isPresent();
        if (isRented) {
            throw new IllegalStateException("Cannot archive equipment; it is currently rented.");
        }

        Equipment existing = equipmentOpt.get();
        Equipment archived = new Equipment(existing.equipment_id(), existing.type(), existing.dimension(), 1);
        equipmentDatabaseAccess.updateEquipment(archived);
    }
}