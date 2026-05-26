package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class DBUtil {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/bug_tracker";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "Ram@2004";

    private static final String URL = getConfig("BUG_TRACKER_DB_URL", "db.url", DEFAULT_URL);
    private static final String USER = getConfig("BUG_TRACKER_DB_USER", "db.user", DEFAULT_USER);
    private static final String PASSWORD = getConfig("BUG_TRACKER_DB_PASSWORD", "db.password", DEFAULT_PASSWORD);

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found on classpath", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getConfig(String envKey, String propKey, String defaultValue) {
        return Optional.ofNullable(System.getenv(envKey))
                .filter(value -> !value.isBlank())
                .or(() -> Optional.ofNullable(System.getProperty(propKey)))
                .orElse(defaultValue);
    }
}
