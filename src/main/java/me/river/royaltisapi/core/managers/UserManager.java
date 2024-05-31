package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.Rank;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.exceptions.LobbyNotFoundException;
import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.game.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.util.HashSet;
import java.util.UUID;

/**
 * This class manages all users in the game. It handles user connections, disconnections,
 * location updates, and item removals.
 */
@DependsOn("socketIOServer")
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final HashSet<User> users = new HashSet<>();
    private final Gson gson = new Gson();

    @Autowired
    private SocketIOServer server;

    @Autowired
    private LobbyManager lobbyManager;

    /**
     * Removes a user from the system based on their session ID.
     *
     * @param sessionId The session ID of the user to remove.
     * @return true if the user was removed successfully, false otherwise.
     * @throws UserNotFoundException If the user with the given session ID is not found.
     */
    public boolean removeUserBySessionId(UUID sessionId) throws UserNotFoundException {
        logger.info("Attempting to remove user with session ID: {}", sessionId);
        for (User user : users) {
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                boolean removed = users.remove(user);
                if (removed) {
                    logger.info("User with session ID {} removed successfully.", sessionId);
                } else {
                    logger.warn("Failed to remove user with session ID {}.", sessionId);
                }
                return removed;
            }
        }
        logger.warn("User with session ID {} not found.", sessionId);
        throw new UserNotFoundException("User not found");
    }

    /**
     * Retrieves a user by their session ID.
     *
     * @param sessionId The session ID of the user to retrieve.
     * @return The User object if found.
     * @throws UserNotFoundException If the user with the given session ID is not found.
     */
    public User getUserBySessionId(UUID sessionId) throws UserNotFoundException {
        logger.info("Retrieving user with session ID: {}", sessionId);
        for (User user : users) {
            if (user.getSocketSessionId().toString().equals(sessionId.toString())) {
                logger.info("User with session ID {} found.", sessionId);
                return user;
            }
        }
        logger.warn("User with session ID {} not found.", sessionId);
        throw new UserNotFoundException("User not found");
    }

    /**
     * Handles the disconnection of a client from the server.
     * This method attempts to disconnect the user from their lobby and remove them from the system.
     *
     * @param client The SocketIOClient that disconnected.
     */
    public void handleClientDisconnect(SocketIOClient client) {
        logger.info("Handling disconnect for client with session ID: {}", client.getSessionId());
        try {
            lobbyManager.disconnectFromLobby(getUserBySessionId(client.getSessionId()));
            removeUserBySessionId(client.getSessionId());
        } catch (Exception e) {
            logger.error("Error while disconnecting user {}: {}", client.getSessionId(), e.getMessage());
        }
    }

    /**
     * Handles the removal of an item by a client.
     * This method updates the lobby's list of removed items and broadcasts the change to all users in the lobby.
     *
     * @param client The SocketIOClient that removed the item.
     * @param data   The data containing the removed item information.
     */
    public void handleItemRemove(SocketIOClient client, Object data) {
        try {
            logger.debug("Handling item removal for client {}: {}", client.getSessionId(), data);
            LootBox.Item item = gson.fromJson(data.toString(), LootBox.Item.class);

            Lobby lobby = lobbyManager.getLobbyByClient(client);
            lobby.addItem(item);

            // Broadcast the updated list of removed items to all users in the lobby
            for (User user : lobby.getOnlineUsers()) {
                user.getClient().sendEvent("removed_items", gson.toJson(lobby.getRemovedItems()));
                logger.debug("Sent removed items update to user {}", user.getSocketSessionId());
            }
        } catch (Exception e) {
            logger.error("Error while removing item for client {}: {}", client.getSessionId(), e.getMessage());
        }
    }

    /**
     * Handles the location update of a client.
     * This method updates the user's coordinates in the lobby and broadcasts the change to all users in the lobby.
     *
     * @param client The SocketIOClient that updated their location.
     * @param data   The data containing the updated coordinates.
     */
    public void handleLocationUpdate(SocketIOClient client, Object data) {
        try {
            logger.debug("Handling location update for client {}: {}", client.getSessionId(), data);
            Coordinates coords = gson.fromJson(data.toString(), Coordinates.class);

            Lobby lobby = lobbyManager.getLobbyByClient(client);
            lobby.getOnlineUserByClient(client).setCoordinates(coords);

            // Broadcast the updated user locations to all users in the lobby
            for (User user : lobby.getOnlineUsers()) {
                user.getClient().sendEvent("user_locations", gson.toJson(lobby.getOnlineUsers()));
                logger.debug("Sent location update to user {}", user.getSocketSessionId());
            }
        } catch (Exception e) {
            logger.error("Error while updating user location for client {}: {}", client.getSessionId(), e.getMessage());
        }
    }


    /**
     * Handles the connection of a client to the server.
     * This method authenticates the user, adds them to the system, and connects them to their lobby if applicable.
     *
     * @param client The SocketIOClient that connected.
     */
    public void handleClientConnect(SocketIOClient client) {
        logger.info("Handling connection for client with session ID: {}", client.getSessionId());
        BroadcastOperations broadcastOperations = server.getBroadcastOperations();
        // Check if the client is already connected
        if (users.contains(new User(client))) {
            logger.warn("Client with session ID {} is already connected.", client.getSessionId());
            return;
        }
        try {
            String token = client.getHandshakeData().getHttpHeaders().get("Authorization");
            // Check if the user is an admin using their token
            if (LoginCheck.checkLoginToken(token)) {
                logger.info("Authorized admin connected with session ID: {}", client.getSessionId());

                // Create a user object and set its rank to ADMIN
                User user = TokenManager.getUserFromToken(token);
                user.setRank(Rank.ADMIN);
                user.setSocketSessionId(client.getSessionId());
                user.setClient(client);

                try {
                    // Add the user to the system and connect them to their lobby
                    users.add(user);
                    String lobbyCodeHeader = client.getHandshakeData().getHttpHeaders().get("X-LobbyCode");
                    logger.debug("Admin joining lobby with code: {}", lobbyCodeHeader);
                    lobbyManager.connectToLobby(user, new LobbyCode(lobbyCodeHeader));
                } catch (RuntimeException re) {
                    // Handle invalid lobby code provided by admin
                    logger.error("Invalid lobby code for admin: {}", re.getMessage());
                    client.disconnect();
                } finally {
                    // Broadcast an update about the player list after the admin's connection
                    logger.debug("Broadcasting player update after admin connection.");
                    broadcastOperations.sendEvent("playerUpdate", gson.toJson(users));

                    try {
                        // Send removed items to the admin
                        client.sendEvent("removed_items", gson.toJson(lobbyManager.getLobbyByClient(client).getRemovedItems()));
                        logger.debug("Sent removed items to admin with session ID: {}", client.getSessionId());
                    } catch (LobbyNotFoundException lnfe) {
                        logger.error("Error sending removed items to admin: {}", lnfe.getMessage());
                    }
                }
            } else {
                // Handle regular user connection
                try {
                    String lobbyCodeHeader = client.getHandshakeData().getHttpHeaders().get("X-LobbyCode");
                    logger.info("Regular user connected with session ID: {} and lobby code: {}", client.getSessionId(), lobbyCodeHeader);

                    // Create a user object and set its rank to USER
                    User user = new User(client, Rank.USER);
                    lobbyManager.connectToLobby(user, new LobbyCode(lobbyCodeHeader));
                    users.add(user);
                } catch (RuntimeException | LobbyNotFoundException r) {
                    // Handle invalid lobby code provided by regular user
                    logger.error("Invalid lobby code for user: {}", r.getMessage());
                    client.disconnect();
                } finally {
                    // Broadcast an update about the player list after the user's connection
                    logger.debug("Broadcasting player update after user connection.");
                    broadcastOperations.sendEvent("playerUpdate", gson.toJson(users));
                }
            }
        } catch (Exception e) {
            // Handle general exceptions during connection
            logger.error("Error handling client connection: {}", e.getMessage());
            client.disconnect();
        }
    }

}