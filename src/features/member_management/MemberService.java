package features.member_management;

import common.DatabaseConnection;

public class MemberService {
    private final MemberDatabaseAccess memberDatabaseAccess;

    public MemberService(DatabaseConnection databaseConnection) {
        memberDatabaseAccess = new MemberDatabaseAccess(databaseConnection.getConnection());
    }

    public Member registerMember(String name, String telephoneNumber, String email, String dateOfBirth,
                                 String emergencyContact) {
        validateMemberInputs(name, telephoneNumber, email, dateOfBirth, emergencyContact);

        checkEmailDuplicate(email);

        Member newMember = new Member(name, telephoneNumber, email, dateOfBirth, emergencyContact);

        Long newId = memberDatabaseAccess.createMember(newMember);
        if (newId == null) {
            throw new RuntimeException("Failed to add new member to the database.");
        }

        newMember.setId(newId);

        return newMember;
    }

    private void checkEmailDuplicate(String email) {
        if (memberDatabaseAccess.findMemberByEmail(email).isEmpty()) {
            throw new RuntimeException("Member with email " + email + " already exists.");
        };
    }

    private void validateMemberInputs(String name, String telephoneNumber, String email, String dateOfBirth,
                                      String emergencyContact) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (telephoneNumber == null || telephoneNumber.isBlank()) {
            throw new IllegalArgumentException("Telephone number cannot be blank");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        if (dateOfBirth == null || dateOfBirth.isBlank()) {
            throw new IllegalArgumentException("Date of Birth cannot be blank");
        }

        if (emergencyContact == null || emergencyContact.isBlank()) {
            throw new IllegalArgumentException("Emergency Contact cannot be blank");
        }
    }

    public void updateMember(Member member) {
        // TODO add validation to Member constructor
        if (member == null || member.getId() == null) {
            throw new IllegalArgumentException("Member id cannot be null");
        }

        boolean status = memberDatabaseAccess.updateMember(member);

        if (!status) {
            throw new RuntimeException("Failed to update member with id " + member.getId());
        }
    }
}
