package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.data.GameId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtils {
    public static boolean doesGameExist(GameId gameId){
        try {
            Connection connection = DBConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Games WHERE id = ?");
            statement.setInt(1, Integer.parseInt(String.valueOf(gameId.gameId())));
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}