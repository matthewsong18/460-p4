package features.member_management;

import java.util.Objects;

public class Member {
    private Long id;
    private String name;
    private String telephoneNumber;
    private String email;
    private String dateOfBirth;
    private String emergencyContact;

    public Member(String name, String telephoneNumber, String email, String dateOfBirth, String emergencyContact) {
        id = null;
        this.name = name;
        this.telephoneNumber = telephoneNumber;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telephone='" + telephoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        // Usually base equality on ID if present, or email if unique
        if (id != null) {
            return Objects.equals(id, member.id);
        }
        return Objects.equals(email, member.email);
    }

    @Override
    public int hashCode() {
        // Match equals: use ID if present, otherwise email
        return Objects.hash(id != null ? id : email);
    }
}