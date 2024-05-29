package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.MiddlePoint;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.records.GameDetails;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Retrieves game previews from the database.
 */
public class GamePreviewRetriever {
    /**
     * The database connection.
     */
    private Connection connection;

    /**
     * Creates a new GamePreviewRetriever.
     */
    public GamePreviewRetriever() throws RuntimeException, ClassNotFoundException, NullEnvironmentVariableException, SQLException {
        this.connection = DBConnector.getConnection();
    }

    /**
     * Retrieves a preview of the game.
     *
     * @return An array containing the game details, borders, lootboxes, and middle points.
     */
    public Object[] retrievePreview() throws SQLException {
        ArrayList<GameDetails> gdl = retrieveGameDetails();
        ArrayList<Border> bl = retrieveBorders();
        ArrayList<LootBox> lbl = retrieveLootboxes();
        ArrayList<MiddlePoint> mpl = retrieveMiddlePoint();
        connection.close();
        return new Object[]{
                gdl, bl, lbl, mpl
        };
    }

    /**
     * Retrieves the game details.
     *
     * @return A list of GameDetails objects.
     */
    private ArrayList<GameDetails> retrieveGameDetails() throws SQLException {
        ArrayList<GameDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        PreparedStatement st = connection.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            int gameId = rs.getInt("id");
            String gameName = rs.getString("name");
            GameDetails gd = new GameDetails(gameId, gameName);
            list.add(gd);
        }
        return list;
    }

    /**
     * Retrieves the borders.
     *
     * @return A list of Border objects.
     */
    private ArrayList<Border> retrieveBorders() {
        try {
            String sql = "SELECT * FROM Borders";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<Border> borders = new ArrayList<>();
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                Border border = new Border(id, type, new Coordinates(latitude, longitude), gameId);
                borders.add(border);
            }
            return borders;
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to retrieve borders from db: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve borders from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves the lootboxes.
     *
     * @return A list of LootBox objects.
     */
    private ArrayList<LootBox> retrieveLootboxes() {
        try {
            String sql = "SELECT * FROM LootBoxes";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<LootBox> lootBoxes = new ArrayList<>();
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double latitude = rs.getDouble("coords_latitude");
                double longitude = rs.getDouble("coords_longitude");
                LootBox lootBox = new LootBox(id, type, new Coordinates(latitude, longitude), new ArrayList<>(), gameId);
                lootBoxes.add(lootBox);
            }
            return lootBoxes;
        } catch (SQLException e) {
            throw new RuntimeException("SQL failed to retrieve lootboxes from db: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve lootboxes from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves the middle points.
     *
     * @return A list of MiddlePoint objects.
     */
    private ArrayList<MiddlePoint> retrieveMiddlePoint() {
        ArrayList<MiddlePoint> middlePoints = new ArrayList<>();
        String sql = "SELECT * FROM MiddlePoints";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
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