package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataRetriever {
    private Connection connection;

    public GameData retrieveGameData(int gameId){
        try{
            this.connection = DBConnector.getConnection();

            String sql = "SELECT * FROM Games WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();
            GameData gameData = null;
            if (rs.next()){
                String gameName = rs.getString("name");
                ArrayList<Border> borders = retrieveBorders(gameId);
                ArrayList<LootBox> lootBoxes = retrieveLootboxes(gameId);
                MiddlePoint middlePoint = retrieveMiddlePoint(gameId);
                gameData = new GameData(borders, lootBoxes, gameName, middlePoint);
                gameData.setGameID(gameId);
            }
            connection.close();
            return gameData;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private ArrayList<Border> retrieveBorders(int gameId){
        try {
            String sql = "SELECT * FROM Borders WHERE game_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            ArrayList<Border> borders = new ArrayList<>();
            while(rs.next()){
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                Border border = new Border(id, type, new Coordinates(latitude, longitude));
                borders.add(border);
            }
            return borders;
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to retrieve borders from db: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to retrieve borders from db: "+e.getMessage());
        }
    }

    private ArrayList<LootBox> retrieveLootboxes(int gameId){
        try{
            String sql = "SELECT * FROM LootBoxes WHERE game_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox> lootBoxes = new ArrayList<>();
            while(rs.next()){
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                ArrayList<LootBox.Item> items = retrieveLootboxItems(id);
                LootBox lootBox = new LootBox(id, type, new Coordinates(latitude, longitude), items);
                lootBoxes.add(lootBox);
            }
            return lootBoxes;
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to retrieve lootboxes from db: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to retrieve lootboxes from db: "+e.getMessage());
        }
    }

    private ArrayList<LootBox.Item> retrieveLootboxItems(int lootboxId){
        try{
            String sql = "SELECT * FROM LootBoxItems WHERE lootbox_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, lootboxId);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox.Item> items = new ArrayList<>();
            while(rs.next()){
                String name = rs.getString("name");
                int id = rs.getInt("id");
                LootBox.Item item = new LootBox.Item(name, id);
                items.add(item);
            }
            return items;
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to retrieve lootbox items from db: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to retrieve lootbox items from db: "+e.getMessage());
        }
    }

    private MiddlePoint retrieveMiddlePoint(int gameId){
        String sql = "SELECT * FROM MiddlePoints WHERE game_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                return new MiddlePoint(new Coordinates(rs.getDouble("coords_latitude"), rs.getDouble("coords_longitude")));
            }else{
                throw new RuntimeException("Middlepoint retriever retrieved empty result set");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
