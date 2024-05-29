package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.data.Rank;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.db.LoginCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.util.HashSet;
import java.util.UUID;

/**
 * The User manager.
 */
@DependsOn("socketIOServer")
public class UserManager {
    /**
     * The server.
     */
    @Autowired
    private SocketIOServer server;

    /**
     * The lobby manager.
     */
    @Autowired
    private LobbyManager lobbyManager;

    /**
     * The users.
     */
    private HashSet<User> users = new HashSet<>();

    /**
     * The Gson instance.
     */
    private Gson gson = new Gson();

    /**
     * Remove user by session id.
     * Removes the user based on the session id.
     *
     * @param sessionId the session id
     */
    public boolean removeUserBySessionId(UUID sessionId) throws UserNotFoundException {
        for (User user : users) {
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                return users.remove(user);
            }
        }
        throw new UserNotFoundException("User not found");
    }

    /**
     * Gets user by session id.
     * Returns the user based on the session id.
     *
     * @param sessionId the session id
     * @return the user by session id
     */
    public User getUserBySessionId(UUID sessionId) throws UserNotFoundException {
        for (User user : users) {
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                return user;
            }
        }
        throw new UserNotFoundException("User not found");
    }

    /**
     * Handle client disconnect.
     * Disconnects the client from the lobby based on the session id.
     *
     * @param client the client
     */
    public void handleClientDisconnect(SocketIOClient client) {
        try{
            lobbyManager.disconnectFromLobby(getUserBySessionId(client.getSessionId()));
            removeUserBySessionId(client.getSessionId());
            System.out.println("User disconnected " + client.getSessionId().toString());
        }catch (Exception e){
            System.out.println("Error while disconnecting user "+client.getSessionId()+": "+e.getMessage());
        }
    }

    /**
     * Handle item remove.
     * Parses the json data and removes the item from the lobby.
     *
     * @param client the client
     * @param data the data
     */
    public void handleItemRemove(SocketIOClient client, Object data){
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

    public void handleLocationUpdate(SocketIOClient client, Object data){
        try{
            System.out.println("data: "+data);

            Coordinates coords = gson.fromJson(data.toString(), Coordinates.class);

            Lobby lobby = lobbyManager.getLobbyByClient(client);
            lobby.getOnlineUserByClient(client).setCoordinates(coords);

            for (User user : lobby.getOnlineUsers()){
                user.getClient().sendEvent("user_locations", gson.toJson(lobby.getOnlineUsers()));
            }
        } catch (Exception e){
            System.err.println("Error while updating user location: "+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle client connect.
     * Checks if the client is authorized and connects them to the lobby either as an admin or a user, then sends the updated player list to all clients.
     *
     * @param client the client
     */
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
            }catch (Exception r) {
                client.disconnect();
                System.out.println("Disconnected user " + client.getSessionId() + " lobby code invalid: "+client.getHandshakeData().getHttpHeaders().get("X-LobbyCode"));
            }finally {
                broadcastOperations.sendEvent("playerUpdate", gson.toJson(users));
            }
        }
    }
}