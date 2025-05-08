package features.ski_pass;

import common.InputValidation;

import java.sql.Date;

public record SkiPass(
        Long passId,
        Long memberId,
        Date purchaseDate,
        Integer totalUses,
        Integer remainingUses,
        String passType,
        Integer price,
        Integer archivedStatus) {

    // Constructor for creating a new ski pass (passId and purchaseDate will be null 
    // since those are set in the database)
    public SkiPass(Long memberId, Integer totalUses, String passType, Integer price) {
        this(null, memberId, null, totalUses, totalUses, passType, price, 0);
    }

    // Canonical constructor with validation
    public SkiPass {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive integer.");
        }
        if (passId != null && purchaseDate == null) {   // validation for getting existing passes
            throw new IllegalArgumentException("Purchase date cannot be null for an existing pass.");
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