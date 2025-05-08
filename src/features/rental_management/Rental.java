package features.rental_management;

import java.sql.Date;

public record Rental(Long rental_id,
        Long pass_id,
        Long equipment_id,
        Date dateTime_From,
        Date dateTime_To) {

    /** Canonical constructor — preserves all values */
    public Rental(Long rental_id, Long pass_id, Long equipment_id, Date dateTime_From, Date dateTime_To) {
        this.rental_id = rental_id;
        this.pass_id = pass_id;
        this.equipment_id = equipment_id;
        this.dateTime_From = dateTime_From;
        this.dateTime_To = dateTime_To;
    }

    /** Convenience constructor for new rentals */
    public Rental(Long pass_id, Long equipment_id) {
        this(null, pass_id, equipment_id, null, null);
    }

    /** Computed rental status: 1 = active, 0 = returned */
    public int return_status() {
        if (dateTime_To == null)
            return 1;
        return dateTime_To.toLocalDate().isAfter(java.time.LocalDate.now()) ? 1 : 0;
    }

    public boolean isActive() {
        return return_status() == 0;
    }
}
