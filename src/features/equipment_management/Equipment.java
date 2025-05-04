package features.equipment_management;

import common.InputValidation;

public record Equipment(Long equipment_id, String type, String dimension, String archived_status) {

    // Service Constructor
    public Equipment(String type, String dimension, String archived_status) {
        this(null, type, dimension, archived_status);
    }

    // Canonical Constructor
    public Equipment {
        checkType(type);
        checkDimension(dimension);
        checkArchivedStatus(archived_status);
    }

    private static void checkType(String type) {
        if (InputValidation.checkBlank(type)) {
            throw new IllegalArgumentException("Type cannot be blank");
        }
    }

    private static void checkDimension(String dimension) {
        if (InputValidation.checkBlank(dimension)) {
            throw new IllegalArgumentException("Dimension cannot be blank");
        }
    }

    private static void checkArchivedStatus(String archivedStatus) {
        if (InputValidation.checkBlank(archivedStatus)) {
            throw new IllegalArgumentException("ArchivedStatus cannot be blank");
        }
    }
}
