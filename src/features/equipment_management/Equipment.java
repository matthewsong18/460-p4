package features.equipment_management;

import common.InputValidation;

public record Equipment(Long equipment_id, String type, String dimension, int archived_status) {

    // Service Constructor
    public Equipment(String type, String dimension) {
        this(null, type, dimension, 0);
    }

    // Canonical Constructor
    public Equipment {
        checkType(type);
        checkDimension(dimension);
        checkArchivedStatus(archived_status);
    }

    private static void checkType(String type) {
        if (InputValidation.checkStringBlank(type)) {
            throw new IllegalArgumentException("Type cannot be blank");
        }
    }

    private static void checkDimension(String dimension) {
        if (InputValidation.checkStringBlank(dimension)) {
            throw new IllegalArgumentException("Dimension cannot be blank");
        }
    }

    private static void checkArchivedStatus(int archived_status) {
        if (archived_status != 0 && archived_status != 1) {
            throw new IllegalArgumentException("Archived status must be either 0 or 1");
        }
    }
}
