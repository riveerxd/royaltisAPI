package me.river.royaltisapi.core.db.transfer;

import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.MiddlePoint;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.records.GameDetails;
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
 * Retrieves game previews from the database.
 */
public class GamePreviewRetriever {
    private static final Logger logger = LoggerFactory.getLogger(GamePreviewRetriever.class);
    private Connection connection;

    /**
     * Constructs a GamePreviewRetriever and establishes a database connection.
     *
     * @throws ClassNotFoundException         If the JDBC driver class is not found.
     * @throws NullEnvironmentVariableException If a required environment variable is null.
     * @throws SQLException                  If a database access error occurs.
     */
    public GamePreviewRetriever() throws ClassNotFoundException, NullEnvironmentVariableException, SQLException {
        this.connection = DBConnector.getConnection();
        logger.info("Established database connection for retrieving game previews.");
    }

    /**
     * Retrieves game previews from the database.
     *
     * @return An array containing lists of game details, borders, lootboxes, and middle points.
     * @throws SQLException If a database access error occurs.
     */
    public Object[] retrievePreview() throws SQLException {
        logger.info("Retrieving game previews from the database.");
        ArrayList<GameDetails> gameDetailsList = retrieveGameDetails();
        logger.info("Retrieved {} game details.", gameDetailsList.size());

        ArrayList<Border> borderList = retrieveBorders();
        logger.info("Retrieved {} borders.", borderList.size());

        ArrayList<LootBox> lootBoxList = retrieveLootboxes();
        logger.info("Retrieved {} lootboxes.", lootBoxList.size());

        ArrayList<MiddlePoint> middlePointList = retrieveMiddlePoint();
        logger.info("Retrieved {} middle points.", middlePointList.size());

        connection.close();
        logger.info("Closed database connection.");
        return new Object[]{gameDetailsList, borderList, lootBoxList, middlePointList};
    }

    /**
     * Retrieves game details (ID and name) from the database.
     *
     * @return An ArrayList of GameDetails objects.
     * @throws SQLException If a database access error occurs.
     */
    private ArrayList<GameDetails> retrieveGameDetails() throws SQLException {
        logger.debug("Retrieving game details.");
        ArrayList<GameDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                int gameId = rs.getInt("id");
                String gameName = rs.getString("name");
                GameDetails gd = new GameDetails(gameId, gameName);
                list.add(gd);
            }
        }
        return list;
    }

    /**
     * Retrieves borders from the database.
     *
     * @return An ArrayList of Border objects.
     */
    private ArrayList<Border> retrieveBorders() {
        logger.debug("Retrieving borders.");
        try {
            String sql = "SELECT * FROM Borders";
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            logger.error("SQL error occurred while retrieving borders: {}", e.getMessage());
            throw new RuntimeException("SQL failed to retrieve borders from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves loot boxes from the database.
     *
     * @return An ArrayList of LootBox objects.
     */
    private ArrayList<LootBox> retrieveLootboxes() {
        logger.debug("Retrieving lootboxes.");
        try {
            String sql = "SELECT * FROM LootBoxes";
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            logger.error("SQL error occurred while retrieving lootboxes: {}", e.getMessage());
            throw new RuntimeException("SQL failed to retrieve lootboxes from db: " + e.getMessage());
        }
    }

    /**
     * Retrieves middle points from the database.
     *
     * @return An ArrayList of MiddlePoint objects.
     */
    private ArrayList<MiddlePoint> retrieveMiddlePoint() {
        logger.debug("Retrieving middle points.");
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
            logger.error("SQL error occurred while retrieving middle points: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
