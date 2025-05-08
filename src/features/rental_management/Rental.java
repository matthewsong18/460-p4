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
