package features.member_management;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MemberDatabaseAccess {
    private final Connection connection;

    public MemberDatabaseAccess(Connection connection) {
        this.connection = connection;
    }

    public Long createMember(Member member) {
        String sql = "INSERT INTO members (name, telephone, email, date_of_birth, emergency_contact) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getTelephoneNumber());
            preparedStatement.setString(3, member.getEmail());
            preparedStatement.setString(4, member.getDateOfBirth());
            preparedStatement.setString(5, member.getEmergencyContact());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Creating member failed, no rows affected.");
                return null;
            }

            // Get the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    System.err.println("Creating member failed, no ID obtained.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            return null;
        }
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

    public Optional<Member> findMemberByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT id, name, telephone, email, date_of_birth, emergency_contact FROM members ORDER BY name";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                sql); ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all members: " + e.getMessage());
        }
        return members;
    }


    public boolean updateMember(Member member) {
        if (member.getId() == null) {
            System.err.println("Cannot update member without an ID.");
            return false;
        }
        String sql = "UPDATE members SET name = ?, telephone = ?, email = ?, date_of_birth = ?, emergency_contact = ?, WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getTelephoneNumber());
            preparedStatement.setString(3, member.getEmail());
            preparedStatement.setString(4, member.getDateOfBirth());
            preparedStatement.setString(5, member.getEmergencyContact());
            preparedStatement.setLong(6, member.getId()); // WHERE

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMember(long id) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

}
