package features.employee_management;

import java.util.Optional;
import common.InputValidation;

public record Instructor(Employee employee, String certification_level) {
    public Instructor {
        checkEmployee(employee);
    }

    private void checkEmployee(Employee employee) {
        if (employee == null || employee.employee_id() == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
    }

    private void checkCertificationLevel(String certification_level) {
        InputValidation.checkStringBlank(certification_level);
    }
}