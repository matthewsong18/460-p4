package features.ski_pass;

import common.InputValidation;
import java.sql.Timestamp;

public record SkiPassData(
        Long passId,
        Long memberId,
        Timestamp purchaseDate,
        Integer totalUses,
        Integer remainingUses,
        String passType,
        Integer price,
        Integer archivedStatus
) {

    // Constructor for creating a new ski pass (passId will be null)
    public SkiPassData(Long memberId, Timestamp purchaseDate, Integer totalUses, String passType, Integer price) {
        this(null, memberId, purchaseDate, totalUses, totalUses, passType, price, 0);
    }

    // Canonical constructor with validation
    public SkiPassData {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive integer.");
        }
        if (totalUses == null || totalUses < 0) {
            throw new IllegalArgumentException("Total uses cannot be negative.");
        }
        if (remainingUses == null || remainingUses < 0 || remainingUses > totalUses) {
            throw new IllegalArgumentException("Remaining uses must be between 0 and total uses.");
        }
        if (InputValidation.checkStringBlank(passType)) {
            throw new IllegalArgumentException("Pass type cannot be blank.");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative.");
        }
        if (archivedStatus == null || (archivedStatus != 0 && archivedStatus != 1)) {
            throw new IllegalArgumentException("Archived status must be 0 (active) or 1 (archived).");
        }
    }
}