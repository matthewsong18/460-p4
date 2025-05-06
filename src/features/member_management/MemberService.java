package features.member_management;

import common.DatabaseConnection;

import java.util.Optional;

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
        }
    }

    public Optional<Member> findMemberById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }

        try {
            return memberDatabaseAccess.findMemberById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find member with id " + id, e);
        }
    }

    public void updateMember(Member member) {
        if (member == null || member.id() == null) {
            throw new IllegalArgumentException("Member id cannot be null");
        }

        boolean status = memberDatabaseAccess.updateMember(member);

        if (!status) {
            throw new RuntimeException("Failed to update member with id " + member.id());
        }
    }

    public void deleteMemberById(Member member) {
        if (member == null || member.id() == null) {
            throw new IllegalArgumentException("Member id cannot be null");
        }

        try {
            memberDatabaseAccess.deleteMember(member);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete member with id " + member.id(), e);
        }
    }
}
