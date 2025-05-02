package features.member_management;

import common.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberDatabaseAccessTest {

    private Connection connection;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");
        DatabaseConnection databaseConnection = new DatabaseConnection(username, password);
        this.connection = databaseConnection.getConnection();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    void findMemberById() {
        MemberDatabaseAccess memberDatabaseAccess = new MemberDatabaseAccess(connection);
        fail();
    }
}