package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionChecker {

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Load database properties
            Properties props = DBPropertyUtil.loadProperty();

            if (props == null) {
                System.err.println("Failed to load database properties. Check the file path and content.");
                return;
            }

            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            if (url == null || username == null) {
                System.err.println("Database URL or username not found in db.properties.");
                return;
            }

            // Attempt to establish a connection
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.err.println("Failed to establish a database connection.");
            }

        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
    }
}