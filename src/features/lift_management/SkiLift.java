package features.lift_management;

import common.InputValidation;

public record SkiLift(Long lift_id, String lift_name, String open_time, String close_time, int difficulty_level,
        String location) {

    public SkiLift(String lift_name, String open_time, String close_time, int difficulty_level, String location) {
        this(null, lift_name, open_time, close_time, difficulty_level, location);
    }

    public SkiLift {
        checkLiftName(lift_name);
        checkTimeFormat(open_time);
        checkTimeFormat(close_time);
        checkDifficulty(difficulty_level);
        checkLocation(location);
    }

    private static void checkLiftName(String name) {
        if (InputValidation.checkStringBlank(name)) {
            throw new IllegalArgumentException("Lift name cannot be blank");
        }
    }

    private static void checkTimeFormat(String time) {
        if (!time.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Time must be in HH:MM format");
        }
    }

    private static void checkDifficulty(int level) {
        if (level < 0 || level > 3) {
            throw new IllegalArgumentException("Difficulty must be between 0 and 3");
        }
    }

    private static void checkLocation(String location) {
        if (InputValidation.checkStringBlank(location)) {
            throw new IllegalArgumentException("Location cannot be blank");
        }
    }
}
