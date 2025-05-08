/*+----------------------------------------------------------------------
 ||
 ||  Class DatabaseAccess
 ||
 ||        Author:  Ethan Huang
 ||                 Matthew Song
 ||
 ||     Purpose:    Common service for entity service classes to obtain the
 ||                 next integer to use as a primary key id.
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  DatabaseAccess(DatabaseConnection databaseConnection);
 ||
 ||  Class Methods:  N/A
 ||
 ||  Inst. Methods:  getNextID(String databaseName, String id_name) --> long
 ||
 ++-----------------------------------------------------------------------*/

package common;

import java.sql.*;

public abstract class DatabaseAccess {
    protected final Connection connection;
    protected final DatabaseConnection databaseConnection;

    protected DatabaseAccess(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.connection = databaseConnection.getConnection();
    }

//    protected void updateCurrentId() {
//        String sql = "SELECT max(?) FROM ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            if (InputValidation.checkStringBlank(tableName) || InputValidation.checkStringBlank(pkFieldName)) {
//                throw new SQLException("Table and PK field name are undefined");
//            }
//            preparedStatement.setString(1, pkFieldName);
//            preparedStatement.setString(2, tableName);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                currentId = resultSet.next() ? resultSet.getLong("max(" + pkFieldName + ")") : 0L;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected long getCurrentId() {
//        return ++currentId;
//    }

    protected Long getNextId(String databaseName, String id_name) throws SQLException {
        String sql = "SELECT MAX(" + id_name + ") AS max_id FROM stephintomson." + databaseName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long currentMax = resultSet.getLong("max_id");
                    return currentMax + 1;
                }
                return 1L;
            }
        }
    }
}
