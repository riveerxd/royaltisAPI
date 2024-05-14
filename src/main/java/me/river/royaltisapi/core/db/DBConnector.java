package me.river.royaltisapi.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.river.royaltisapi.core.data.AccessKeys.*;
/**
 * Connects to the database.
 */
public class DBConnector {
    /**
     * Gets a connection to the database.
     *
     * @return the connection
     * @throws SQLException the sql exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_CLASS);
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }
}
