package features.rental_management;

import java.sql.Timestamp;

public record Rental(Long rental_id, Long ski_pass_id, Long equipment_id, Timestamp dateTimeFrom, int return_status) {
    public Rental(Long ski_pass_id, Long equipment_id, int return_status) {
        this(null, ski_pass_id, equipment_id, null, return_status);
    }

    public Rental {
        checkReturnStatus(return_status);
    }

    private void checkReturnStatus(int return_status) {
        if (return_status != 0 && return_status != 1) {
            throw new IllegalArgumentException("Invalid return status: " + return_status);
        }
    }
}