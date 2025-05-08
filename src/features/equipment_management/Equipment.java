package features.equipment_management;

import common.InputValidation;

/*+----------------------------------------------------------------------
 ||
 ||  Class Equipment
 ||
 ||      Author:  Matthew Song
 ||
 ||      Purpose:  Represents equipment with ID, type, dimension, and
 ||                archived status. Includes validation for fields.
 ||
 ||  Inherits From:  java.lang.Record
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  Equipment(Long equipment_id, String type, String dimension, int archived_status) // Canonical
 ||                  Equipment(String type, String dimension) // Custom ("Service Constructor")
 ||
 ||  Class Methods:  private static void checkType(String type)
 ||                  private static void checkDimension(String dimension)
 ||                  private static void checkArchivedStatus(int archived_status)
 ||
 ||  Inst. Methods:  Long equipment_id()
 ||                  String type()
 ||                  String dimension()
 ||                  int archived_status()
 ||                  // Implicit: equals(), hashCode(), toString()
 ||
 ++-----------------------------------------------------------------------*/
public record Equipment(Long equipment_id, String type, String dimension, int archived_status) {

    /*---------------------------------------------------------------------
     |  Method Equipment (Custom Constructor)
     |
     |  Purpose:  Creates new equipment; defaults archived_status to 0.
     |            Used when equipment_id is not yet assigned (e.g., new entry).
     |
     |  Pre-condition:  Type and dimension are not blank (validated by compact constructor).
     |
     |  Post-condition: New Equipment object created with null ID, specified
     |                  type/dimension, and archived_status set to 0.
     |
     |  Parameters:
     |      type -- Equipment type (e.g., "Skis", "Snowboard").
     |      dimension -- Equipment dimension (e.g., "170cm", "Size M").
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    // Service Constructor
    public Equipment(String type, String dimension) {
        this(null, type, dimension, 0);
    }

    /*---------------------------------------------------------------------
     |  Method Equipment (Compact Constructor)
     |
     |  Purpose:  Validates fields (type, dimension, archived_status)
     |            during canonical construction of an Equipment record.
     |
     |  Pre-condition:  Record fields (type, dimension, archived_status) are
     |                  about to be initialized by the canonical constructor.
     |
     |  Post-condition: Fields are validated. If any validation fails,
     |                  an IllegalArgumentException is thrown.
     |
     |  Parameters:
     |      type -- (Implicit) The equipment type.
     |      dimension -- (Implicit) The equipment dimension.
     |      archived_status -- (Implicit) The archived status.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    // Canonical Constructor
    public Equipment {
        checkType(type);
        checkDimension(dimension);
        checkArchivedStatus(archived_status);
    }

    /*---------------------------------------------------------------------
     |  Method checkType
     |
     |  Purpose:  Validates equipment type is not blank.
     |
     |  Pre-condition:  `type` string provided.
     |
     |  Post-condition: Completes if valid, else throws IllegalArgumentException.
     |
     |  Parameters:
     |      type -- Equipment type string to validate.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private static void checkType(String type) {
        if (InputValidation.checkStringBlank(type)) {
            throw new IllegalArgumentException("Type cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkDimension
     |
     |  Purpose:  Validates equipment dimension is not blank.
     |
     |  Pre-condition:  `dimension` string provided.
     |
     |  Post-condition: Completes if valid, else throws IllegalArgumentException.
     |
     |  Parameters:
     |      dimension -- Equipment dimension string to validate.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private static void checkDimension(String dimension) {
        if (InputValidation.checkStringBlank(dimension)) {
            throw new IllegalArgumentException("Dimension cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkArchivedStatus
     |
     |  Purpose:  Validates archived_status is 0 (not archived) or 1 (archived).
     |
     |  Pre-condition:  `archived_status` integer provided.
     |
     |  Post-condition: Completes if valid (0 or 1), else throws
     |                  IllegalArgumentException.
     |
     |  Parameters:
     |      archived_status -- Integer representing archived status.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private static void checkArchivedStatus(int archived_status) {
        if (archived_status != 0 && archived_status != 1) {
            throw new IllegalArgumentException("Archived status must be either 0 or 1");
        }
    }
}