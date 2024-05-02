package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.Border;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.json.JsonParser;

import java.sql.*;
import java.util.ArrayList;

public class DataUploader {
    private Connection connection;
    private GameData gameData;

    private static final String INSERT_BORDERS_SQL = "INSERT INTO Borders (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOXES_SQL = "INSERT INTO LootBoxes (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
    private static final String INSERT_LOOTBOX_ITEMS_SQL = "INSERT INTO LootBoxItems (lootbox_id, name) VALUES (?, ?)";
    private static final String INSERT_MIDDLEPOINT = "INSERT INTO MiddlePoints (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";


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

    private void uploadBorders() throws RuntimeException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BORDERS_SQL)) {
            ArrayList<Border> borders = gameData.getBorders();
            for (Border curr : borders) {
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.getType());
                statement.setDouble(3, curr.getCoords().getLatitude());
                statement.setDouble(4, curr.getCoords().getLongitude());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to upload borders: " + e.getMessage());
        }
    }

    private void uploadLootboxes() throws RuntimeException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LOOTBOXES_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ArrayList<LootBox> lootboxes = gameData.getLootboxes();
            for (LootBox curr : lootboxes) {
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.getType());
                statement.setDouble(3, curr.getCoords().getLatitude());
                statement.setDouble(4, curr.getCoords().getLongitude());
                statement.addBatch();
            }
            statement.executeBatch();

            //Retrieve generated keys for lootboxes
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

    private void uploadMiddlePoint() {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_MIDDLEPOINT)) {
            statement.setInt(1, gameData.getGameID());
            statement.setString(2, "mapCenter");
            statement.setDouble(3, gameData.getMiddlePoint().getCoordinates().getLatitude());
            statement.setDouble(4, gameData.getMiddlePoint().getCoordinates().getLongitude());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}