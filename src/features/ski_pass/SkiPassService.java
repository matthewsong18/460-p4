package features.ski_pass;

import common.DatabaseConnection;
import features.member_management.MemberService;

import java.sql.SQLException;
import java.sql.Date;
import java.util.Optional;

public class SkiPassService {
    private final SkiPassDatabaseAccess skiPassDatabaseAccess;
    private final DatabaseConnection dbConnection;

    public SkiPassService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.skiPassDatabaseAccess = new SkiPassDatabaseAccess(dbConnection);
    }

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

    public Optional<SkiPass> findSkiPassById(long passId) {
        try {
            return skiPassDatabaseAccess.findSkiPassById(passId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve ski pass details for ID " + passId + ": " + e.getMessage(),
                    e);
        }
    }

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

    public void updatePassDetails(SkiPass skiPassData) {
        try {
            skiPassDatabaseAccess.updateSkiPass(skiPassData);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update ski pass ID " + skiPassData.passId() + ": " + e.getMessage(),
                    e);
        }
    }

    public void deletePass(long passId) {
        try {
            skiPassDatabaseAccess.deleteSkiPass(passId);
            System.out.println("Ski pass " + passId + " deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ski pass ID " + passId + ": " + e.getMessage(), e);
        }
    }
}