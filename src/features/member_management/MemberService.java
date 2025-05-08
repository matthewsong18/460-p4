package features.member_management;

import common.DatabaseConnection;
import features.lesson_management.LessonDatabaseAccess;
import features.rental_management.RentalDatabaseAccess;
import features.ski_pass.SkiPassDatabaseAccess;

import java.util.Optional;
import java.sql.Date;
import java.sql.SQLException;

/*+----------------------------------------------------------------------
 ||
 ||  Class MemberService
 ||
 ||      Author:  Matthew Song, Stephin Tomson
 ||
 ||      Purpose:  This class acts as a service layer, providing a
 ||                client-facing API for operations related to members.
 ||                It orchestrates calls to MemberDatabaseAccess and
 ||                other DAOs for more complex operations like registration
 ||                (including duplicate checks) and pre-deletion checks.
 ||                It encapsulates business logic associated with member
 ||                management.
 ||
 ||  Inherits From:  None.
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  MemberService(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  long registerMember(String name, String telephoneNumber, String email, Date dateOfBirth, Long emergencyContact)
 ||                  Optional<Member> findMemberById(Long id)
 ||                  void updateMember(Member member)
 ||                  void deleteMemberById(Member member)
 ||                  int memberPreCheck(Optional<Member> member)
 ||                  private void checkEmailDuplicate(String email) // Though private, it's a key helper
 ||
 ++-----------------------------------------------------------------------*/
public class MemberService {
    private final DatabaseConnection conn;
    private final MemberDatabaseAccess memberDatabaseAccess;

    /*---------------------------------------------------------------------
     |  Method MemberService (Constructor)
     |
     |  Purpose:  Constructs a new MemberService object, initializing it
     |            with a database connection and creating an instance of
     |            MemberDatabaseAccess for data operations.
     |
     |  Pre-condition:  The databaseConnection object must be a valid, open
     |                  connection to the database.
     |
     |  Post-condition: A new MemberService instance is created with its
     |                  own DatabaseConnection and a MemberDatabaseAccess
     |                  object, ready to perform member-related services.
     |
     |  Parameters:
     |      databaseConnection -- An instance of DatabaseConnection that provides
     |                            the active connection to the database.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public MemberService(DatabaseConnection databaseConnection) {
        this.conn = databaseConnection;
        memberDatabaseAccess = new MemberDatabaseAccess(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method registerMember
     |
     |  Purpose:  Registers a new member by first checking for email
     |            duplicates and then creating the member record in the
     |            database via MemberDatabaseAccess.
     |
     |  Pre-condition:  All parameters (name, telephoneNumber, email,
     |                  dateOfBirth, emergencyContact) must be valid and
     |                  appropriately formatted. The database connection
     |                  must be active.
     |
     |  Post-condition: If the email is not a duplicate and the creation
     |                  is successful, a new member is created in the
     |                  database, and their new ID is returned.
     |                  If the email already exists, a RuntimeException is thrown.
     |                  If the member ID is not returned from the database access
     |                  layer, a RuntimeException is thrown.
     |
     |  Parameters:
     |      name -- The full name of the member.
     |      telephoneNumber -- The telephone number of the member.
     |      email -- The email address of the member; this is checked for
     |               uniqueness.
     |      dateOfBirth -- The date of birth of the member.
     |      emergencyContact -- The ID of the emergency contact for the member.
     |
     |  Returns:  A long representing the unique ID assigned to the newly
     |            registered member.
     *-------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------
     |  Method checkEmailDuplicate
     |
     |  Purpose:  A private helper method to check if a given email address
     |            already exists for another member in the database.
     |
     |  Pre-condition:  The 'email' parameter is a non-null String.
     |                  The memberDatabaseAccess object is initialized.
     |
     |  Post-condition: If a member with the specified email is found, a
     |                  RuntimeException is thrown. Otherwise, the method
     |                  completes without side effects.
     |
     |  Parameters:
     |      email -- The email address (String) to check for duplication.
     |
     |  Returns:  None. Throws RuntimeException if email is a duplicate.
     *-------------------------------------------------------------------*/
    private void checkEmailDuplicate(String email) {
        if (memberDatabaseAccess.findMemberByEmail(email).isPresent()) {
            throw new RuntimeException("Member with email " + email + " already exists.");
        }
    }

    /*---------------------------------------------------------------------
     |  Method findMemberById
     |
     |  Purpose:  Finds a member by their unique ID. It validates that the
     |            ID is not null and then calls the MemberDatabaseAccess
     |            to retrieve the member.
     |
     |  Pre-condition:  The database connection must be active.
     |
     |  Post-condition: Returns an Optional containing the Member if found,
     |                  otherwise an empty Optional. Throws
     |                  IllegalArgumentException if ID is null. Wraps and
     |                  re-throws RuntimeExceptions from the database access layer.
     |
     |  Parameters:
     |      id -- The unique identifier (Long) of the member to find.
     |
     |  Returns:  An Optional<Member>. It contains the Member object if found,
     |            otherwise it is empty.
     *-------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------
     |  Method updateMember
     |
     |  Purpose:  Updates the details of an existing member. It validates
     |            that the member object and its ID are not null, then calls
     |            MemberDatabaseAccess to perform the update.
     |
     |  Pre-condition:  The 'member' object must not be null, and its 'id'
     |                  field must not be null and must correspond to an
     |                  existing member. The database connection must be active.
     |
     |  Post-condition: The member's record in the database is updated.
     |                  If the update fails (e.g., member not found or database
     |                  error), a RuntimeException is thrown. Throws
     |                  IllegalArgumentException if member or member.id() is null.
     |
     |  Parameters:
     |      member -- A Member object containing the ID of the member to
     |                update and their new details.
     |
     |  Returns:  None. Throws RuntimeException if update fails.
     *-------------------------------------------------------------------*/
    public void updateMember(Member member) {
        if (member == null || member.id() == null) {
            throw new IllegalArgumentException("Member id cannot be null");
        }

        boolean status = memberDatabaseAccess.updateMember(member);

        if (!status) {
            throw new RuntimeException("Failed to update member with id " + member.id());
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteMemberById
     |
     |  Purpose:  Deletes a member using their Member object (primarily its ID).
     |            It validates that the member object and its ID are not null,
     |            then calls MemberDatabaseAccess to perform the deletion.
     |
     |  Pre-condition:  The 'member' object must not be null, and its 'id'
     |                  field must not be null and correspond to an existing,
     |                   deletable member. The database connection must be active.
     |                  Consider implications of memberPreCheck before calling.
     |
     |  Post-condition: The member's record is deleted from the database.
     |                  If the deletion fails (e.g., member not found or
     |                  database error), a RuntimeException is thrown. Throws
     |                  IllegalArgumentException if member or member.id() is null.
     |
     |  Parameters:
     |      member -- A Member object whose ID is used to identify the record
     |                to be deleted.
     |
     |  Returns:  None. Throws RuntimeException if deletion fails.
     *-------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------
     |  Method memberPreCheck
     |
     |  Purpose:  Checks if a given member has any associated records in
     |            other parts of the system (ski passes, rentals, lessons).
     |            This is typically used to determine if a member can be
     |            safely deleted without leaving orphaned data.
     |
     |  Pre-condition:  The 'member' Optional may or may not contain a Member.
     |                  The database connection ('conn') must be active and valid.
     |                  SkiPassDatabaseAccess, RentalDatabaseAccess, and
     |                  LessonDatabaseAccess must be correctly implemented.
     |
     |  Post-condition: Returns 1 if the member has any active ski passes,
     |                  rental history, or lesson transactions. Returns 0 if
     |                  the member Optional is empty or if no such associations
     |                  are found. Catches and prints stack trace for SQLExceptions,
     |                  returning 0 in case of such an error during checks.
     |
     |  Parameters:
     |      member -- An Optional<Member> representing the member to check.
     |                If the Optional is empty, the method returns 0.
     |
     |  Returns:  An int: 1 if associations are found, 0 otherwise (or if
     |            member is empty or an SQL error occurs during checks).
     *-------------------------------------------------------------------*/
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