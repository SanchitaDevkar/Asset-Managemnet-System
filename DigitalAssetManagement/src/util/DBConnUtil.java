package util; // Assuming DBConnUtil is in the util package

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {

    public static Connection getConnection() throws SQLException {
        Properties props = DBPropertyUtil.loadProperty();
        if (props == null) {
            throw new SQLException("Failed to load database properties.");
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        if (url == null || username == null) {
            throw new SQLException("Database URL or username not found.");
        }

        return DriverManager.getConnection(url, username, password);
    }
}