package features.member_management;

import java.util.Optional;

public interface IMemberDatabaseAccess {
    Long createMember(Member member);

    Optional<Member> findMemberById(Long id);

    void updateMember(Member member);

    void deleteMember(Member member);

    Optional<Member> findMemberByEmail(String email);
}