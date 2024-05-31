package me.river.royaltisapi.core.game;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a game lobby, which manages connections of online users and keeps track of game state.
 */
public class Lobby {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private LobbyCode lobbyCode;
    private GameId gameId;
    private HashSet<User> onlineUsers = new HashSet<>();
    private HashSet<LootBox.Item> removedItems = new HashSet<>();

    /**
     * Creates a new lobby for the specified game.
     * @param lobbyCode Unique code for the lobby.
     * @param gameId The ID of the game associated with this lobby.
     */
    public Lobby(LobbyCode lobbyCode, GameId gameId) {
        this.lobbyCode = lobbyCode;
        this.gameId = gameId;
        logger.info("Lobby {} created for game {}", lobbyCode.lobbyCode(), gameId.gameId());
    }

    /**
     * Adds an item to the list of removed items in the game.
     * @param item The item to be added.
     * @return true if the item was added successfully, false otherwise.
     */
    public boolean addItem(LootBox.Item item) {
        boolean added = removedItems.add(item);
        if (added) {
            logger.info("Item {} added to removed items for lobby {}", item.getName(), lobbyCode.lobbyCode());
        }
        return added;
    }

    /**
     * Connects a user to the lobby.
     * @param user The user to be connected.
     * @return true if the user was connected successfully, false otherwise.
     */
    public boolean connectUser(User user) {
        onlineUsers.add(user);
        logger.info("{} {} connected to lobby {}", user.isAdmin() ? "Admin" : "User", user.getSocketSessionId(), lobbyCode.lobbyCode());
        return true;
    }

    /**
     * Disconnects a user from the lobby.
     * @param user The user to be disconnected.
     * @return true if the user was disconnected successfully, false otherwise.
     */
    public boolean disconnectUser(User user) {
        boolean removed = onlineUsers.remove(user);
        if (removed) {
            logger.info("User {} disconnected from lobby {}", user.getSocketSessionId(), lobbyCode.lobbyCode());
        }
        return removed;
    }

    /**
     * Disconnects all users from the lobby.
     * @return true if all users were disconnected successfully, false otherwise.
     */
    public boolean disconnectAllUsers() {
        logger.info("Disconnecting all users from lobby {}", lobbyCode.lobbyCode());
        try {
            for (User user : onlineUsers) {
                logger.debug("Disconnecting user {} from lobby {}", user.getSocketSessionId(), lobbyCode.lobbyCode());
                user.getClient().disconnect();
            }
            return true;
        } catch (Exception e) {
            logger.error("Error disconnecting users from lobby {}: {}", lobbyCode.lobbyCode(), e.getMessage());
            return false;
        }
    }

    /**
     * @return The lobby code.
     */
    public LobbyCode getLobbyCode() {
        return lobbyCode;
    }

    /**
     * Sets the lobby code.
     * @param lobbyCode The new lobby code.
     */
    public void setLobbyCode(LobbyCode lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    /**
     * @return The ID of the game associated with this lobby.
     */
    public GameId getGameId() {
        return gameId;
    }

    /**
     * @return A set of all online users in the lobby.
     */
    public HashSet<User> getOnlineUsers() {
        return onlineUsers;
    }

    /**
     * Gets an online user by their socket client.
     * @param client The socket client of the user.
     * @return The user object if found.
     * @throws UserNotFoundException If the user is not found in the lobby.
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
     * @return A set of all removed items in the game.
     */
    public HashSet<LootBox.Item> getRemovedItems() {
        return removedItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(lobbyCode, lobby.lobbyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lobbyCode);
    }

    /**
     * Returns a string representation of the lobby.
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "Lobby{" +
                "lobbyCode=" + lobbyCode +
                ", gameId=" + gameId +
                '}';
    }
}
