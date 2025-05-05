package features.equipment_management;

import common.DatabaseConnection;

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
}
