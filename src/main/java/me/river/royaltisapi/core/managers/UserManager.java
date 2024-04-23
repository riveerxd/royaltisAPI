package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.Rank;
import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.db.LoginCheck;

import java.util.HashSet;
import java.util.UUID;

public class UserManager {
    private HashSet<User> users = new HashSet<>();

    public boolean removeUserBySessionId(UUID sessionId){
        for (User user : users){
            if (user.getSocketSessionId().toString().equals(sessionId.toString())){
                return users.remove(user);
            }
        }
        return false;
    }

    public void handleClientConnect(SocketIOClient client){
        final UUID sessionId = client.getSessionId();
        if (users.contains(new User(sessionId))){
            return;
        }
        try {
            String token = client.getHandshakeData().getHttpHeaders().get("Authorization");
            if (LoginCheck.checkLoginToken(token)){
                System.out.println("Authorized admin "+sessionId);
                User user = TokenManager.getUserFromToken(token);
                user.setRank(Rank.ADMIN);
                user.setSocketSessionId(sessionId);
                users.add(user);
            }else{
                System.out.println("Couldnt authorize admin "+client.getSessionId());
                client.disconnect();
            }
        }catch (Exception e){
            System.out.println("Client connected "+client.getSessionId());
            User user = new User(client.getSessionId(), Rank.USER);
            users.add(user);
        }
        System.out.println(users);
    }

    public void handleClientDisconnect(SocketIOClient client){
        client.getNamespace().getAllClients().stream().forEach(data -> {
            System.out.println("User disconnected " + data.getSessionId().toString());
            System.out.println(removeUserBySessionId(data.getSessionId()));
        });
        System.out.println(users);
    }

}
