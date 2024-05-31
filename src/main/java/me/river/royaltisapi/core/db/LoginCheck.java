package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.managers.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides methods for checking user login credentials and login tokens.
 */
public class LoginCheck {
    private static final Logger logger = LoggerFactory.getLogger(LoginCheck.class);

    /**
     * Checks the login credentials (username and password) of a user.
     *
     * @param user The user object containing the username and password.
     * @return true if the login is valid, false otherwise.
     * @throws SQLException                  If a database access error occurs.
     * @throws NullEnvironmentVariableException If a required environment variable is null.
     * @throws ClassNotFoundException         If the JDBC driver class cannot be found.
     */
    public static boolean checkLogin(User user) throws SQLException, NullEnvironmentVariableException, ClassNotFoundException {
        logger.info("Initiating login check for username: {}", user.getUsername());
        try (Connection connection = DBConnector.getConnection()) {
            logger.debug("Executing SQL query: SELECT password FROM Users WHERE username = {}", user.getUsername());
            String query = "SELECT password FROM Users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, TokenManager.encrypt(user.getUsername()));
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        boolean isValid = rs.getString("password").equals(TokenManager.encrypt(user.getPassword()));
                        logger.info("Login check for {}: {}", user.getUsername(), isValid);
                        return isValid;
                    } else {
                        logger.warn("No user found with username: {}", user.getUsername());
                        return false;
                    }
                }
            }
        }
    }

    /**
     * Checks the validity of a login token.
     *
     * @param token The login token to check.
     * @return true if the token is valid, false otherwise.
     * @throws SQLException                  If a database access error occurs.
     * @throws NullEnvironmentVariableException If a required environment variable is null.
     * @throws ClassNotFoundException         If the JDBC driver class cannot be found.
     */
    public static boolean checkLoginToken(String token) throws SQLException, NullEnvironmentVariableException, ClassNotFoundException {
        logger.info("Initiating login check with token {}", token);
        User user = TokenManager.getUserFromToken(token);
        logger.debug("User extracted from token: {}", user.getUsername());
        return checkLogin(user);
    }
}
