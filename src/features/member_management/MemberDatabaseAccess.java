package features.member_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

class MemberDatabaseAccess {
    private final Connection connection;

    public MemberDatabaseAccess(Connection connection) {
        this.connection = connection;
    }

    public Optional<Member> findMemberById(long id) {
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
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String telephone = resultSet.getString("telephone");
        String email = resultSet.getString("email");
        String dateOfBirth = resultSet.getString("date_of_birth");
        String emergencyContact = resultSet.getString("emergency_contact");

        return new Member(id, name, telephone, email, dateOfBirth, emergencyContact);
    }
}
