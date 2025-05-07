package features.equipment_management;

import common.DatabaseConnection;
import features.rental_management.RentalService;

import java.util.Optional;

public class EquipmentService {
    private final EquipmentDatabaseAccess equipmentDatabaseAccess;
    private final RentalService rentalService;

    public EquipmentService(DatabaseConnection databaseConnection) {
        equipmentDatabaseAccess = new EquipmentDatabaseAccess(databaseConnection.getConnection());
        rentalService = new RentalService(databaseConnection);
    }

    public Long createEquipment(String type, String dimension, String archived_status) {
        Equipment equipment = new Equipment(type, dimension);

        Long newId = equipmentDatabaseAccess.createEquipment(equipment);
        if (newId == null) {
            throw new RuntimeException("Failed to return the equipment id");
        }

        return newId;
    }

    public Optional<Equipment> findEquipment(Long equipment_id) {
        return equipmentDatabaseAccess.findEquipmentById(equipment_id);
    }

    public void updateEquipment(Equipment equipment) {
        if (equipment == null || equipment.equipment_id() == null) {
            throw new IllegalArgumentException("Equipment id cannot be null");
        }

        equipmentDatabaseAccess.updateEquipment(equipment);
    }

    /*
     * Archives the equipment if it is not currently rented.
     */
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
