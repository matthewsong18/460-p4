/*
 * File: Member
 * 
 * Author: Matthew Song
 * 
 * Description: Local representation of a member tuple in the database
 * 
 */

package features.member_management;

import common.InputValidation;
import java.sql.Date;

public record Member(
        Long id,
        String name,
        String telephoneNumber,
        String email,
        Date dateOfBirth,
        Long emergencyContact
) {

    // Service Constructor for creating a new member without an ID before database insertion
    public Member(String name, String telephoneNumber, String email, Date dateOfBirth, Long emergencyContact) {
        this(null, name, telephoneNumber, email, dateOfBirth, emergencyContact);
    }

    // Canonical constructor
    public Member {
        validateInputs(name, telephoneNumber, email, dateOfBirth, emergencyContact);
    }

    private static void validateInputs(String name, String telephone, String email, Date dateOfBirth,
                                       Long emergencyContact) {
        checkName(name);
        checkTelephone(telephone);
        checkEmail(email);
        checkDateOfBirth(dateOfBirth);
        checkEmergencyContact(emergencyContact);
    }

    private static void checkName(String name) {
        if (InputValidation.checkStringBlank(name)) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }

    private static void checkTelephone(String telephone) {
        if (InputValidation.checkStringBlank(telephone)) {
            throw new IllegalArgumentException("Telephone number cannot be blank");
        }
        if (telephone.length() != 10) {
            throw new IllegalArgumentException("Telephone number must have 10 digits");
        }
    }

    private static void checkEmail(String email) {
        if (InputValidation.checkStringBlank(email)) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }

    private static void checkDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be blank");
        }
    }

    private static void checkEmergencyContact(Long emergencyContact) {
        if (emergencyContact == null) {
            throw new IllegalArgumentException("Emergency contact cannot be blank");
        }
        if (emergencyContact < 1000000000L || emergencyContact > 9999999999L) {
            throw new IllegalArgumentException("Emergency contact must have 10 digits");
        }
    }

}