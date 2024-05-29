package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database utilities.
 */
public class DbUtils {
    /**
     * Checks if a game exists.
     *
     * @param gameId the game id
     * @return true if the game exists, false otherwise
     */
    public static boolean doesGameExist(GameId gameId) throws SQLException, ClassNotFoundException, NullEnvironmentVariableException {
        Connection connection = DBConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Games WHERE id = ?");
        statement.setInt(1, Integer.parseInt(String.valueOf(gameId.gameId())));
        ResultSet rs = statement.executeQuery();
        boolean is = rs.next();
        connection.close();
        return is;
    }
}