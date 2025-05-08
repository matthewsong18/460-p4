package features.rental_management;

import common.DatabaseConnection;
import features.equipment_management.Equipment;
import features.equipment_management.EquipmentService;
import features.ski_pass.SkiPass;
import features.ski_pass.SkiPassService;

import java.util.Optional;

public class RentalService {
    private final DatabaseConnection databaseConnection;
    private final RentalDatabaseAccess rentalDatabaseAccess;

    public RentalService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        rentalDatabaseAccess = new RentalDatabaseAccess(databaseConnection);
    }

    // Returns new id
    public Long registerRental(Long pass_id, Long equipment_id) {
        // Check ids
        checkSkiPass(pass_id);
        checkEquipment(equipment_id);

        Rental rental = new Rental(pass_id, equipment_id);

        try {
            return rentalDatabaseAccess.createRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating Rental", e);
        }
    }

    private void checkSkiPass(Long ski_pass_id) {
        if (ski_pass_id == null) {
            throw new IllegalArgumentException("SkiPass id cannot be null");
        }

        SkiPassService skiPassService = new SkiPassService(databaseConnection);

        Optional<SkiPass> skiPassOptional = skiPassService.findSkiPassById(ski_pass_id);
        if (skiPassOptional.isEmpty()) {
            throw new IllegalArgumentException("SkiPass id does not exist");
        }
    }

    private void checkEquipment(Long equipment_id) {
        if (equipment_id == null) {
            throw new IllegalArgumentException("Equipment id cannot be null");
        }

        EquipmentService equipmentService = new EquipmentService(databaseConnection);

        Optional<Equipment> equipmentOptional = equipmentService.findEquipment(equipment_id);
        if (equipmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Equipment with id " + equipment_id + " not found");
        }
    }

    public Optional<Rental> findRental(Long rental_id) {
        if (rental_id == null) {
            throw new IllegalArgumentException("Rental id is null");
        }

        try {
            return rentalDatabaseAccess.findRental(rental_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed while finding rental with id " + rental_id, e);
        }
    }

    public void updateRental(Rental rental) {
        if (rental == null || rental.rental_id() == null) {
            throw new IllegalArgumentException("Rental id cannot be null");
        }

        try {
            rentalDatabaseAccess.updateRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update rental with id" + rental.rental_id(), e);
        }
    }

    public void deleteRental(Rental rental) {
        if (rental == null || rental.rental_id() == null) {
            throw new IllegalArgumentException("Rental id cannot be null");
        }

        try {
            rentalDatabaseAccess.deleteRental(rental);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete rental with id" + rental.rental_id(), e);
        }
    }

    public Optional<Rental> findActiveRental(Long equipmentId) {
        return rentalDatabaseAccess.findActiveRental(equipmentId);
    }

}
