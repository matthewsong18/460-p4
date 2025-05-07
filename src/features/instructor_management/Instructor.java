package features.instructor_management;

import common.InputValidation;

public record Instructor(Long id, Long employee_id, String certification_level) {

    // Service Constructor for creating a new instructor without an ID before database insertion
    public Instructor(Long employee_id, String certification_level) {
        this(null, employee_id, certification_level);
    }

    // Canonical constructor
    public Instructor {
        validateInputs(employee_id, certification_level);
    }

    private static void validateInputs(Long employee_id, String certification_level) {
        checkEmployeeId(employee_id);
        checkCertificationLevel(certification_level);
    }

    private static void checkEmployeeId(Long employee_id) {
        if (employee_id == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
    }

    private static void checkCertificationLevel(String certification_level) {
        if (InputValidation.checkStringBlank(certification_level)) {
            throw new IllegalArgumentException("Certification level cannot be blank");
        }
    }
}
