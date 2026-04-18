package com.vis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // ✅ Supabase (Session Pooler - EU West)
    private static final String HOST = "aws-0-eu-west-1.pooler.supabase.com";
    private static final String PORT = "5432";
    private static final String DATABASE = "postgres";
    private static final String USER = "postgres.dvhflbcjjlmbpwmgupwb";
    private static final String PASSWORD = "Amohelang@123";

    // ✅ JDBC URL (SSL + Timeout)
    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE +
                    "?sslmode=require&connectTimeout=10&socketTimeout=10";

    // ✅ Singleton instance
    private static DatabaseManager instance;
    private Connection connection;

    // ✅ Private constructor
    private DatabaseManager() {
        connect();
    }

    // ✅ Get single instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ✅ Connect to database
    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("✅ Connected to Supabase PostgreSQL!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL Driver not found.");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }

    // ✅ Get connection (auto reconnect)
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Reconnecting to database...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking connection: " + e.getMessage());
        }
        return connection;
    }

    // ✅ Test connection
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection is active.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Test failed: " + e.getMessage());
        }
        return false;
    }

    // ✅ Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}