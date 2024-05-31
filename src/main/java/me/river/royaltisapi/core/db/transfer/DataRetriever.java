package me.river.royaltisapi.core.db.transfer;

import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.db.DBConnector;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Retrieves game data from the database.
 */
public class DataRetriever {
    private static final Logger logger = LoggerFactory.getLogger(DataRetriever.class);
    private Connection connection;

    /**
     * Retrieves game data from the database for the specified game ID.
     *
     * @param gameId the ID of the game to retrieve
     * @return the retrieved game data
     * @throws SQLException                  if a SQL error occurs
     * @throws NullEnvironmentVariableException if an environment variable is null
     * @throws ClassNotFoundException         if the JDBC driver class cannot be found
     */
    public GameData retrieveGameData(int gameId) throws SQLException, NullEnvironmentVariableException, ClassNotFoundException {
        logger.info("Retrieving game data for game ID: {}", gameId);
        this.connection = DBConnector.getConnection();

        String sql = "SELECT * FROM Games WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, gameId);
        ResultSet rs = statement.executeQuery();
        GameData gameData = null;
        if (rs.next()) {
            String gameName = rs.getString("name");
            logger.info("Game name retrieved: {}", gameName);

            ArrayList<Border> borders = retrieveBorders(gameId);
            logger.info("Retrieved {} borders for game ID: {}", borders.size(), gameId);

            ArrayList<LootBox> lootBoxes = retrieveLootboxes(gameId);
            logger.info("Retrieved {} lootboxes for game ID: {}", lootBoxes.size(), gameId);

            MiddlePoint middlePoint = retrieveMiddlePoint(gameId);
            logger.info("Retrieved middle point for game ID: {}", gameId);

            gameData = new GameData(borders, lootBoxes, gameName, middlePoint);
            gameData.setGameID(gameId);
        } else {
            logger.warn("No game data found for game ID: {}", gameId);
        }
        connection.close();
        logger.info("Closing database connection");

        return gameData;
    }

    /**
     * Retrieves borders for the specified game ID from the database.
     *
     * @param gameId the ID of the game to retrieve borders for
     * @return a list of retrieved borders
     */
    private ArrayList<Border> retrieveBorders(int gameId) {
        logger.debug("Retrieving borders for game ID: {}", gameId);
        try {
            String sql = "SELECT * FROM Borders WHERE game_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            ArrayList<Border> borders = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                Border border = new Border(id, type, new Coordinates(latitude, longitude));
                borders.add(border);
            }
            return borders;
        } catch (SQLException e) {
            logger.error("SQL error retrieving borders for game ID {}: {}", gameId, e.getMessage());
            throw new RuntimeException("SQL failed to retrieve borders from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves loot boxes for the specified game ID from the database.
     *
     * @param gameId the ID of the game to retrieve loot boxes for
     * @return a list of retrieved loot boxes
     */
    private ArrayList<LootBox> retrieveLootboxes(int gameId) {
        logger.debug("Retrieving lootboxes for game ID: {}", gameId);
        try {
            String sql = "SELECT * FROM LootBoxes WHERE game_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox> lootBoxes = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                ArrayList<LootBox.Item> items = retrieveLootboxItems(id);
                LootBox lootBox = new LootBox(id, type, new Coordinates(latitude, longitude), items);
                lootBoxes.add(lootBox);
            }
            return lootBoxes;
        } catch (SQLException e) {
            logger.error("SQL error retrieving lootboxes for game ID {}: {}", gameId, e.getMessage());
            throw new RuntimeException("SQL failed to retrieve lootboxes from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves items for the specified loot box ID from the database.
     *
     * @param lootboxId the ID of the loot box to retrieve items for
     * @return a list of retrieved items
     */
    private ArrayList<LootBox.Item> retrieveLootboxItems(int lootboxId) {
        logger.debug("Retrieving items for lootbox ID: {}", lootboxId);
        try {
            String sql = "SELECT * FROM LootBoxItems WHERE lootbox_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, lootboxId);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox.Item> items = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                LootBox.Item item = new LootBox.Item(name, id);
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            logger.error("SQL error retrieving items for lootbox ID {}: {}", lootboxId, e.getMessage());
            throw new RuntimeException("SQL failed to retrieve lootbox items from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves the middle point for the specified game ID from the database.
     *
     * @param gameId the ID of the game to retrieve the middle point for
     * @return the retrieved middle point
     */
    private MiddlePoint retrieveMiddlePoint(int gameId) {
        logger.debug("Retrieving middle point for game ID: {}", gameId);
        String sql = "SELECT * FROM MiddlePoints WHERE game_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new MiddlePoint(new Coordinates(rs.getDouble("coords_latitude"), rs.getDouble("coords_longitude")));
            } else {
                logger.warn("No middle point found for game ID: {}", gameId);
                throw new RuntimeException("Middlepoint retriever retrieved empty result set");
            }
        } catch (SQLException e) {
            logger.error("SQL error retrieving middle point for game ID {}: {}", gameId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
