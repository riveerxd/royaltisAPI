package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.AccessKeys;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public static Connection getConnection() throws SQLException, ClassNotFoundException, NullEnvironmentVariableException {
        AccessKeys accessKeys = new AccessKeys();
        Class.forName(accessKeys.getDRIVER_CLASS());
        return DriverManager.getConnection(accessKeys.getDATABASE_URL(), accessKeys.getUSERNAME(), accessKeys.getPASSWORD());
    }
}
