package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.Border;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;

import java.sql.*;

public class DataUploader {
    private Connection connection;
    private GameData gameData;

    public boolean uploadGameData(GameData gameData) throws RuntimeException {
        try {
            this.connection = DBConnector.getConnection();
            this.gameData = gameData;
            boolean gameUploaded = uploadGame(this.gameData.getGameName());
            if (gameUploaded){
                uploadBorders();
                uploadLootboxes();
                uploadLootboxItems();
                connection.close();
                return true;
            }else{
                return false;
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean uploadGame(String gameName) throws SQLException {
        String sql = "INSERT INTO Games (name) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, gameName);
        int rowsAffected = statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int gameId = generatedKeys.getInt(1);
            this.gameData.setGameID(gameId);
        } else {
            throw new SQLException("Failed to retrieve generated game ID");
        }

        statement.close();
        generatedKeys.close();
        return rowsAffected > 0;
    }


    private void uploadBorders() throws RuntimeException {
        try {
            for (Border curr : gameData.getBorders()) {
                String sql = "INSERT INTO Borders (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.getType());
                statement.setDouble(3, curr.getCoords().getLatitude());
                statement.setDouble(4, curr.getCoords().getLongitude());
                statement.executeUpdate();
                statement.close();
            }
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to upload borders: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to upload borders: "+e.getMessage());
        }
    }

    private void uploadLootboxes() throws RuntimeException {
        try {
            for (LootBox curr : gameData.getLootboxes()) {
                String sql = "INSERT INTO LootBoxes (game_id, type, coords_latitude, coords_longitude) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, gameData.getGameID());
                statement.setString(2, curr.getType());
                statement.setDouble(3, curr.getCoords().getLatitude());
                statement.setDouble(4, curr.getCoords().getLongitude());
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lootboxId = generatedKeys.getInt(1);
                    curr.setMysqlID(lootboxId);
                } else {
                    throw new SQLException("Failed to retrieve generated lootbox mysql ID");
                }
                statement.close();
                generatedKeys.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to upload lootboxes: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload lootboxes: " + e.getMessage());
        }
    }

    private void uploadLootboxItems() throws RuntimeException {
        try {
            for (LootBox currLootbox : gameData.getLootboxes()) {
                for (LootBox.Item currItem : currLootbox.getItems()) {
                    String sql = "INSERT INTO LootBoxItems (lootbox_id, name) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, (int)currLootbox.getMysqlID());
                    statement.setString(2, currItem.getName());
                    statement.executeUpdate();
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to upload lootbox items: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload lootbox items: " + e.getMessage());
        }
    }
}
