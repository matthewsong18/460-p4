/*
 * File: SkiPassService.java
 * 
 * Author: Matthew Song
 *         Ethan Huang
 * 
 * Description: Client-facing API responsible for database services on Ski Pass table.
 * 
 */

package features.ski_pass;

import common.DatabaseConnection;
import features.member_management.MemberService;

import java.sql.SQLException;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class SkiPassService
 ||
 ||         Author:  Ethan Huang
 ||                  Matthew Song
 ||
 ||        Purpose:  This class exposes the public interface for managing ski pass 
 ||                  records in the database, such as issuing new passes, validating 
 ||                  use, checking expiration, and deleting records.
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  skiPassDatabaseAccess -- provides access to ski pass
 ||                      database
 ||                  dbConnection -- valid SQL connection
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  SkiPassService(DatabaseConnection dbConnection)
 ||
 ||  Class Methods:  [List the names, arguments, and return types of all
 ||                   public class methods.]
 ||
 ||  Inst. Methods:  [List the names, arguments, and return types of all
 ||                   public instance methods.]
 ||
 ++-----------------------------------------------------------------------*/

public class SkiPassService {
    private final SkiPassDatabaseAccess skiPassDatabaseAccess;
    private final DatabaseConnection dbConnection;

    public SkiPassService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.skiPassDatabaseAccess = new SkiPassDatabaseAccess(dbConnection);
    }

    /*---------------------------------------------------------------------
    |  Method createSkiPass(Long memberId, Integer totalUses, String passType, Integer price)
    |
    |  Purpose:  Creates a new SkiPass for an existing member with usage limits, type, and price.
    |
    |  Pre-condition:  memberId must refer to an existing member. totalUses and 
    |                  price must be non-null.
    |
    |  Post-condition: A new ski pass is inserted into the database and its ID is 
    |                  returned.
    |
    |  Parameters:
    |      memberId -- ID of the member the ski pass is issued to
    |      totalUses -- number of times the pass can be used
    |      passType -- string identifier for the pass type ("1-day", "2-day", "4-day", or "season")
    |      price -- purchase price
    |
    |  Returns:  the ID of the newly created ski pass
    *-------------------------------------------------------------------*/
    public Long createSkiPass(Long memberId, Integer totalUses, String passType, Integer price) {
        MemberService memberService = new MemberService(dbConnection);
        if (memberService.findMemberById(memberId).isEmpty()) {
            throw new IllegalArgumentException("Member with ID " + memberId + " not found.");
        }

        SkiPass newSkiPass = new SkiPass(memberId, totalUses, passType, price);
        try {
            return skiPassDatabaseAccess.createSkiPass(newSkiPass);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to issue ski pass: " + e.getMessage(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method findSkiPassById(long passId)
     |
     |  Purpose:  Retrieves a SkiPass from the database by its unique ID.
     |
     |  Pre-condition:  The passId must be a valid identifier.
     |
     |  Post-condition: Returns a populated Optional<SkiPass> if found.
     |
     |  Parameters:
     |      passId -- unique identifier for the ski pass
     |
     |  Returns:  Optional<SkiPass> -- the matching ski pass, if present, else empty
     *-------------------------------------------------------------------*/

    public Optional<SkiPass> findSkiPassById(long passId) {
        try {
            return skiPassDatabaseAccess.findSkiPassById(passId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve ski pass details for ID " + passId + ": " + e.getMessage(),
                    e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method useSkiPass(long passId)
     |
     |  Purpose:  Decrements remaining uses of a ski pass by one. 
     |            Automatically archives the pass if expired or depleted.
     |
     |  Pre-condition:  passId must correspond to a valid, non-archived ski pass
     |                  with at least one remaining use.
     |
     |  Post-condition: The ski pass is updated in the database with one fewer use.
     |                  If it has zero uses left or is expired, it is archived.
     |
     |  Parameters:
     |      passId -- the unique ID of the ski pass to be used
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public void useSkiPass(long passId) {
        try {
            Optional<SkiPass> passOpt = skiPassDatabaseAccess.findSkiPassById(passId);
            if (passOpt.isEmpty()) {
                throw new IllegalArgumentException("Ski pass with ID " + passId + " not found.");
            }

            SkiPass pass = passOpt.get();
            if (pass.archivedStatus() == 1) {
                throw new IllegalArgumentException("Ski pass " + passId + " is archived.");
            }
            if (isPassExpired(pass)) {
                archivePass(passId, true); // Force archive if expired and used
                throw new IllegalArgumentException("Ski pass " + passId + " is expired.");
            }
            if (pass.remainingUses() <= 0) {
                archivePass(passId, true); // Archive if no uses left
                throw new IllegalArgumentException("Ski pass " + passId + " has no remaining uses.");
            }

            SkiPass updatedPass = new SkiPass(
                    pass.passId(),
                    pass.memberId(),
                    pass.purchaseDate(),
                    pass.totalUses(),
                    pass.remainingUses() - 1,
                    pass.passType(),
                    pass.price(),
                    (pass.remainingUses() - 1 == 0) ? 1 : 0 // Archive if this is the last use
            );
            skiPassDatabaseAccess.updateSkiPass(updatedPass);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to use ski pass ID " + passId + ": " + e.getMessage(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method archivePass(long passId, boolean forceArchive)
     |
     |  Purpose:  Manually archives a ski pass, optionally bypassing checks.
     |
     |  Pre-condition:  passId must exist in the database.
     |
     |  Post-condition: The ski pass is marked as archived in the database.
     |
     |  Parameters:
     |      passId -- the ID of the pass to be archived
     |      forceArchive -- if true, force archives the pass
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public void archivePass(long passId, boolean forceArchive) {
        try {
            Optional<SkiPass> skiPassDataOptional = skiPassDatabaseAccess.findSkiPassById(passId);
            if (skiPassDataOptional.isEmpty()) {
                throw new IllegalArgumentException("Ski pass " + passId + " not found.");
            }
            SkiPass skiPassData = skiPassDataOptional.get();
            if (skiPassData.archivedStatus() == 1 && !forceArchive) {
                throw new IllegalArgumentException("Ski pass " + passId + " is already archived.");
            }
            SkiPass updatedPass = new SkiPass(
                    skiPassData.passId(),
                    skiPassData.memberId(),
                    skiPassData.purchaseDate(),
                    skiPassData.totalUses(),
                    skiPassData.remainingUses(),
                    skiPassData.passType(),
                    skiPassData.price(),
                    1 // Set archived status to 1
            );
            skiPassDatabaseAccess.updateSkiPass(updatedPass);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to archive ski pass ID " + passId + ": " + e.getMessage(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method isPassExpired(SkiPass pass)
     |
     |  Purpose:  Checks if a ski pass has expired based on its type and
     |            number of days since purchase.
     |
     |  Pre-condition:  passId must be valid and refer to an existing ski pass.
     |
     |  Post-condition: Returns true if the pass is expired, false otherwise.
     |
     |  Parameters:
     |      passId -- ID of the ski pass to check
     |
     |  Returns:  boolean -- whether the pass is expired
     *-------------------------------------------------------------------*/

    private boolean isPassExpired(SkiPass pass) throws SQLException {
        if (pass.purchaseDate() == null) {
            System.err.println("Pass " + pass.passId() + " has no purchase date, cannot determine expiration.");
            return false;
        }

        int daysSincePurchase = skiPassDatabaseAccess.getDaysSincePurchase(pass.passId());
        return switch (pass.passType().toLowerCase()) {
            case "1-day" -> daysSincePurchase >= 1;
            case "2-day" -> daysSincePurchase >= 2;
            case "4-day" -> daysSincePurchase >= 4;
            case "season" -> daysSincePurchase >= 180;
            default -> throw new RuntimeException("Unknown pass type for expiration check: " + pass.passType());
        };
    }

    /*---------------------------------------------------------------------
     |  Method addUsesToPass(long passId, int usesToAdd)
     |
     |  Purpose:  Increases the remaining uses on an active ski pass.
     |
     |  Pre-condition:  passId must point to an active, non-archived pass. usesToAdd 
     |                  must be greater than zero.
     |
     |  Post-condition: Pass is updated with additional uses, capped at totalUses.
     |
     |  Parameters:
     |      passId -- ID of the ski pass
     |      usesToAdd -- number of uses to add
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public void addUsesToPass(long passId, int usesToAdd) {
        if (usesToAdd <= 0) {
            throw new IllegalArgumentException("Uses to add must be positive.");
        }
        try {
            Optional<SkiPass> skiPassDataOptional = skiPassDatabaseAccess.findSkiPassById(passId);
            if (skiPassDataOptional.isEmpty()) {
                throw new IllegalArgumentException("Ski pass " + passId + " not found.");
            }
            SkiPass pass = skiPassDataOptional.get();
            if (pass.archivedStatus() == 1) {
                throw new IllegalArgumentException("Cannot add uses to an archived pass: " + passId);
            }
            if (pass.remainingUses() + usesToAdd > pass.totalUses()) {
                usesToAdd = pass.totalUses() - pass.remainingUses(); // Cap remaining uses to total uses
                System.out.println("Capping uses to " + pass.remainingUses() + ", to avoid exceeding total uses.");
            }

            SkiPass updatedPass = new SkiPass(
                    pass.passId(),
                    pass.memberId(),
                    pass.purchaseDate(),
                    pass.totalUses(),
                    pass.remainingUses() + usesToAdd,
                    pass.passType(),
                    pass.price(),
                    pass.archivedStatus());
            skiPassDatabaseAccess.updateSkiPass(updatedPass);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add uses to ski pass ID " + passId + ": " + e.getMessage(), e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method isPassExpired(long passId)
     |
     |  Purpose:  Checks if a ski pass has expired.
     |
     |  Pre-condition:  passId refers to an existing ski pass.
     |
     |  Post-condition: Returns true if the pass is expired, false otherwise.
     |
     |  Parameters:
     |      passId -- ID of the ski pass to check
     |
     |  Returns:  true if expired, else false
     *-------------------------------------------------------------------*/

    public boolean isPassExpired(long passId) {
        try {
            Optional<SkiPass> passOpt = skiPassDatabaseAccess.findSkiPassById(passId);
            if (passOpt.isEmpty()) {
                throw new IllegalArgumentException("Ski pass " + passId + " not found.");
            }
            return isPassExpired(passOpt.get());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check expiration for ski pass ID " + passId + ": " + e.getMessage(),
                    e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method updatePassDetails(SkiPass skiPassData)
     |
     |  Purpose:  Updates the fields of a SkiPass tuple.
     |
     |  Pre-condition:  skiPassData must be a valid and existing ski pass object.
     |
     |  Post-condition: The ski pass record is updated with new values.
     |
     |  Parameters:
     |      skiPassData -- the updated SkiPass object
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public void updatePassDetails(SkiPass skiPassData) {
        try {
            skiPassDatabaseAccess.updateSkiPass(skiPassData);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update ski pass ID " + skiPassData.passId() + ": " + e.getMessage(),
                    e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deletePass
     |
     |  Purpose:  Deletes a ski pass record from the database.
     |
     |  Pre-condition:  passId refers to an existing skiPass
     |
     |  Post-condition: The pass tuple is removed from the database.
     |
     |  Parameters:
     |      passId -- ID of the ski pass to delete
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/

    public void deletePass(long passId) {
        try {
            skiPassDatabaseAccess.deleteSkiPass(passId);
            System.out.println("Ski pass " + passId + " deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ski pass ID " + passId + ": " + e.getMessage(), e);
        }
    }
}