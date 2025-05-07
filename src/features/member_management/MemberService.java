package features.member_management;

import java.util.Optional;

public class MemberService {
    private final IMemberDatabaseAccess memberDatabaseAccess;

    public MemberService(IMemberDatabaseAccess memberDatabaseAccess) {
        this.memberDatabaseAccess = memberDatabaseAccess;
    }

    public long registerMember(String name, String telephoneNumber, String email, String dateOfBirth,
                               String emergencyContact) {
        // Member has internal validation as well
        Member newMember = new Member(name, telephoneNumber, email, dateOfBirth, emergencyContact);

        checkEmailDuplicate(email);

        return memberDatabaseAccess.createMember(newMember);
    }

    private void checkEmailDuplicate(String email) {
        if (memberDatabaseAccess.findMemberByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Member with email " + email + " already exists.");
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

        try {
            memberDatabaseAccess.updateMember(member);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update member with id " + member.id(), e);
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
