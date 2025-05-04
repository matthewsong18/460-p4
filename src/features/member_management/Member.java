package features.member_management;

import java.util.Objects;

public class Member {
    private Long id;
    private String name;
    private String telephoneNumber;
    private String email;
    private String dateOfBirth;
    private String emergencyContact;

    // Use to create a member object to add to the database
    public Member(String name, String telephone, String email, String dateOfBirth, String emergencyContact) {
        validateInputs(name, telephone, email, dateOfBirth, emergencyContact);

        id = null;
        this.name = name;
        this.telephoneNumber = telephone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
    }

    private void validateInputs(String name, String telephone, String email, String dateOfBirth,
                                String emergencyContact) {
        checkName(name);
        checkTelephone(telephone);
        checkEmail(email);
        checkDateOfBirth(dateOfBirth);
        checkEmergencyContact(emergencyContact);
    }

    private void checkName(String name) {
        if (checkBlank(name)) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }

    private boolean checkBlank(Object object) {
        if (Objects.isNull(object)) {
            return true;
        }

        return object instanceof String s && s.isBlank();
    }

    private void checkTelephone(String telephone) {
        if (checkBlank(telephone)) {
            throw new IllegalArgumentException("Telephone number cannot be blank");
        }
    }

    private void checkEmail(String email) {
        if (checkBlank(email)) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }

    private void checkDateOfBirth(String dateOfBirth) {
        if (checkBlank(dateOfBirth)) {
            throw new IllegalArgumentException("Date of birth cannot be blank");
        }
    }

    private void checkEmergencyContact(String emergencyContact) {
        if (checkBlank(emergencyContact)) {
            throw new IllegalArgumentException("Emergency contact cannot be blank");
        }
    }


    // Used by the database access object to create a member with an id
    public Member(long id, String name, String telephone, String email, String dateOfBirth, String emergencyContact) {
        this.id = id;
        this.name = name;
        this.telephoneNumber = telephone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
    }

    public Long getId() {
        if (id == null) {
            return 0L;
        }
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    @Override
    public int hashCode() {
        // Match equals: use ID if present, otherwise email
        return Objects.hash(id != null ? id : email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        // Usually base equality on ID if present, or email if unique
        if (id != null) {
            return Objects.equals(id, member.id);
        }
        return Objects.equals(email, member.email);
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", name='" + name + '\'' + ", telephone='" + telephoneNumber + '\'' + ", email='" + email + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", emergencyContact='" + emergencyContact + '\'' + '}';
    }
}