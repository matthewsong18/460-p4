package features.employee_management;

import common.InputValidation;

import java.sql.Timestamp;

public record Employee(
        Long employee_id,
        String name,
        String age,
        String gender,
        String position,
        int salary,
        Timestamp start_date,
        Long property_id
) {
    public Employee(String name, String age, String gender, String position, int salary, Long property_id) {
        this(null, name, age, gender, position, salary, null, property_id);
    }
    public Employee {
        checkEmployeeName(name);
        checkEmployeeAge(age);
        checkEmployeeGender(gender);
        checkEmployeePosition(position);
        checkEmployeeSalary(salary);
    }

    private void checkEmployeeName(String name) {
        if (InputValidation.checkStringBlank(name)) {
            throw new IllegalArgumentException("Employee name cannot be blank");
        }
    }

    private void checkEmployeeAge(String age) {
        if (InputValidation.checkStringBlank(age)) {
            throw new IllegalArgumentException("Employee age cannot be blank");
        }
    }

    private void checkEmployeeGender(String gender) {
        if (InputValidation.checkStringBlank(gender)) {
            throw new IllegalArgumentException("Employee gender cannot be blank");
        }
    }

    private void checkEmployeePosition(String position) {
        if (InputValidation.checkStringBlank(position)) {
            throw new IllegalArgumentException("Employee position cannot be blank");
        }
    }

    private void checkEmployeeSalary(int salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Employee salary cannot be negative");
        }
    }
}
