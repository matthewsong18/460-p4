package features.lift_management;

import common.DatabaseConnection;

import java.util.Optional;

public class SkiLiftService {
    private final SkiLiftDatabaseAccess db;

    public SkiLiftService(DatabaseConnection dbConn) {
        this.db = new SkiLiftDatabaseAccess(dbConn.getConnection());
    }

    public Long createLift(String name, String open, String close, int diff, String loc) {
        SkiLift lift = new SkiLift(name, open, close, diff, loc);
        return db.create(lift);
    }

    public Optional<SkiLift> findLift(Long id) {
        return db.find(id);
    }

    public void updateLift(SkiLift lift) {
        if (lift.lift_id() == null) throw new IllegalArgumentException("Lift ID required for update");
        db.update(lift);
    }
}
