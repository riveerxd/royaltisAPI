package me.river.royaltisapi.core.db.transfer;

import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.db.DBConnector;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is responsible for uploading game data to the database.
 */
public class DataUploader {
    private static final Logger logger = LoggerFactory.getLogger(DataUploader.class);
    private static final String INSERT_BORDERS_SQL = "INSERT INTO Borders (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOXES_SQL = "INSERT INTO LootBoxes (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOX_ITEMS_SQL = "INSERT INTO LootBoxItems (lootbox_id, name) VALUES (?, ?)";
    private static final String INSERT_MIDDLEPOINT = "INSERT INTO MiddlePoints (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private Connection connection;
    private GameData gameData;

    /**
     * Uploads the provided game data to the database.
     *
     * @param gameData The game data to upload.
     * @return The ID of the uploaded game in the database.
     * @throws SQLException                  If a SQL error occurs during the upload process.
     * @throws ClassNotFoundException         If the JDBC driver class cannot be found.
     * @throws NullEnvironmentVariableException If a required environment variable is not set.
     */
    public Integer uploadGameData(GameData gameData) throws SQLException, ClassNotFoundException, NullEnvironmentVariableException {
        logger.info("Starting game data upload process.");
        this.connection = DBConnector.getConnection();
        this.gameData = gameData;

        logger.debug("Uploading game: {}", gameData.getGameName());
        Integer gameUploaded = uploadGame(this.gameData.getGameName());
        logger.info("Game uploaded with ID: {}", gameUploaded);

        logger.debug("Uploading borders...");
        uploadBorders();
        logger.info("Borders uploaded successfully.");

        logger.debug("Uploading lootboxes...");
        uploadLootboxes();
        logger.info("Lootboxes uploaded successfully.");

        logger.debug("Uploading lootbox items...");
        uploadLootboxItems();
        logger.info("Lootbox items uploaded successfully.");

        logger.debug("Uploading middle point...");
        uploadMiddlePoint();
        logger.info("Middle point uploaded successfully.");

        connection.close();
        logger.info("Database connection closed. Upload process complete.");
        return gameUploaded;
    }

    /**
     * Uploads a game with the specified name to the database.
     *
     * @param gameName The name of the game to upload.
     * @return The ID of the uploaded game in the database.
     * @throws SQLException If a SQL error occurs during the upload process.
     */
    private int uploadGame(String gameName) throws SQLException {
        logger.debug("Executing SQL query: INSERT INTO Games (name) VALUES (?)");
        String sql = "INSERT INTO Games (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameName);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameId = generatedKeys.getInt(1);
                    this.gameData.setGameID(gameId);
                    return gameId;
                } else {
                    logger.error("Failed to retrieve generated game ID");
                    throw new SQLException("Failed to retrieve generated game ID");
                }
            }
        }
    }

    /**
     * Uploads the borders associated with the current game data.
     */
    private void uploadBorders() {
        logger.debug("Executing SQL query: {}", INSERT_BORDERS_SQL);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BORDERS_SQL)) {
            ArrayList<Border> borders = gameData.getBorders();
            for (Border curr : borders) {
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.type());
                statement.setDouble(3, curr.coords().latitude());
                statement.setDouble(4, curr.coords().longitude());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("SQL error uploading borders: {}", e.getMessage());
            throw new RuntimeException("SQL failed to upload borders: " + e.getMessage());
        }
    }

    /**
     * Uploads the loot boxes associated with the current game data.
     */
    private void uploadLootboxes() {
        logger.debug("Executing SQL query: {}", INSERT_LOOTBOXES_SQL);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LOOTBOXES_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ArrayList<LootBox> lootboxes = gameData.getLootboxes();
            for (LootBox curr : lootboxes) {
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.getType());
                statement.setDouble(3, curr.getCoords().latitude());
                statement.setDouble(4, curr.getCoords().longitude());
                statement.addBatch();
            }
            statement.executeBatch();

            var generatedKeys = statement.getGeneratedKeys();
            for (int i = 0; i < lootboxes.size(); i++) {
                if (generatedKeys.next()) {
                    int lootboxId = generatedKeys.getInt(1);
                    lootboxes.get(i).setMysqlID(lootboxId);
                } else {
                    logger.error("Failed to retrieve generated lootbox ID for index: {}", i);
                    throw new SQLException("Failed to retrieve generated lootbox ID for index: " + i);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL error uploading lootboxes: {}", e.getMessage());
            throw new RuntimeException("SQL failed to upload lootboxes: " + e.getMessage());
        }
    }

    /**
     * Uploads the loot box items associated with the current game data.
     */
    private void uploadLootboxItems() {
        logger.debug("Executing SQL query: {}", INSERT_LOOTBOX_ITEMS_SQL);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LOOTBOX_ITEMS_SQL)) {
            ArrayList<LootBox> lootboxes = gameData.getLootboxes();
            for (LootBox currLootbox : lootboxes) {
                for (LootBox.Item currItem : currLootbox.getItems()) {
                    statement.setInt(1, currLootbox.getMysqlID());
                    statement.setString(2, currItem.getName());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("SQL error uploading lootbox items: {}", e.getMessage());
            throw new RuntimeException("SQL failed to upload lootbox items: " + e.getMessage());
        }
    }

    /**
     * Uploads the middle point associated with the current game data.
     */
    private void uploadMiddlePoint() {
        logger.debug("Executing SQL query: {}", INSERT_MIDDLEPOINT);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MIDDLEPOINT)) {
            statement.setInt(1, gameData.getGameID());
            statement.setString(2, "mapCenter");
            statement.setDouble(3, gameData.getMiddlePoint().getCoordinates().latitude());
            statement.setDouble(4, gameData.getMiddlePoint().getCoordinates().longitude());
            statement.execute();
        } catch (SQLException e) {
            logger.error("SQL error uploading middle point: {}", e.getMessage());
            throw new RuntimeException("SQL failed to upload middle point: " + e.getMessage());
        }
    }
}
