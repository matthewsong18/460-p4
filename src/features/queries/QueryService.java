/*
 * File: QueryService
 * 
 * Author: Elias Zheng
 * 
 * Description: Encompasses API and database services for executing prepared queries on the database.
 * 
 */

package features.queries;
import java.sql.*;
import common.*;

public class QueryService {
    private static DatabaseConnection conn;
    private static Statement curStmt;
    
    public static void setConnection(DatabaseConnection connection){
        conn = connection;
        curStmt = null;
    }

    private static ResultSet executeQuery(String query){
        /*
         * Utility method, executes a query and closes the previously associated statement
         * if it exists. This implies that only one query can be actively executed and processed at a time.
         * 
         * Pre: query has valid SQL syntax
         * 
         * Parameters:
         *  - query: The full SQL query to execute (must be pre-formatted)
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         * 
         */
        try{
            Statement stmt = conn.getConnection().createStatement();
            
            if (curStmt != null) {
                curStmt.close();
            }

            curStmt = stmt;
            return stmt.executeQuery(query);

        } catch (SQLException e){
            System.err.println("FATAL: Exception occurred while attempting to execute query.");
            return null;
        }
    }

    public static ResultSet queryLessonHistory(String memberid){
        /*
         * Queries lesson order history and associated lesson information for a given member.
         * 
         * Parameters:
         *  - memberid: ID of the member to query lesson history for.
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                        select *
                        from stephintomson.LessonOrder
                        join stephintomson.Lesson using (lesson_id)
                        where member_id = '%s'
                       """, memberid);
        
        return executeQuery(query);
    }

    public static ResultSet queryPassRentals(String passid){
        /*
         * Queries equipment rentals associated with a certain pass
         * 
         * Parameters:
         *  - passid: id of the pass to search with
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                       select rental_id, equipment_id, datetime_to, datetime_from, returned
                       from stephintomson.Rental
                       where pass_id = %s and archived_status != 0
                """, passid);
        
        return executeQuery(query);
    }

    public static ResultSet queryPassLogs(String passid){
        /*
         * Queries entry logs associated with a certain pass
         * 
         * Parameters:
         *  - passid: id of the pass to search with
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                       select datetime, lift_name, difficulty_level
                       from stephintomson.EntryLog
                       join stephintomson.Lift using (lift_id)
                       where pass_id = %s
                """, passid);
        
        return executeQuery(query);
    }

    public static ResultSet queryIntTrails(){
        /*
         * Finds all trails that are of the intermediate difficulty.
         * 
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                        select *
                        from stephintomson.Trail
                        where difficulty = 2       
                    """);

        return executeQuery(query);
    }

    public static ResultSet queryLifts(String trailid){
        /*
         * Finds all lifts associated with the given trail
         * 
         * Parameters:
         *  - trailid: id of the trail to search with
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                        select lift_name, open_time, close_time, difficulty_level, location
                        from stephintomson.LiftTrail
                        join stephintomson.Lift using (lift_id)
                        where trail_id = %s  
                    """, trailid);

        return executeQuery(query);
    }

    public static ResultSet queryWageExpense(String propertyid){
        /*
         * Finds the wage expense of a given property
         * 
         * Paremeters:
         *  - propertyid: Property to search wage expense for
         * 
         * Return: ResultSet with DB response, null if exception occurred.
         */

        String query = String.format("""
                        select property_id, sum(salary) as "Wage Expense"
                        from stephintomson.Employee
                        join stephintomson.Property using (property_id)
                        where property_id = %s
                        group by property_id
                """, propertyid);

        return executeQuery(query);
    }

    public static void close(){
        /*
         * Closes the current open statement.
         * 
         * Necessary when you are not planning to execute additional statements.
         * 
         */

        try{
            if (curStmt != null) {
                curStmt.close();
            }
        } catch (SQLException e){
            System.err.println("FATAL: Exception occurred when closing the current statement.");
            return;
        }
       
    }
}
