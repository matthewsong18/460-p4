/*
 * File: Rental.java
 * 
 * Author: Ethan Huang
 *         Stephin Tomson
 * 
 * Description: Local representation of a tuple in the rental table.
 * 
 */

package features.rental_management;

import java.sql.Date;

public record Rental(Long rental_id,
        Long pass_id,
        Long equipment_id,
        Date dateTime_From,
        Date dateTime_To) {

    public Rental {
        if (pass_id == null)
            throw new IllegalArgumentException("Pass id cannot be null");
        if (equipment_id == null)
            throw new IllegalArgumentException("Equipment id cannot be null");
        if (rental_id != null && dateTime_From == null)
            throw new IllegalArgumentException("DateTime_from cannot be null on existing rental");
    }

    /** Convenience constructor for new rentals */
    public Rental(Long pass_id, Long equipment_id) {
        this(null, pass_id, equipment_id, null, null);
    }

    /** Computed rental status: true = (datetime_to = NULL) (updated when equipment is returned) */
    public boolean isActive() {
                                        // change to local date to stay consistent across time zones
        return dateTime_To == null || dateTime_To.toLocalDate().isAfter(java.time.LocalDate.now());
    }

}
