package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Configurable connection parameters. Defaulting to local postgres instance.
    private static final String URL = "jdbc:postgresql://localhost:5432/bug_tracker";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        try {
            // Load the PostgreSQL JDBC driver explicitly
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found on classpath", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
