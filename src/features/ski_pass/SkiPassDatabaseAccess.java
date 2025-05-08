package features.ski_pass;

import common.DatabaseAccess;
import common.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SkiPassDatabaseAccess extends DatabaseAccess {

    public SkiPassDatabaseAccess(DatabaseConnection dbConnection) {
        super(dbConnection);
    }

    public Long createSkiPass(SkiPass skiPass) throws SQLException {

        String insertSql = "INSERT INTO stephintomson.SkiPass VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            Long newPassId = getNextId("SkiPass", "pass_id"); // Helper to get a new ID
            java.sql.Date sqlDate = java.sql.Date.valueOf(skiPass.purchaseDate() + "");
            preparedStatement.setLong(1, newPassId);
            preparedStatement.setLong(2, skiPass.memberId());
            preparedStatement.setDate(3, sqlDate);
            preparedStatement.setInt(4, skiPass.totalUses());
            preparedStatement.setInt(5, skiPass.remainingUses());
            preparedStatement.setString(6, skiPass.passType());
            preparedStatement.setInt(7, skiPass.price());
            preparedStatement.setInt(8, skiPass.archivedStatus());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ski pass failed, no rows affected.");
            }
            return newPassId;
        }
    }

    public Optional<SkiPass> findSkiPassById(long passId) throws SQLException {
        String sql = "SELECT pass_id, member_id, purchase_date, total_uses, remaining_uses, " +
                "pass_type, price, archived_status FROM stephintomson.SkiPass WHERE pass_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToSkiPassData(rs));
            }
        }
        return Optional.empty();
    }

    private SkiPass mapRowToSkiPassData(ResultSet rs) throws SQLException {
        return new SkiPass(
                rs.getLong("pass_id"),
                rs.getLong("member_id"),
                rs.getDate("purchase_date"),
                rs.getInt("total_uses"),
                rs.getInt("remaining_uses"),
                rs.getString("pass_type"),
                rs.getInt("price"),
                rs.getInt("archived_status"));
    }

    public void updateSkiPass(SkiPass skiPass) throws SQLException {
        String sql = "UPDATE stephintomson.SkiPass SET member_id = ?, purchase_date = ?, total_uses = ?, remaining_uses = ?, pass_type = ?, price = ?, archived_status = ? WHERE pass_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, skiPass.memberId());
            preparedStatement.setDate(2, skiPass.purchaseDate());
            preparedStatement.setInt(3, skiPass.totalUses());
            preparedStatement.setInt(4, skiPass.remainingUses());
            preparedStatement.setString(5, skiPass.passType());
            preparedStatement.setInt(6, skiPass.price());
            preparedStatement.setInt(7, skiPass.archivedStatus());
            preparedStatement.setLong(8, skiPass.passId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ski pass failed, no rows affected. Pass ID: " + skiPass.passId());
            }
        }
    }

    public void deleteSkiPass(long passId) throws SQLException {
        String sql = "DELETE FROM stephintomson.SkiPass WHERE pass_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting ski pass failed, no rows affected. Pass ID: " + passId);
            }
        }
    }

    public int getDaysSincePurchase(long passId) throws SQLException {
        String sql = "SELECT TRUNC(SYSDATE - purchase_date, 0) AS time_diff FROM stephintomson.SkiPass WHERE pass_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("time_diff");
            } else {
                throw new SQLException("Pass not found for calculating days since purchase, ID: " + passId);
            }
        }
    }
}