package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.LobbyNotFoundException;
import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.game.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;

/**
 * This class manages all the lobbies in the game. It provides functionality to create,
 * retrieve, join, leave, and delete lobbies.
 */
public class LobbyManager {
    private static final Logger logger = LoggerFactory.getLogger(LobbyManager.class);
    private final HashSet<Lobby> lobbies = new HashSet<>();

    /**
     * Checks if a lobby with the given lobby code exists.
     *
     * @param lobbyCode the lobby code to check for
     * @return true if the lobby exists, false otherwise
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public boolean doesLobbyExist(LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Checking if lobby with code {} exists.", lobbyCode.lobbyCode());
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                logger.info("Lobby with code {} found.", lobbyCode.lobbyCode());
                return true;
            }
        }
        logger.warn("Lobby with code {} not found.", lobbyCode.lobbyCode());
        throw new LobbyNotFoundException("Lobby does not exist");
    }

    /**
     * Retrieves the game ID associated with the given lobby code.
     *
     * @param lobbyCode the lobby code to retrieve the game ID for
     * @return the GameId object associated with the lobby
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public GameId getGameIdByLobbyCode(LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Retrieving game ID for lobby with code {}.", lobbyCode.lobbyCode());
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                GameId gameId = lobby.getGameId();
                logger.info("Game ID for lobby with code {} is {}.", lobbyCode.lobbyCode(), gameId.gameId());
                return gameId;
            }
        }
        logger.warn("Lobby with code {} not found.", lobbyCode.lobbyCode());
        throw new LobbyNotFoundException("Lobby does not exist");
    }

    /**
     * Connects a user to a lobby with the given lobby code.
     *
     * @param user the user to connect
     * @param lobbyCode the lobby code of the lobby to connect to
     * @return true if the user was successfully connected, false otherwise
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public boolean connectToLobby(User user, LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Attempting to connect user {} to lobby {}.", user.getUsername(), lobbyCode.lobbyCode());
        if (doesLobbyExist(lobbyCode)) {
            Lobby wantedLobby = getLobbyByLobbyCode(lobbyCode);
            boolean connected = wantedLobby.connectUser(user);
            if (connected) {
                logger.info("User {} connected to lobby {} successfully.", user.getUsername(), lobbyCode.lobbyCode());
            } else {
                logger.warn("Failed to connect user {} to lobby {}.", user.getUsername(), lobbyCode.lobbyCode());
            }
            return connected;
        }
        logger.warn("Lobby with code {} not found for user connection.", lobbyCode.lobbyCode());
        return false;
    }

    /**
     * Disconnects a user from their current lobby.
     *
     * @param user the user to disconnect
     * @return true if the user was successfully disconnected, false otherwise
     * @throws UserNotFoundException if the user is not found in any lobby
     * @throws LobbyNotFoundException if the lobby the user was in is not found
     */
    public boolean disconnectFromLobby(User user) throws UserNotFoundException, LobbyNotFoundException {
        logger.info("Attempting to disconnect user {} from lobby.", user.getUsername());
        for (Lobby lobby : lobbies) {
            for (User currUser : lobby.getOnlineUsers()) {
                if (currUser.equals(user)) {
                    boolean disconnected = lobby.disconnectUser(currUser);
                    if (disconnected) {
                        logger.info("User {} disconnected from lobby {} successfully.", user.getUsername(), lobby.getLobbyCode().lobbyCode());
                        checkLobbyDestroy(lobby.getLobbyCode());
                    } else {
                        logger.warn("Failed to disconnect user {} from lobby {}.", user.getUsername(), lobby.getLobbyCode().lobbyCode());
                    }
                    return disconnected;
                }
            }
        }
        logger.warn("User {} not found in any lobby for disconnection.", user.getUsername());
        throw new UserNotFoundException("User not connected");
    }

    /**
     * Checks if a lobby should be destroyed because it is empty. If so, it removes
     * the lobby.
     *
     * @param lobbyCode the lobby code to check
     * @return true if the lobby was destroyed, false otherwise
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public boolean checkLobbyDestroy(LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Checking if lobby with code {} should be destroyed.", lobbyCode.lobbyCode());
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                if (lobby.getOnlineUsers().isEmpty()) {
                    removeLobby(lobbyCode);
                    logger.info("Destroyed empty lobby {} on game {}.", lobbyCode.lobbyCode(), lobby.getGameId().gameId());
                    return true;
                } else {
                    logger.info("Lobby {} still has active users.", lobbyCode.lobbyCode());
                }
            }
        }
        logger.warn("Lobby with code {} not found for destruction check.", lobbyCode.lobbyCode());
        throw new LobbyNotFoundException("Lobby not found");
    }

    /**
     * Retrieves a lobby with the given lobby code.
     *
     * @param lobbyCode the lobby code to retrieve
     * @return the Lobby object
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public Lobby getLobbyByLobbyCode(LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Retrieving lobby with code {}.", lobbyCode.lobbyCode());
        if (doesLobbyExist(lobbyCode)) {
            for (Lobby lobby : lobbies) {
                if (lobby.getLobbyCode().equals(lobbyCode)) {
                    return lobby;
                }
            }
        }
        logger.warn("Lobby with code {} not found.", lobbyCode.lobbyCode());
        throw new LobbyNotFoundException("Lobby not found");
    }

    /**
     * Retrieves the lobby a client is currently in.
     *
     * @param client the client to find the lobby for
     * @return the Lobby object
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public Lobby getLobbyByClient(SocketIOClient client) throws LobbyNotFoundException {
        logger.info("Retrieving lobby for client with session ID {}.", client.getSessionId());
        for (Lobby lobby : lobbies){
            for (User user : lobby.getOnlineUsers()){
                if (user.getClient().getSessionId().equals(client.getSessionId())){
                    logger.info("Found lobby {} for client.", lobby.getLobbyCode().lobbyCode());
                    return lobby;
                }
            }
        }
        logger.warn("No lobby found for client with session ID {}.", client.getSessionId());
        throw new LobbyNotFoundException("Lobby not found");
    }

    /**
     * Removes a lobby with the given lobby code.
     *
     * @param lobbyCode the lobby code to remove
     * @return true if the lobby was successfully removed, false otherwise
     * @throws LobbyNotFoundException if the lobby is not found
     */
    public boolean removeLobby(LobbyCode lobbyCode) throws LobbyNotFoundException {
        logger.info("Removing lobby with code {}.", lobbyCode.lobbyCode());
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                lobby.disconnectAllUsers(); // disconnect all users before removing the lobby
                boolean removed = lobbies.remove(lobby);
                if (removed) {
                    logger.info("Lobby with code {} removed successfully", lobbyCode.lobbyCode());
                } else {
                    logger.warn("Failed to remove lobby with code {}.", lobbyCode.lobbyCode());
                }
                return removed;
            }
        }
        logger.warn("Lobby with code {} not found for removal.", lobbyCode.lobbyCode());
        throw new LobbyNotFoundException("Lobby not found");
    }

    /**
     * Creates a new lobby for the specified game.
     *
     * @param gameId the ID of the game to create the lobby for
     * @return the lobby code of the newly created lobby
     */
    public String createLobby(GameId gameId) {
        LobbyCode lobbyCode = generateLobbyCode();
        Lobby lobby = new Lobby(lobbyCode, gameId);

        // Ensure the generated lobby code is unique
        while (lobbies.contains(lobby)) {
            lobbyCode = generateLobbyCode();
            lobby.setLobbyCode(lobbyCode);
        }

        lobbies.add(lobby);
        logger.info("Created lobby with code {} for game {}.", lobbyCode.lobbyCode(), gameId.gameId());
        return lobby.getLobbyCode().toString();
    }

    /**
     * Generates a random six-digit lobby code.
     *
     * @return a new LobbyCode object
     */
    private LobbyCode generateLobbyCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        return new LobbyCode(code.toString());
    }

    /**
     * Lobbies getter
     * @return lobbies
     */
    public HashSet<Lobby> getLobbies() {
        return lobbies;
    }
}
