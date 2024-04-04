package me.river.royaltisapi.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.river.royaltisapi.core.data.AccessKeys.*;

public class DBConnector {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_CLASS);
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }
}
