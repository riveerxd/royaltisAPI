package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.managers.TokenManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Checks if a user can log in.
 */
public class LoginCheck {
    /**
     * Checks user credentials with the database.
     *
     * @param user the user
     * @return true if the user can log in, false otherwise
     */
    public static boolean checkLogin(User user) throws SQLException, NullEnvironmentVariableException, ClassNotFoundException {
        String username = user.getUsername();
        String password = user.getPassword();

        Connection connection = DBConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT password FROM Users WHERE username = ?");
        statement.setString(1, TokenManager.encrypt(username));
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            boolean is = rs.getString("password").equals(TokenManager.encrypt(password));
            connection.close();
            return is;
        } else {
            connection.close();
            return false;
        }
    }

    /**
     * Checks if a user can log in with a token.
     *
     * @param token the token
     * @return true if the user can log in, false otherwise
     */
    public static boolean checkLoginToken(String token) throws SQLException, NullEnvironmentVariableException, ClassNotFoundException {
        User user = TokenManager.getUserFromToken(token);
        return checkLogin(user);
    }
}
