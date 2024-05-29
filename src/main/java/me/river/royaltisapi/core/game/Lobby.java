package me.river.royaltisapi.core.game;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.UserNotFoundException;

import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a lobby.
 */
public class Lobby {
    /**
     * The lobby code.
     */
    private LobbyCode lobbyCode;
    /**
     * The game id.
     */
    private GameId gameId;
    /**
     * The online users.
     */
    private HashSet<User> onlineUsers = new HashSet<>();
    /**
     * The removed items.
     */
    private HashSet<LootBox.Item> removedItems = new HashSet<>();

    /**
     * Add item to removed items.
     *
     * @param item the item
     * @return the boolean
     */
    public boolean addItem(LootBox.Item item) {
        return removedItems.add(item);
    }

    /**
     * Connects a user.
     *
     * @param user the user
     * @return connected or not
     */
    public boolean connectUser(User user) {
        onlineUsers.add(user);
        System.out.println(user.isAdmin() ? "Admin " : "User " + user.getSocketSessionId() + " connected to lobby " + lobbyCode);
        return true;
    }

    /**
     * Disconnects a user.
     *
     * @param user the user
     * @return disconnected or not
     */
    public boolean disconnectUser(User user) {
        return onlineUsers.remove(user);
    }


    /**
     * Disconnects all users
     * @return disconnected or not
     */
    public boolean disconnectAllUsers() {
        try{
            for (User user : onlineUsers) {
                user.getClient().disconnect();
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Gets online users.
     *
     * @return the online users
     */
    public HashSet<User> getOnlineUsers() {
        return onlineUsers;
    }

    /**
     * Returns online user based in client object
     *
     * @param client
     * @return user
     */
    public User getOnlineUserByClient(SocketIOClient client) throws UserNotFoundException {
        for (User user : onlineUsers) {
            if (user.getClient().getSessionId().toString().equals(client.getSessionId().toString())) {
                return user;
            }
        }
        throw new UserNotFoundException("User not found");
    }

    /**
     * Gets removed items.
     *
     * @return the removed items
     */
    public HashSet<LootBox.Item> getRemovedItems() {
        return removedItems;
    }

    /**
     * Lobby equals.
     * @param o the object
     * @return equals or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(lobbyCode, lobby.lobbyCode);
    }

    /**
     * Sets lobby code.
     *
     * @param lobbyCode the lobby code
     */
    public void setLobbyCode(LobbyCode lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(lobbyCode);
    }

    /**
     * Instantiates a new Lobby.
     *
     * @param lobbyCode the lobby code
     * @param gameId    the game id
     */
    public Lobby(LobbyCode lobbyCode, GameId gameId) {
        this.lobbyCode = lobbyCode;
        this.gameId = gameId;
    }

    /**
     * Gets lobby code.
     *
     * @return the lobby code
     */
    public LobbyCode getLobbyCode() {
        return lobbyCode;
    }

    /**
     * Gets game id.
     *
     * @return the game id
     */
    public GameId getGameId() {
        return gameId;
    }

    /**
     * Lobby to string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Lobby{" +
                "lobbyCode=" + lobbyCode +
                ", gameId=" + gameId +
                '}';
    }
}