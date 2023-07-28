package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    static {
        driverLoader();
    }

    private static final String URL = "db.url";
    private static final String USER = "db.user";
    private static final String PASS = "db.pass";
    private static final Properties PROPERTIES = new Properties();

    private Util() {
    }

    public static Connection open() {
        loadProperties();
        try {
            return DriverManager.getConnection(
                    PROPERTIES.getProperty(URL),
                    PROPERTIES.getProperty(USER),
                    PROPERTIES.getProperty(PASS));
        } catch (SQLException e) {
            throw new RuntimeException("Connection error");
        }
    }

    private static void loadProperties() {
        try (InputStream inputStream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Properties read error");
        }
    }

    private static void driverLoader() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver load error");
        }
    }
}
