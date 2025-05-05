package features.equipment_management;

import common.DatabaseConnection;

import java.util.Optional;

public class EquipmentService {
    private final EquipmentDatabaseAccess equipmentDatabaseAccess;

    public EquipmentService(DatabaseConnection databaseConnection) {
        equipmentDatabaseAccess = new EquipmentDatabaseAccess(databaseConnection.getConnection());
    }

    public Long createEquipment(String type, String dimension, String archived_status) {
        Equipment equipment = new Equipment(type, dimension);

        Long newId = equipmentDatabaseAccess.createEquipment(equipment);
        if (newId == null) {
            throw new RuntimeException("Failed to create equipment");
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
}
