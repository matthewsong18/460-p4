package features.rental_management;

import java.sql.Date;

/*+---------------------------------------------------------------------- 
 ||  Class Rental
 ||
 ||         Author:  Ethan Huang, Stephin Tomson
 ||
 ||        Purpose:  Models a single rental record from the Rental table
 ||                  in the Ski Resort database. This immutable record
 ||                  simplifies the tracking and manipulation of rental data
 ||                  within the application logic.
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  
 ||      - Rental(Long rental_id, Long pass_id, Long equipment_id, Date dateTime_From, Date dateTime_To)
 ||          Full constructor that validates required fields.
 ||      - Rental(Long pass_id, Long equipment_id)
 ||          Convenience constructor for creating a new rental.
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  
 ||      - boolean isActive(): returns true if rental is still active (i.e., not returned).
 ||
 ++-----------------------------------------------------------------------*/
public record Rental(Long rental_id,
        Long pass_id,
        Long equipment_id,
        Date dateTime_From,
        Date dateTime_To) {
    /*--------------------------------------------------------------------- 
    |  Constructor Rental 
    |
    |  Purpose:  Initializes a Rental object with all attributes, performing
    |            basic validation on pass_id, equipment_id, and dateTime_From.
    |
    |  Pre-condition:  
    |      - pass_id and equipment_id must not be null.
    |      - If rental_id is non-null, dateTime_From must also be non-null.
    |
    |  Post-condition:  
    |      - A new immutable Rental object is created.
    |
    |  Parameters:
    |      rental_id       -- Unique ID of the rental (nullable for new entries).
    |      pass_id         -- Associated ski pass ID (non-null).
    |      equipment_id    -- Associated equipment ID (non-null).
    |      dateTime_From   -- Rental start timestamp.
    |      dateTime_To     -- Rental end timestamp.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public Rental {
        if (pass_id == null)
            throw new IllegalArgumentException("Pass id cannot be null");
        if (equipment_id == null)
            throw new IllegalArgumentException("Equipment id cannot be null");
        if (rental_id != null && dateTime_From == null)
            throw new IllegalArgumentException("DateTime_from cannot be null on existing rental");
    }

    /*--------------------------------------------------------------------- 
    |  Constructor Rental (convenience)
    |
    |  Purpose:  Constructs a new Rental object for inserting a rental 
    |            without an ID or timestamp (to be handled by the DB).
    |
    |  Pre-condition:  pass_id and equipment_id must not be null.
    |
    |  Post-condition:  
    |      - A new Rental object with null rental_id, dateTime_From, and dateTime_To
    |        is returned.
    |
    |  Parameters:
    |      pass_id      -- Associated ski pass ID.
    |      equipment_id -- Associated equipment ID.
    |
    |  Returns:  A Rental record instance ready for insertion.
    *-------------------------------------------------------------------*/
    public Rental(Long pass_id, Long equipment_id) {
        this(null, pass_id, equipment_id, null, null);
    }

    /*--------------------------------------------------------------------- 
    |  Method isActive
    |
    |  Purpose:  Determines whether the rental is still active. A rental is 
    |            considered active if its return date (`dateTime_To`) is null
    |            or is a future date.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition:  
    |      - Returns true if the rental is currently active, false otherwise.
    |
    |  Parameters:  None.
    |
    |  Returns:  boolean -- True if rental is ongoing; false if returned.
    *-------------------------------------------------------------------*/
    public boolean isActive() {
        // change to local date to stay consistent across time zones
        return dateTime_To == null || dateTime_To.toLocalDate().isAfter(java.time.LocalDate.now());
    }

}
