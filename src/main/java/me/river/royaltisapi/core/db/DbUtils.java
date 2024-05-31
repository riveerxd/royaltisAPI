package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides utility functions for database operations.
 */
public class DbUtils {
    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    /**
     * Checks if a game with the given ID exists in the database.
     *
     * @param gameId The ID of the game to check.
     * @return True if the game exists, false otherwise.
     * @throws SQLException                  If a database access error occurs.
     * @throws ClassNotFoundException         If the JDBC driver class cannot be found.
     * @throws NullEnvironmentVariableException If a required environment variable is not set.
     */
    public static boolean doesGameExist(GameId gameId) throws SQLException, ClassNotFoundException, NullEnvironmentVariableException {
        logger.info("Checking if game with ID {} exists in database", gameId.gameId());
        try (Connection connection = DBConnector.getConnection()) {
            logger.debug("Executing SQL query: SELECT * FROM Games WHERE id = {}", gameId.gameId());
            String query = "SELECT * FROM Games WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Integer.parseInt(String.valueOf(gameId.gameId())));
                try (ResultSet rs = statement.executeQuery()) {
                    boolean exists = rs.next();
                    logger.info("Game with ID {} exists: {}", gameId.gameId(), exists);
                    return exists;
                }
            }
        }
    }
}
