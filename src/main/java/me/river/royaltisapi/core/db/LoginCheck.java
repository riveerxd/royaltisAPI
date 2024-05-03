package me.river.royaltisapi.core.db;

import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.managers.TokenManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginCheck {
    public static boolean checkLogin(User user){
        try {
            String username = user.getUsername();
            String password = user.getPassword();

            Connection connection = DBConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT password FROM Users WHERE username = ?");
            statement.setString(1, TokenManager.encrypt(username));
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                boolean is = rs.getString("password").equals(TokenManager.encrypt(password));
                connection.close();
                return is;
            }else{
                connection.close();
                throw new IllegalArgumentException("Empty result set");
            }
        } catch (SQLException e) {
            System.out.println("SQL error: "+e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("CNF error: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean checkLoginToken(String token){
        User user = TokenManager.getUserFromToken(token);
        return checkLogin(user);
    }
}
