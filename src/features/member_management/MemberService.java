/*
 * File: MemberService
 * 
 * Author: Matthew Song
 *         Stephin Tomson
 * 
 * Description: Client-facing API for database interactions involving the member table.
 * 
 */

package features.member_management;

import common.DatabaseConnection;
import features.lesson_management.LessonDatabaseAccess;
import features.rental_management.RentalDatabaseAccess;
import features.ski_pass.SkiPassDatabaseAccess;

import java.util.Optional;
import java.sql.Date;
import java.sql.SQLException;

public class MemberService {
    private final DatabaseConnection conn;
    private final MemberDatabaseAccess memberDatabaseAccess;

    public MemberService(DatabaseConnection databaseConnection) {
        this.conn = databaseConnection;
        memberDatabaseAccess = new MemberDatabaseAccess(databaseConnection);
    }

    public long registerMember(String name, String telephoneNumber, String email, Date dateOfBirth,
            Long emergencyContact) {
        Member newMember = new Member(name, telephoneNumber, email, dateOfBirth, emergencyContact);

        checkEmailDuplicate(email);

        Long newId = memberDatabaseAccess.createMember(newMember);
        if (newId == null) {
            throw new RuntimeException("Failed to return the member id");
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

    // This method checks if the member is associated with any ski pass data, lift
    // usage logs, rental history,
    // or lesson transactions. If any associations are found, it returns 1;
    // otherwise, it returns 0.
    // This method checks if the member is associated with any ski pass data.
    // If any associations are found, it returns 1; otherwise, it returns 0.
    public int memberPreCheck(Optional<Member> member) {
        if (member.isEmpty())
            return 0;

        try {
            SkiPassDatabaseAccess skiPassDAO = new SkiPassDatabaseAccess(conn);
            RentalDatabaseAccess rentalDAO = new RentalDatabaseAccess(conn);
            LessonDatabaseAccess lessonDAO = new LessonDatabaseAccess(conn);

            Long memberId = member.get().id();

            if (skiPassDAO.hasActiveSkiPassByMember(memberId) == 1
                    || rentalDAO.hasRentalByMember(memberId) == 1
                    || lessonDAO.hasLessonByMember(memberId) == 1) {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
