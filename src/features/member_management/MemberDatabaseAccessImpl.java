package features.member_management;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MemberDatabaseAccessImpl implements IMemberDatabaseAccess {
    private final Connection connection;

    public MemberDatabaseAccessImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Long createMember(Member member) {
        String sql = "INSERT INTO members (name, telephone, email, date_of_birth, emergency_contact) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, member.name());
            preparedStatement.setString(2, member.telephoneNumber());
            preparedStatement.setString(3, member.email());
            preparedStatement.setString(4, member.dateOfBirth());
            preparedStatement.setString(5, member.emergencyContact());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            // Get the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating member failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Member> findMemberById(Long id) {
        String sql = "SELECT id, name, telephone, email, date_of_birth, emergency_contact FROM members WHERE id = ?";

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

    @Override
    public void updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, telephone = ?, email = ?, date_of_birth = ?, emergency_contact = ?, WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, member.name());
            preparedStatement.setString(2, member.telephoneNumber());
            preparedStatement.setString(3, member.email());
            preparedStatement.setString(4, member.dateOfBirth());
            preparedStatement.setString(5, member.emergencyContact());
            preparedStatement.setLong(6, member.id()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating member failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMember(Member member) {
        String sql = "DELETE FROM members WHERE id = ?";

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

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";

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
        String sql = "SELECT id, name, telephone, email, date_of_birth, emergency_contact FROM members ORDER BY name";

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

}
