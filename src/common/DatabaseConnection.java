package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*+----------------------------------------------------------------------
 ||
 ||  Class DatabaseConnection 
 ||
 ||        Author:  Matthew W. Song (Copies from Class Example)
 ||                 Elias Zheng (close method)
 ||
 ||     Purpose:  Handles the establishment of a JDBC connection to the 
 ||               Oracle database using provided credentials. It loads
 ||               the Oracle driver and manages the connection object.
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  oracleURL (private final String) // Within constructor scope
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  DatabaseConnection(String username, String password)
 ||
 ||  Class Methods:  None // loadOracleDriver and getDatabaseConnection are private static helpers
 ||
 ||  Inst. Methods:  getConnection() -> Connection
 ||                  close() - > void
 ||                  // Private static methods below
 ||                  // loadOracleDriver() -> void
 ||                  // getDatabaseConnection(String, String, String) -> Connection
 ||
 ++-----------------------------------------------------------------------*/
public class DatabaseConnection {

    private Connection dbConnection;

    /*---------------------------------------------------------------------
    |  Constructor DatabaseConnection(String username, String password)
    |
    |  Purpose:  Initializes the database connection. It defines the Oracle
    |            URL, calls helper methods to load the Oracle JDBC driver,
    |            and establishes the actual connection to the database using
    |            the provided username and password. Exits on failure.
    |
    |  Pre-condition:  Valid Oracle username and password are provided.
    |                  Oracle JDBC driver is available in the classpath.
    |
    |  Post-condition: If successful, dbConnection instance variable holds
    |                  an active connection. If fails (driver load or connection),
    |                  prints an error message and exits the program.
    |
    |  Parameters:
    |      username -- The Oracle DBMS username.
    |      password -- The Oracle DBMS password.
    |
    |  Returns:  A new DatabaseConnection object with an active connection.
    *-------------------------------------------------------------------*/
    public DatabaseConnection(String username, String password) {

        final String oracleURL = // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.

        loadOracleDriver();

        // make a database connection to the user's
        // Oracle database

        dbConnection = getDatabaseConnection(oracleURL, username, password);
    }

    /*---------------------------------------------------------------------
    |  Method loadOracleDriver()
    |
    |  Purpose:  Loads the Oracle JDBC driver class (`oracle.jdbc.OracleDriver`)
    |            into the JVM using Class.forName(). Handles potential
    |            ClassNotFoundException by printing an error and exiting.
    |
    |  Pre-condition:  Oracle JDBC driver JAR is expected to be in the classpath.
    |
    |  Post-condition: The Oracle driver class is loaded and registered with
    |                  DriverManager, or the program has exited on error.
    |
    |  Parameters: None
    |
    |  Returns:  None (exits program on failure)
    *-------------------------------------------------------------------*/
    private static void loadOracleDriver() {
        try {

            Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);

        }
    }

    /*---------------------------------------------------------------------
    |  Method getDatabaseConnection() 
    |
    |  Purpose:  Attempts to establish a connection to the Oracle database
    |            using the provided URL and credentials via DriverManager.
    |            Handles potential SQLException by printing details and exiting.
    |
    |  Pre-condition:  Oracle JDBC driver is loaded. Valid oracleURL, username,
    |                  and password are provided.
    |
    |  Post-condition: An active database connection is established, or the
    |                  program has exited on error.
    |
    |  Parameters:
    |      oracleURL -- The JDBC URL string for the Oracle database.
    |      username  -- The Oracle DBMS username.
    |      password  -- The Oracle DBMS password.
    |
    |  Returns:  The established java.sql.Connection object, or program exits.
    *-------------------------------------------------------------------*/
    private static Connection getDatabaseConnection(final String oracleURL, String username, String password) {
        Connection dbConnection = null;

        try {
            dbConnection = DriverManager.getConnection(oracleURL, username, password);

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        return dbConnection;
    }

    /*---------------------------------------------------------------------
    |  Method getConnection()
    |
    |  Purpose:  Provides access to the established database connection object.
    |
    |  Pre-condition:  The constructor successfully established a connection
    |                  (dbConnection is not null).
    |
    |  Post-condition: None.
    |
    |  Parameters: None
    |
    |  Returns:  The java.sql.Connection object managed by this instance.        
    *-------------------------------------------------------------------*/
    public Connection getConnection() {
        return dbConnection;
    }

    /*---------------------------------------------------------------------
    |  Method close()
    |
    |  Purpose:  Closes database connection
    |
    |  Pre-condition:  The constructor successfully established a connection
    |                  (dbConnection is not null).
    |
    |  Post-condition: None.
    |
    |  Parameters: None
    |
    *-------------------------------------------------------------------*/
    public void close(){
        try {
            dbConnection.close();
        } catch(SQLException e){
            System.err.println("FATAL SQL EXCEPTION: DB connection refused to closed. Terminating program.");
            System.exit(-1);
        }
    }
}
