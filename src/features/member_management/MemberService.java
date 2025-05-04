package features.member_management;

import common.DatabaseConnection;

public class MemberService {
    private final MemberDatabaseAccess memberDatabaseAccess;

    public MemberService(DatabaseConnection databaseConnection) {
        memberDatabaseAccess = new MemberDatabaseAccess(databaseConnection.getConnection());
    }

    public long registerMember(String name, String telephoneNumber, String email, String dateOfBirth,
                                 String emergencyContact) {
        Member newMember = new Member(name, telephoneNumber, email, dateOfBirth, emergencyContact);

        checkEmailDuplicate(email);

        Long newId = memberDatabaseAccess.createMember(newMember);
        if (newId == null) {
            throw new RuntimeException("Failed to add new member to the database.");
        }

        return newId;
    }

    private void checkEmailDuplicate(String email) {
        if (memberDatabaseAccess.findMemberByEmail(email).isPresent()) {
            throw new RuntimeException("Member with email " + email + " already exists.");
        };
    }

    public void updateMember(Member member) {
        if (member == null || member.getId() == null) {
            throw new IllegalArgumentException("Member id cannot be null");
        }

        boolean status = memberDatabaseAccess.updateMember(member);

        if (!status) {
            throw new RuntimeException("Failed to update member with id " + member.getId());
        }
    }
}
