package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.Lobby;
import me.river.royaltisapi.core.Rank;
import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.data.LobbyCode;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.db.LoginCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.util.HashSet;
import java.util.UUID;

@DependsOn("socketIOServer")
public class UserManager {
    private HashSet<User> users = new HashSet<>();
    @Autowired
    private SocketIOServer server;

    @Autowired
    private LobbyManager lobbyManager;

    private Gson gson = new Gson();


    public boolean removeUserBySessionId(UUID sessionId) {
        for (User user : users) {
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                return users.remove(user);
            }
        }
        throw new RuntimeException("User not found");
    }

    public User getUserBySessionId(UUID sessionId) {
        for (User user : users) {
            System.out.println("comparing "+user.getSocketSessionId()+" with "+sessionId);
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                return user;
            }
        }
        throw new RuntimeException("User not found");
    }

    public void handleClientConnect(SocketIOClient client) {
        BroadcastOperations broadcastOperations = server.getBroadcastOperations();
        final UUID sessionId = client.getSessionId();
        if (users.contains(new User(client))) {
            return;
        }
        try {
            String token = client.getHandshakeData().getHttpHeaders().get("Authorization");
            if (LoginCheck.checkLoginToken(token)) {
                System.out.println("Authorized admin " + sessionId);
                User user = TokenManager.getUserFromToken(token);
                user.setRank(Rank.ADMIN);
                user.setSocketSessionId(client.getSessionId());
                user.setClient(client);
                try {
                    users.add(user);
                    lobbyManager.connectToLobby(user, new LobbyCode(client.getHandshakeData().getHttpHeaders().get("X-LobbyCode")));
                } catch (RuntimeException re) {
                    client.disconnect();
                    System.out.println("Disconnected admin " + client.getSessionId() + ". lobby code invalid");
                } finally {
                    broadcastOperations.sendEvent("playerUpdate", gson.toJson(users));
                    client.sendEvent("removed_items", gson.toJson(lobbyManager.getLobbyByClient(client).getRemovedItems()));
                    System.out.println("sending "+gson.toJson(lobbyManager.getLobbyByClient(client).getRemovedItems())+" to "+client.getSessionId());


                }
            } else {
                System.out.println("Couldnt authorize admin " + client.getSessionId());
                client.disconnect();
            }
        } catch (Exception e) {
            try{
                LobbyCode lobbyCode = new LobbyCode(client.getHandshakeData().getHttpHeaders().get("X-LobbyCode"));
                User user = new User(client, Rank.USER);
                lobbyManager.connectToLobby(user, lobbyCode);
                users.add(user);
                System.out.println("Client connected " + client.getSessionId());
            }catch (Exception r) {
                client.disconnect();
                System.out.println("Disconnected user " + client.getSessionId() + " lobby code invalid: "+client.getHandshakeData().getHttpHeaders().get("X-LobbyCode"));
            }finally {
                broadcastOperations.sendEvent("playerUpdate", gson.toJson(users));
            }
        }
        System.out.println("all connected users: "+users);
    }
    public void handleClientDisconnect(SocketIOClient client) {
        try{
            lobbyManager.disconnectFromLobby(getUserBySessionId(client.getSessionId()));
            System.out.println(removeUserBySessionId(client.getSessionId()));
            System.out.println(users);
            System.out.println("User disconnected " + client.getSessionId().toString());
        }catch (Exception e){
            System.out.println("Error while disconnecting user "+client.getSessionId()+": "+e.getMessage());
    }
    }

    public void handleItemRemove(SocketIOClient client, Object data, AckRequest ack){
        try{
            System.out.println("data: "+ data);
            LootBox.Item item = gson.fromJson(data.toString(), LootBox.Item.class);

            Lobby lobby = lobbyManager.getLobbyByClient(client);
            lobby.addItem(item);
            for (User user : lobby.getOnlineUsers()){
                user.getClient().sendEvent("removed_items", gson.toJson(lobby.getRemovedItems()));
                System.out.println("sending "+gson.toJson(lobby.getRemovedItems())+" to "+user.getSocketSessionId());
            }
        } catch (Exception e){
            System.out.println("Error while removing item: "+e.getMessage());
            e.printStackTrace();
        }
    }
}