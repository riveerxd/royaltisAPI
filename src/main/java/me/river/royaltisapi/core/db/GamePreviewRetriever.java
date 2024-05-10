package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GamePreviewRetriever {
    private Connection connection;

    public GamePreviewRetriever() {
        try {
            this.connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] retrievePreview()  {
        ArrayList<GameDetails> gdl;
        ArrayList<Border> bl;
        ArrayList<LootBox> lbl;
        ArrayList<MiddlePoint> mpl;
        try {
            gdl = retrieveGameDetails();
            bl = retrieveBorders();
            lbl = retrieveLootboxes();
            mpl = retrieveMiddlePoint();
            connection.close();
        } catch (Exception e) {
            gdl = new ArrayList<>();
            bl = new ArrayList<>();
            lbl = new ArrayList<>();
            mpl = new ArrayList<>();
        }
        return new Object[]{
                gdl, bl, lbl, mpl
        };
    }

    private ArrayList<GameDetails> retrieveGameDetails(){
        ArrayList<GameDetails> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Games";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                int gameId = rs.getInt("id");
                String gameName = rs.getString("name");
                GameDetails gd = new GameDetails(gameId, gameName);
                list.add(gd);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private ArrayList<Border> retrieveBorders(){
        try {
            String sql = "SELECT * FROM Borders";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<Border> borders = new ArrayList<>();
            while(rs.next()){
                int gameId = rs.getInt("game_id");
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                Border border = new Border(id, type, new Coordinates(latitude, longitude), gameId);
                borders.add(border);
            }
            return borders;
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to retrieve borders from db: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to retrieve borders from db: "+e.getMessage());
        }
    }

    private ArrayList<LootBox> retrieveLootboxes(){
        try{
            String sql = "SELECT * FROM LootBoxes";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox> lootBoxes = new ArrayList<>();
            while(rs.next()){
                int gameId = rs.getInt("game_id");
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                LootBox lootBox = new LootBox(id, type, new Coordinates(latitude, longitude), new ArrayList<>(), gameId);
                lootBoxes.add(lootBox);
            }
            return lootBoxes;
        }catch (SQLException e){
            throw new RuntimeException("SQL failed to retrieve lootboxes from db: "+e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to retrieve lootboxes from db: "+e.getMessage());
        }
    }
    private ArrayList<MiddlePoint> retrieveMiddlePoint(){
        ArrayList<MiddlePoint> middlePoints = new ArrayList<>();
        String sql = "SELECT * FROM MiddlePoints";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                int gameId = rs.getInt("game_id");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                MiddlePoint middlePoint = new MiddlePoint(gameId, new Coordinates(latitude, longitude));
                middlePoints.add(middlePoint);
            }
            return middlePoints;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}