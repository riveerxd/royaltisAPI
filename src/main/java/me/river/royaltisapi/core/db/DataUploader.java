package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;

import java.sql.*;
import java.util.ArrayList;

public class DataUploader {
    /**
     * The database connection.
     */
    private Connection connection;

    /**
     * The game data.
     */
    private GameData gameData;

    // Constants
    private static final String INSERT_BORDERS_SQL = "INSERT INTO Borders (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOXES_SQL = "INSERT INTO LootBoxes (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOX_ITEMS_SQL = "INSERT INTO LootBoxItems (lootbox_id, name) VALUES (?, ?)";
    private static final String INSERT_MIDDLEPOINT = "INSERT INTO MiddlePoints (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";

    /**
     * Creates a connection to the database and tries to upload the game data.
     */
    public Integer uploadGameData(GameData gameData) throws RuntimeException {
        try {
            this.connection = DBConnector.getConnection();
            this.gameData = gameData;
            Integer gameUploaded = uploadGame(this.gameData.getGameName());
            if (gameUploaded != null){
                uploadBorders();
                uploadLootboxes();
                uploadLootboxItems();
                uploadMiddlePoint();
                connection.close();
                return gameUploaded;
            }else{
                return null;
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Uploads a game to the database.
     * @param gameName The name of the game to upload.
     * @return The ID of the game that was uploaded.
     * @throws SQLException If the SQL query fails.
     */
    private int uploadGame(String gameName) throws SQLException {
        String sql = "INSERT INTO Games (name) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, gameName);
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int gameId = generatedKeys.getInt(1);
            this.gameData.setGameID(gameId);
            statement.close();
            generatedKeys.close();
            return gameId;
        } else {
            throw new SQLException("Failed to retrieve generated game ID");
        }


    }

    /**
     * Uploads the borders to the database.
     * @throws RuntimeException If the SQL query fails.
     */
    private void uploadBorders() throws RuntimeException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BORDERS_SQL)) {
            ArrayList<Border> borders = gameData.getBorders();
            for (Border curr : borders) {
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.type());
                statement.setDouble(3, curr.coords().longitude());
                statement.setDouble(4, curr.coords().longitude());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to upload borders: " + e.getMessage());
        }
    }

    /**
     * Uploads the lootboxes to the database.
     * @throws RuntimeException If the SQL query fails.
     */
    private void uploadLootboxes() throws RuntimeException {
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
                    throw new SQLException("Failed to retrieve generated lootbox ID for index: " + i);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to upload lootboxes: " + e.getMessage());
        }
    }

    /**
     * Uploads the lootbox items to the database.
     * @throws RuntimeException If the SQL query fails.
     */
    private void uploadLootboxItems() throws RuntimeException {
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
            throw new RuntimeException("SQL failed to upload lootbox items: " + e.getMessage());
        }
    }

    /**
     * Uploads the middle point to the database.
     */
    private void uploadMiddlePoint() {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_MIDDLEPOINT)) {
            statement.setInt(1, gameData.getGameID());
            statement.setString(2, "mapCenter");
            statement.setDouble(3, gameData.getMiddlePoint().getCoordinates().latitude());
            statement.setDouble(4, gameData.getMiddlePoint().getCoordinates().latitude());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}