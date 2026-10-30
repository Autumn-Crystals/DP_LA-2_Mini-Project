package com.campus.complaints.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to the MySQL database.
 *
 * <p>Credentials are loaded from a {@code .env} file (via dotenv-java) in
 * the working directory. If the file is absent, environment variables are
 * used as a fallback. This approach avoids hard-coding sensitive credentials.
 *
 * <p>For production use, replace with a connection pool (HikariCP).
 * For this lab project, a simple per-request connection suffices.
 */
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String host;
    private static final String port;
    private static final String dbName;
    private static final String user;
    private static final String password;

    static {
        Dotenv dotenv;
        try {
            dotenv = Dotenv.configure()
                           .directory("./")
                           .ignoreIfMissing()
                           .load();
        } catch (DotenvException e) {
            log.warn("Could not load .env file: {}", e.getMessage());
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        }

        host     = getEnv(dotenv, "DB_HOST",     "localhost");
        port     = getEnv(dotenv, "DB_PORT",     "3306");
        dbName   = getEnv(dotenv, "DB_NAME",     "campus_complaint_db");
        user     = getEnv(dotenv, "DB_USER",     "root");
        password = getEnv(dotenv, "DB_PASSWORD", "");
    }

    private static String getEnv(Dotenv dotenv, String key, String defaultValue) {
        String val = dotenv.get(key);
        if (val == null || val.isBlank()) {
            val = System.getenv(key);
        }
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    /**
     * Returns a new JDBC connection to the campus database.
     * Caller is responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=Asia/Calcutta" +
                "&allowPublicKeyRetrieval=true&characterEncoding=UTF-8",
                host, port, dbName);
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error("Failed to connect to database at {}:{}/{} as user '{}'",
                      host, port, dbName, user);
            throw e;
        }
    }

    /**
     * Tests whether a database connection can be established.
     * Returns a descriptive error message on failure, or null on success.
     */
    public static String testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return null; // success
            }
            return "Connection returned null or was immediately closed.";
        } catch (SQLException e) {
            return "Database connection failed: " + e.getMessage();
        }
    }

    public static String getDbName() { return dbName; }
    public static String getHost()   { return host; }
    public static String getPort()   { return port; }
}
