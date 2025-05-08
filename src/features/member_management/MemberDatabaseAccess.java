package features.member_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*+----------------------------------------------------------------------
 ||
 ||  Class MemberDatabaseAccess
 ||
 ||      Author:  Matthew Song, Stephin Tomson
 ||
 ||      Purpose:  This class is responsible for all database interactions
 ||                related to member data. It handles creating, retrieving,
 ||                updating, and deleting member records in the database.
 ||                It serves as the backend logic for member services.
 ||
 ||  Inherits From:  common.DatabaseAccess
 ||
 ||      Interfaces:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  MemberDatabaseAccess(DatabaseConnection databaseConnection)
 ||
 ||  Class Methods:  None.
 ||
 ||  Inst. Methods:  Long createMember(Member member)
 ||                  Optional<Member> findMemberById(Long id)
 ||                  Optional<Member> findMemberByEmail(String email)
 ||                  List<Member> getAllMembers()
 ||                  boolean updateMember(Member member)
 ||                  void deleteMember(Member member)
 ||                  private Member mapRowToMember(ResultSet resultSet) throws SQLException // Though private, it's a key helper
 ||
 ++-----------------------------------------------------------------------*/
public class MemberDatabaseAccess extends DatabaseAccess {

    /*---------------------------------------------------------------------
     |  Method MemberDatabaseAccess (Constructor)
     |
     |  Purpose:  Constructs a new MemberDatabaseAccess object, initializing
     |            it with a database connection. This connection is managed
     |            by the parent DatabaseAccess class.
     |
     |  Pre-condition:  The databaseConnection object must be a valid, open
     |                  connection to the database.
     |
     |  Post-condition: A new MemberDatabaseAccess instance is created and
     |                  ready to interact with the member table in the database.
     |
     |  Parameters:
     |      databaseConnection -- An instance of DatabaseConnection that provides
     |                            the active connection to the database.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public MemberDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    /*---------------------------------------------------------------------
     |  Method createMember
     |
     |  Purpose:  Inserts a new member record into the database. It first
     |            retrieves the next available unique ID for the member,
     |            then executes an INSERT SQL statement.
     |
     |  Pre-condition:  The 'member' object contains valid data for all
     |                  required fields (name, telephone, email, dateOfBirth,
     |                  emergencyContact). The database connection must be active.
     |                  The `getNextId` method must be functional and return a unique ID.
     |
     |  Post-condition: If successful, a new row is inserted into the
     |                  'stephintomson.Member' table, and the ID of the newly
     |                  created member is returned. If the insertion fails,
     |                  an SQLException is thrown (wrapped in a RuntimeException).
     |
     |  Parameters:
     |      member -- A Member object containing the details of the member
     |                to be created. The 'id' field of this object is ignored
     |                as a new one will be generated.
     |
     |  Returns:  A Long representing the unique ID assigned to the newly
     |            created member.
     *-------------------------------------------------------------------*/
    public Long createMember(Member member) {
        String sql = "INSERT INTO stephintomson.Member VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newMemberId = getNextId("Member", "member_id");

            preparedStatement.setLong(1, newMemberId);
            preparedStatement.setString(2, member.name());
            preparedStatement.setString(3, member.telephoneNumber());
            preparedStatement.setString(4, member.email());
            preparedStatement.setDate(5, member.dateOfBirth());
            preparedStatement.setLong(6, member.emergencyContact());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            return newMemberId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method findMemberById
     |
     |  Purpose:  Retrieves a member from the database based on their unique ID.
     |            It executes a SELECT SQL query and maps the result to a
     |            Member object if a record is found.
     |
     |  Pre-condition:  The 'id' parameter is a valid Long. The database
     |                  connection must be active.
     |
     |  Post-condition: If a member with the given ID exists, an Optional
     |                  containing the Member object is returned. Otherwise,
     |                  an empty Optional is returned. If a database error
     |                  occurs, a RuntimeException is thrown.
     |
     |  Parameters:
     |      id -- The unique identifier (Long) of the member to find.
     |
     |  Returns:  An Optional<Member>. It contains the Member object if found,
     |            otherwise it is empty.
     *-------------------------------------------------------------------*/
    public Optional<Member> findMemberById(Long id) {
        String sql = "SELECT * FROM stephintomson.Member WHERE member_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Member member = mapRowToMember(resultSet);
                    return Optional.of(member);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    /*---------------------------------------------------------------------
     |  Method mapRowToMember
     |
     |  Purpose:  A private helper method that converts a row from a ResultSet
     |            into a Member object. This method encapsulates the logic for
     |            extracting data from the ResultSet and instantiating a Member.
     |
     |  Pre-condition:  The 'resultSet' parameter is a valid ResultSet, and its
     |                  cursor is positioned on a row containing member data.
     |                  The ResultSet must contain columns named 'member_id',
     |                  'member_name', 'telephone', 'email', 'date_of_birth',
     |                  and 'emergency_contact'.
     |
     |  Post-condition: A Member object is created and populated with data
     |                  from the current row of the ResultSet.
     |
     |  Parameters:
     |      resultSet -- The ResultSet from which to extract member data.
     |                   It is expected to be pointing to a valid row.
     |
     |  Returns:  A Member object initialized with the data from the current
     |            ResultSet row.
     *-------------------------------------------------------------------*/
    private Member mapRowToMember(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("member_id");
        String name = resultSet.getString("member_name");
        String telephone = resultSet.getString("telephone");
        String email = resultSet.getString("email");
        Date dateOfBirth = resultSet.getDate("date_of_birth");
        Long emergencyContact = resultSet.getLong("emergency_contact");

        return new Member(id, name, telephone, email, dateOfBirth, emergencyContact);
    }

    /*---------------------------------------------------------------------
     |  Method findMemberByEmail
     |
     |  Purpose:  Retrieves a member from the database based on their email
     |            address. It executes a SELECT SQL query and maps the result
     |            to a Member object if a record is found.
     |
     |  Pre-condition:  The 'email' parameter is a valid String. The database
     |                  connection must be active. The email column in the
     |                  database should have a unique constraint for this
     |                  method to reliably return a single member.
     |
     |  Post-condition: If a member with the given email exists, an Optional
     |                  containing the Member object is returned. Otherwise,
     |                  an empty Optional is returned. If a database error
     |                  occurs, a RuntimeException is thrown.
     |
     |  Parameters:
     |      email -- The email address (String) of the member to find.
     |
     |  Returns:  An Optional<Member>. It contains the Member object if found,
     |            otherwise it is empty.
     *-------------------------------------------------------------------*/
    public Optional<Member> findMemberByEmail(String email) {
        String sql = "SELECT * FROM stephintomson.Member WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Member member = mapRowToMember(resultSet);
                    return Optional.of(member);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    /*---------------------------------------------------------------------
     |  Method getAllMembers
     |
     |  Purpose:  Retrieves all member records from the database, ordered by
     |            member name. It executes a SELECT SQL query and maps each
     |            row to a Member object, adding it to a list.
     |
     |  Pre-condition:  The database connection must be active.
     |
     |  Post-condition: A List of Member objects is returned. If no members
     |                  are found, an empty list is returned. If a database
     |                  error occurs, a RuntimeException is thrown. The returned
     |                  list is ordered by 'member_name'.
     |
     |  Parameters: None.
     |
     |  Returns:  A List<Member> containing all members found in the
     |            database, or an empty list if none are found.
     *-------------------------------------------------------------------*/
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM stephintomson.Member ORDER BY member_name";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                sql); ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return members;
    }

    /*---------------------------------------------------------------------
     |  Method updateMember
     |
     |  Purpose:  Updates an existing member's details in the database.
     |            It uses the member's ID to identify the record to update.
     |            Throws a RuntimeException if the member's ID is null.
     |
     |  Pre-condition:  The 'member' object must have a non-null ID and contain
     |                  the updated information. The database connection must be
     |                  active. A member with the given ID must exist in the
     |                  database for any rows to be affected.
     |
     |  Post-condition: If a member with the given ID exists, their record in
     |                  the 'stephintomson.Member' table is updated with the new
     |                  details. The method returns true if at least one row
     |                  was affected, false otherwise. If a database error occurs,
     |                  a RuntimeException is thrown.
     |
     |  Parameters:
     |      member -- A Member object containing the ID of the member to update
     |                and their new details.
     |
     |  Returns:  A boolean value: true if the update was successful and
     |            at least one row was affected, false otherwise.
     *-------------------------------------------------------------------*/
    public boolean updateMember(Member member) {
        if (member.id() == null) {
            throw new RuntimeException("Cannot update member without an ID.");
        }
        String sql = "UPDATE stephintomson.Member SET member_name = ?, telephone = ?, email = ?, date_of_birth = ?, emergency_contact = ? WHERE member_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, member.name());
            preparedStatement.setString(2, member.telephoneNumber());
            preparedStatement.setString(3, member.email());
            preparedStatement.setDate(4, member.dateOfBirth());
            preparedStatement.setLong(5, member.emergencyContact());
            preparedStatement.setLong(6, member.id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*---------------------------------------------------------------------
     |  Method deleteMember
     |
     |  Purpose:  Deletes a member from the database based on their ID.
     |
     |  Pre-condition:  The 'member' object must have a non-null ID. The
     |                  database connection must be active.
     |
     |  Post-condition: If a member with the given ID exists, their record is
     |                  deleted from the 'stephintomson.Member' table. If no
     |                  rows are affected (e.g., member not found), an
     |                  SQLException is thrown (wrapped in a RuntimeException).
     |                  If another database error occurs, a RuntimeException is thrown.
     |
     |  Parameters:
     |      member -- A Member object whose ID is used to identify the record
     |                to be deleted. Other fields in the member object are
     |                not used by this method.
     |
     |  Returns:  None.
     *-------------------------------------------------------------------*/
    public void deleteMember(Member member) {

        String sql = "DELETE FROM stephintomson.Member WHERE member_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, member.id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting member failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}