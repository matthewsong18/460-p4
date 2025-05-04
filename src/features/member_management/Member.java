package features.member_management;

import java.util.Objects;

public record Member(
        Long id,
        String name,
        String telephoneNumber,
        String email,
        String dateOfBirth,
        String emergencyContact
) {

    // Service Constructor for creating a new member without an ID before database insertion
    public Member(String name, String telephoneNumber, String email, String dateOfBirth, String emergencyContact) {
        this(null, name, telephoneNumber, email, dateOfBirth, emergencyContact);
    }

    // Canonical constructor
    public Member {
        validateInputs(name, telephoneNumber, email, dateOfBirth, emergencyContact);
    }

    private static void validateInputs(String name, String telephone, String email, String dateOfBirth,
                                       String emergencyContact) {
        checkName(name);
        checkTelephone(telephone);
        checkEmail(email);
        checkDateOfBirth(dateOfBirth);
        checkEmergencyContact(emergencyContact);
    }

    private static void checkName(String name) {
        if (checkBlank(name)) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }

    private static boolean checkBlank(Object object) {
        if (Objects.isNull(object)) {
            return true;
        }
        return object instanceof String s && s.isBlank();
    }

    private static void checkTelephone(String telephone) {
        if (checkBlank(telephone)) {
            throw new IllegalArgumentException("Telephone number cannot be blank");
        }
    }

    private static void checkEmail(String email) {
        if (checkBlank(email)) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }

    private static void checkDateOfBirth(String dateOfBirth) {
        if (checkBlank(dateOfBirth)) {
            throw new IllegalArgumentException("Date of birth cannot be blank");
        }
    }

    private static void checkEmergencyContact(String emergencyContact) {
        if (checkBlank(emergencyContact)) {
            throw new IllegalArgumentException("Emergency contact cannot be blank");
        }
    }

}