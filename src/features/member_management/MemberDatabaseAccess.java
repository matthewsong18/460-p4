package features.member_management;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MemberDatabaseAccess extends DatabaseAccess {

    public MemberDatabaseAccess(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Long createMember(Member member) {
        String sql = "INSERT INTO Member VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long newMemberId = getNextId("Member", "member_id");

            preparedStatement.setLong(1, newMemberId);
            preparedStatement.setString(2, member.name());
            preparedStatement.setString(3, member.telephoneNumber());
            preparedStatement.setString(4, member.email());
            preparedStatement.setString(5, member.dateOfBirth());
            preparedStatement.setString(6, member.emergencyContact());

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
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String telephone = resultSet.getString("telephone");
        String email = resultSet.getString("email");
        String dateOfBirth = resultSet.getString("date_of_birth");
        String emergencyContact = resultSet.getString("emergency_contact");

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
        String sql = "SELECT * FROM stephintomson.Member ORDER BY name";

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
        String sql = "UPDATE stephintomson.Member SET name = ?, telephone = ?, email = ?, date_of_birth = ?, emergency_contact = ? WHERE member_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, member.name());
            preparedStatement.setString(2, member.telephoneNumber());
            preparedStatement.setString(3, member.email());
            preparedStatement.setString(4, member.dateOfBirth());
            preparedStatement.setString(5, member.emergencyContact());
            preparedStatement.setLong(6, member.id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
