/**
 * @author Ethan Huang
 */

package common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseAccess {
    protected Connection connection;
    protected long currentId;
    protected String tableName;
    protected String pkFieldName;

    protected void updateCurrentId() {
        String sql = "SELECT max(?) FROM ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (InputValidation.checkStringBlank(tableName) || InputValidation.checkStringBlank(pkFieldName)) {
                throw new SQLException("Table and PK field name are undefined");
            }
            preparedStatement.setString(1, pkFieldName);
            preparedStatement.setString(2, tableName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                currentId = resultSet.next() ? resultSet.getLong("max(" + pkFieldName + ")") : 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected long getCurrentId() {
        return ++currentId;
    }
}
