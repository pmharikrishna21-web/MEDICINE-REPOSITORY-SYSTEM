package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized JDBC Database Connection Manager.
 * Configures the MySQL connection string, credentials, and establishes connections
 * using standard JDBC driver practices.
 */
public class DatabaseConnection {

    // Default configuration for standard local MySQL instance
    private static String dbUrl = System.getenv("DB_URL") != null 
            ? System.getenv("DB_URL") 
            : "jdbc:mysql://localhost:3306/medicine_repository?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    private static String dbUser = System.getenv("DB_USER") != null 
            ? System.getenv("DB_USER") 
            : "root";
            
    private static String dbPassword = System.getenv("DB_PASSWORD") != null 
            ? System.getenv("DB_PASSWORD") 
            : "root";

    static {
        try {
            // Explicitly load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL Connector/J driver class not found in classpath: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
        // Private constructor to prevent direct instantiation
    }

    /**
     * Obtains a new active connection to the MySQL medicine_repository database.
     * Callers must close the returned connection via try-with-resources.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Tests whether the database connection can be established.
     *
     * @return true if connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Configuration getters & setters for custom environments
    public static String getDbUrl() {
        return dbUrl;
    }

    public static void setDbUrl(String url) {
        dbUrl = url;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static void setDbUser(String user) {
        dbUser = user;
    }

    public static void setDbPassword(String password) {
        dbPassword = password;
    }
}
