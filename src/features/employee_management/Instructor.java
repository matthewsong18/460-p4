package features.employee_management;

import common.InputValidation;

public record Instructor(Employee employee, String certification_level) {
    public Instructor {
        checkEmployee(employee);
        checkCertificationLevel(certification_level);
    }

    private void checkEmployee(Employee employee) {
        if (employee == null || employee.employee_id() == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
    }

    private void checkCertificationLevel(String certification_level) {
        if (InputValidation.checkStringBlank(certification_level)) {
            throw new IllegalArgumentException("Certification level cannot be null");
        }
    }

    public Long employee_id() {
        return employee.employee_id();
    }
}