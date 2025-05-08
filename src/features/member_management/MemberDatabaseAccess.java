package features.member_management;

import common.DatabaseAccess;
import common.DatabaseConnection;
import features.rental_management.RentalDatabaseAccess;
import features.ski_pass.SkiPassDatabaseAccess;
import features.lesson_management.LessonDatabaseAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MemberDatabaseAccess extends DatabaseAccess {

    public MemberDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

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

    // Helper method to map a ResultSet row to a Member object
    private Member mapRowToMember(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("member_id");
        String name = resultSet.getString("member_name");
        String telephone = resultSet.getString("telephone");
        String email = resultSet.getString("email");
        Date dateOfBirth = resultSet.getDate("date_of_birth");
        Long emergencyContact = resultSet.getLong("emergency_contact");

        return new Member(id, name, telephone, email, dateOfBirth, emergencyContact);
    }

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

    // This method checks if the member is associated with any ski pass data, lift
    // usage logs, rental history,
    // or lesson transactions. If any associations are found, it returns 1;
    // otherwise, it returns 0.
    // This method checks if the member is associated with any ski pass data.
    // If any associations are found, it returns 1; otherwise, it returns 0.
    private int memberPreCheck(Member member) {
        try {
            if (SkiPassDatabaseAccess.activeSkiPassbyMemberId(member.id()) == 1
                    || RentalDatabaseAccess.hasRentalByMember(member.id()) // e.g., returns 1 if any rental
                    || LessonDatabaseAccess.hasLessonForMember(member.id())) {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // This method remove all related records, including ski pass data, lift usage
    // logs, rental history, and
    // lesson transactions associated with the member.
    private void memberPostCheck(Member member) {

    }

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
