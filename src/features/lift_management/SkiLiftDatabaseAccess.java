package features.lift_management;

import java.sql.*;
import java.util.Optional;

public class SkiLiftDatabaseAccess {
    private final Connection conn;

    public SkiLiftDatabaseAccess(Connection conn) {
        this.conn = conn;
    }

    public Long create(SkiLift lift) {
        String sql = "INSERT INTO Lift (lift_name, open_time, close_time, difficulty_level, location) VALUES (?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, lift.lift_name());
            ps.setString(2, lift.open_time());
            ps.setString(3, lift.close_time());
            ps.setInt(4, lift.difficulty_level());
            ps.setString(5, lift.location());

            int affected = ps.executeUpdate();
            if (affected == 0)
                throw new SQLException("Insert failed.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return keys.getLong(1);
                else
                    throw new SQLException("No lift ID returned.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting lift: " + e.getMessage(), e);
        }
    }

    public Optional<SkiLift> find(Long id) {
        String sql = "SELECT * FROM Lift WHERE lift_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding lift: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public void update(SkiLift lift) {
        String sql = "UPDATE Lift SET lift_name=?, open_time=?, close_time=?, difficulty_level=?, location=? WHERE lift_id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lift.lift_name());
            ps.setString(2, lift.open_time());
            ps.setString(3, lift.close_time());
            ps.setInt(4, lift.difficulty_level());
            ps.setString(5, lift.location());
            ps.setLong(6, lift.lift_id());

            int updated = ps.executeUpdate();
            if (updated == 0)
                throw new SQLException("No lift updated.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating lift: " + e.getMessage(), e);
        }
    }

    private SkiLift map(ResultSet rs) throws SQLException {
        return new SkiLift(
                rs.getLong("lift_id"),
                rs.getString("lift_name"),
                rs.getString("open_time"),
                rs.getString("close_time"),
                rs.getInt("difficulty_level"),
                rs.getString("location"));
    }
}
