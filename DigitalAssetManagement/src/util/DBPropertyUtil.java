package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBPropertyUtil {

    public static Properties loadProperty() {
        Properties props = new Properties();
        // Use ClassLoader to get the resource from the classpath
        try (InputStream is = DBPropertyUtil.class.getClassLoader().getResourceAsStream("util/db.properties")) {
            if (is == null) {
                System.err.println("Failed to find db.properties in the classpath.");
                return null; // Or throw an exception
            }
            props.load(is);
        } catch (IOException e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            e.printStackTrace();
        }
        return props;
    }
}