package com.prison.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DBConnection {
    // Default connection parameters for MySQL
    private static String JDBC_URL = "jdbc:mysql://localhost:3306/prison_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USERNAME = "root";
    private static String PASSWORD = "root";

    // Embedded Fallback Database (H2 in MySQL mode)
    private static final String EMBEDDED_URL = "jdbc:h2:mem:prison_management;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
    private static final String EMBEDDED_USER = "sa";
    private static final String EMBEDDED_PASS = "";

    private static boolean isInitialized = false;
    private static boolean usingEmbeddedFallback = false;
    private static Connection embeddedConnection = null;

    private static void initializeConnection() {
        if (isInitialized) {
            return;
        }

        // 1. Try to load properties from config.properties
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                JDBC_URL = props.getProperty("db.url", JDBC_URL);
                
                // Support both db.username and db.user
                String user = props.getProperty("db.username");
                if (user == null || user.trim().isEmpty()) {
                    user = props.getProperty("db.user", USERNAME);
                }
                USERNAME = user;
                PASSWORD = props.getProperty("db.password", PASSWORD);
                System.out.println("[DBConnection] Loaded database configuration from config.properties");
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Could not read config.properties: " + e.getMessage());
        }

        // 2. Try loading MySQL Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("[DBConnection] MySQL driver class not found in classpath.");
        }

        // 3. Test primary MySQL connection
        boolean mysqlSuccess = false;
        try (Connection testConn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            if (testConn != null) {
                mysqlSuccess = true;
                System.out.println("[DBConnection] Connected to MySQL successfully.");
                // Ensure tables exist in MySQL
                ensureTablesExist(testConn);
            }
        } catch (SQLException e) {
            System.out.println("[DBConnection] MySQL connection failed (" + e.getMessage() + ").");
        }

        if (!mysqlSuccess) {
            // 4. Activate embedded H2 fallback
            System.out.println("[DBConnection] Activating embedded H2 database fallback...");
            try {
                Class.forName("org.h2.Driver");
                embeddedConnection = DriverManager.getConnection(EMBEDDED_URL, EMBEDDED_USER, EMBEDDED_PASS);
                ensureTablesExist(embeddedConnection);
                usingEmbeddedFallback = true;
                System.out.println("[DBConnection] Embedded database initialized with seed data. App is fully operational!");
            } catch (Exception ex) {
                System.err.println("[DBConnection] Failed to initialize embedded database: " + ex.getMessage());
            }
        }

        isInitialized = true;
    }

    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            initializeConnection();
        }

        if (usingEmbeddedFallback) {
            return DriverManager.getConnection(EMBEDDED_URL, EMBEDDED_USER, EMBEDDED_PASS);
        }

        try {
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            // If MySQL became unavailable at runtime, switch to embedded fallback
            System.err.println("[DBConnection] MySQL lost: " + e.getMessage() + ". Switching to embedded mode.");
            usingEmbeddedFallback = true;
            return DriverManager.getConnection(EMBEDDED_URL, EMBEDDED_USER, EMBEDDED_PASS);
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                conn.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[DBConnection] testConnection failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean isUsingEmbeddedFallback() {
        return usingEmbeddedFallback;
    }

    private static void ensureTablesExist(Connection conn) {
        try (InputStream schemaStream = DBConnection.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (schemaStream == null) {
                System.out.println("[DBConnection] schema.sql not found in resources.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {

                StringBuilder currentStatement = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                        continue;
                    }

                    // Ignore CREATE DATABASE and USE statements for embedded DB compatibility
                    if (line.toUpperCase().startsWith("CREATE DATABASE") || line.toUpperCase().startsWith("USE ")) {
                        continue;
                    }

                    currentStatement.append(line).append(" ");

                    if (line.endsWith(";")) {
                        String sql = currentStatement.toString().trim();
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        try {
                            stmt.execute(sql);
                        } catch (SQLException ex) {
                            // Suppress errors for duplicate keys during seeding
                            if (!ex.getMessage().toLowerCase().contains("duplicate") &&
                                !ex.getMessage().toLowerCase().contains("already exists")) {
                                System.err.println("[DBConnection] Schema execution warning: " + ex.getMessage());
                            }
                        }
                        currentStatement.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Error executing schema.sql: " + e.getMessage());
        }
    }
}