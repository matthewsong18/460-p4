package features.employee_management;

import common.InputValidation;

import java.sql.Timestamp;

/*+----------------------------------------------------------------------
 ||
 ||  Class Employee
 ||
 ||      Author:  Matthew Song
 ||
 ||      Purpose:  Represents an employee entity with attributes such as ID,
 ||                name, age, gender, position, salary, start date, and
 ||                associated property ID. This record class provides a
 ||                concise data structure for employee information and
 ||                includes validation for name, age, gender, position,
 ||                and salary during instantiation.
 ||
 ||  Inherits From:  java.lang.Record
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  Employee(Long employee_id, String name, String age, String gender, String position, int salary, Timestamp start_date, Long property_id) (Canonical constructor, validation logic in compact constructor)
 ||                  Employee(String name, String age, String gender, String position, int salary, Long property_id) (Custom constructor delegating to canonical)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long employee_id() // Accessor for employee_id
 ||                  String name()       // Accessor for name
 ||                  String age()        // Accessor for age
 ||                  String gender()     // Accessor for gender
 ||                  String position()   // Accessor for position
 ||                  int salary()        // Accessor for salary
 ||                  Timestamp start_date() // Accessor for start_date
 ||                  Long property_id()  // Accessor for property_id
 ||                  private void checkEmployeeName(String name)
 ||                  private void checkEmployeeAge(String age)
 ||                  private void checkEmployeeGender(String gender)
 ||                  private void checkEmployeePosition(String position)
 ||                  private void checkEmployeeSalary(int salary)
 ||                  // Standard record methods: equals(Object o), hashCode(), toString() are also implicitly provided.
 ||
 ++-----------------------------------------------------------------------*/
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
    /*---------------------------------------------------------------------
     |  Method Employee (Custom Constructor)
     |
     |  Purpose:  Constructs a new Employee object with a null employee_id
     |            and null start_date. This is typically used when creating
     |            a new employee record that has not yet been assigned an ID
     |            or a formal start date by the system/database. It delegates
     |            to the canonical constructor for actual object creation
     |            and subsequent validation via the compact constructor.
     |
     |  Pre-condition:  Name, age, gender, and position are not blank. Salary
     |                  is not negative. These are enforced by the compact
     |                  constructor invoked by the canonical constructor.
     |
     |  Post-condition: A new Employee object is created with the specified
     |                  details, a null employee_id, and a null start_date.
     |                  Validation rules defined in the compact constructor
     |                  will have been applied to other fields.
     |
     |  Parameters:
     |      name -- The name of the employee.
     |      age -- The age of the employee (as a String).
     |      gender -- The gender of the employee.
     |      position -- The job position of the employee.
     |      salary -- The salary of the employee.
     |      property_id -- The ID of the property associated with the employee.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public Employee(String name, String age, String gender, String position, int salary, Long property_id) {
        this(null, name, age, gender, position, salary, null, property_id);
    }

    /*---------------------------------------------------------------------
     |  Method Employee (Compact Constructor)
     |
     |  Purpose:  This compact constructor is automatically invoked by the
     |            canonical constructor before its own body executes. Its
     |            purpose here is to validate the core employee attributes
     |            (name, age, gender, position, salary) using private
     |            helper methods.
     |
     |  Pre-condition:  The parameters for name, age, gender, position, and
     |                  salary are about to be assigned to the record's fields
     |                  by the canonical constructor.
     |
     |  Post-condition: The name, age, gender, position, and salary fields
     |                  are validated. If any validation fails, an
     |                  IllegalArgumentException is thrown, preventing the
     |                  object's creation. If all validations pass, the
     |                  fields of the record will be initialized.
     |
     |  Parameters:
     |      name -- (Implicit) The employee name passed to the canonical constructor.
     |      age -- (Implicit) The employee age string passed to the canonical constructor.
     |      gender -- (Implicit) The employee gender passed to the canonical constructor.
     |      position -- (Implicit) The employee position passed to the canonical constructor.
     |      salary -- (Implicit) The employee salary passed to the canonical constructor.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public Employee {
        checkEmployeeName(name);
        checkEmployeeAge(age);
        checkEmployeeGender(gender);
        checkEmployeePosition(position);
        checkEmployeeSalary(salary);
    }

    /*---------------------------------------------------------------------
     |  Method checkEmployeeName
     |
     |  Purpose:  Validates the employee's name. It ensures that the name
     |            is not null or blank (empty or whitespace only). This
     |            supports data integrity for the 'name' field.
     |
     |  Pre-condition:  The 'name' string is provided.
     |
     |  Post-condition: If the 'name' is blank, an IllegalArgumentException
     |                  is thrown. Otherwise, the method completes normally.
     |
     |  Parameters:
     |      name -- The string representing the employee's name to be validated.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkEmployeeName(String name) {
        if (InputValidation.checkStringBlank(name)) {
            throw new IllegalArgumentException("Employee name cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkEmployeeAge
     |
     |  Purpose:  Validates the employee's age string. It ensures that the
     |            age string is not null or blank. This supports data
     |            integrity for the 'age' field.
     |
     |  Pre-condition:  The 'age' string is provided.
     |
     |  Post-condition: If the 'age' string is blank, an
     |                  IllegalArgumentException is thrown. Otherwise, the
     |                  method completes normally.
     |
     |  Parameters:
     |      age -- The string representing the employee's age to be validated.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkEmployeeAge(String age) {
        if (InputValidation.checkStringBlank(age)) {
            throw new IllegalArgumentException("Employee age cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkEmployeeGender
     |
     |  Purpose:  Validates the employee's gender string. It ensures that
     |            the gender string is not null or blank. This supports
     |            data integrity for the 'gender' field.
     |
     |  Pre-condition:  The 'gender' string is provided.
     |
     |  Post-condition: If the 'gender' string is blank, an
     |                  IllegalArgumentException is thrown. Otherwise, the
     |                  method completes normally.
     |
     |  Parameters:
     |      gender -- The string representing the employee's gender to be validated.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkEmployeeGender(String gender) {
        if (InputValidation.checkStringBlank(gender)) {
            throw new IllegalArgumentException("Employee gender cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkEmployeePosition
     |
     |  Purpose:  Validates the employee's position string. It ensures that
     |            the position string is not null or blank. This supports
     |            data integrity for the 'position' field.
     |
     |  Pre-condition:  The 'position' string is provided.
     |
     |  Post-condition: If the 'position' string is blank, an
     |                  IllegalArgumentException is thrown. Otherwise, the
     |                  method completes normally.
     |
     |  Parameters:
     |      position -- The string representing the employee's position to be validated.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkEmployeePosition(String position) {
        if (InputValidation.checkStringBlank(position)) {
            throw new IllegalArgumentException("Employee position cannot be blank");
        }
    }

    /*---------------------------------------------------------------------
     |  Method checkEmployeeSalary
     |
     |  Purpose:  Validates the employee's salary. It ensures that the
     |            salary is not a negative value. This supports data
     |            integrity for the 'salary' field.
     |
     |  Pre-condition:  The 'salary' integer is provided.
     |
     |  Post-condition: If the 'salary' is less than 0, an
     |                  IllegalArgumentException is thrown. Otherwise, the
     |                  method completes normally.
     |
     |  Parameters:
     |      salary -- The integer representing the employee's salary to be validated.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    private void checkEmployeeSalary(int salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Employee salary cannot be negative");
        }
    }
}