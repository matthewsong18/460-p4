package features.ski_pass;

import common.DatabaseConnection;
import common.DatabaseAccess;
import java.sql.*;
import java.util.Optional;

public class SkiPassDatabaseAccess extends DatabaseAccess {
    private final Connection connection;

    public SkiPassDatabaseAccess(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
        tableName = "SkiPass";
        pkFieldName = "pass_id";
        updateCurrentId();
    }

    public Long createSkiPass(SkiPassData skiPass) throws SQLException {
        String insertSql = "INSERT INTO SkiPass VALUES (?, ?, SYSTIMESTAMP, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            long newPassId = getCurrentId();
            pstmt.setLong(1, newPassId);
            pstmt.setLong(2, skiPass.memberId());
            // purchase_date is set by SYSTIMESTAMP in SQL
            pstmt.setInt(3, skiPass.totalUses());
            pstmt.setInt(4, skiPass.remainingUses());
            pstmt.setString(5, skiPass.passType());
            pstmt.setInt(6, skiPass.price());
            pstmt.setInt(7, skiPass.archivedStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ski pass failed, no rows affected.");
            }
            return newPassId;
        }
    }

    public Optional<SkiPassData> findSkiPassById(long passId) throws SQLException {
        String sql = "SELECT pass_id, member_id, purchase_date, total_uses, remaining_uses, pass_type, price, archived_status FROM SkiPass WHERE pass_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, passId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToSkiPassData(rs));
            }
        }
        return Optional.empty();
    }

    private SkiPassData mapRowToSkiPassData(ResultSet rs) throws SQLException {
        return new SkiPassData(
                rs.getLong("pass_id"),
                rs.getLong("member_id"),
                rs.getTimestamp("purchase_date"),
                rs.getInt("total_uses"),
                rs.getInt("remaining_uses"),
                rs.getString("pass_type"),
                rs.getInt("price"),
                rs.getInt("archived_status")
        );
    }

    public void updateSkiPass(SkiPassData skiPass) throws SQLException {
        String sql = "UPDATE SkiPass SET member_id = ?, purchase_date = ?, total_uses = ?, remaining_uses = ?, pass_type = ?, price = ?, archived_status = ? WHERE pass_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, skiPass.memberId());
            pstmt.setTimestamp(2, skiPass.purchaseDate());
            pstmt.setInt(3, skiPass.totalUses());
            pstmt.setInt(4, skiPass.remainingUses());
            pstmt.setString(5, skiPass.passType());
            pstmt.setInt(6, skiPass.price());
            pstmt.setInt(7, skiPass.archivedStatus());
            pstmt.setLong(8, skiPass.passId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ski pass failed, no rows affected. Pass ID: " + skiPass.passId());
            }
        }
    }

    public void deleteSkiPass(long passId) throws SQLException {
        String sql = "DELETE FROM SkiPass WHERE pass_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, passId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting ski pass failed, no rows affected. Pass ID: " + passId);
            }
        }
    }

    public int getDaysSincePurchase(long passId) throws SQLException {
        String sql = "SELECT TRUNC(SYSDATE - purchase_date, 0) AS time_diff FROM SkiPass WHERE pass_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, passId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("time_diff");
            } else {
                throw new SQLException("Pass not found for calculating days since purchase, ID: " + passId);
            }
        }
    }
}