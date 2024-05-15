package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;

import java.util.HashSet;
import java.util.Random;

/**
 * The Lobby manager.
 */
public class LobbyManager {
    /**
     * The Lobbies.
     */
    private HashSet<Lobby> lobbies = new HashSet<>();

    /**
     * Does lobby exist boolean.
     *
     * @param lobbyCode the lobby code
     * @return the boolean
     */
    public boolean doesLobbyExist(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return true;
            }
        }
        throw new RuntimeException("Lobby does not exist");
    }

    /**
     * Does lobby exist boolean.
     *
     * @param lobbyCode the lobby code
     * @return the boolean
     */
    public GameId getGameIdByLobbyCode(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return lobby.getGameId();
            }
        }
        throw new RuntimeException("Lobby does not exist");
    }

    /**
     * Connect a user to a lobby.
     *
     * @param user the user
     * @param lobbyCode the lobby code
     * @return connected or not
     */
    public boolean connectToLobby(User user, LobbyCode lobbyCode) {
        if (doesLobbyExist(lobbyCode)) {
            Lobby wantedLobby = getLobbyByLobbyCode(lobbyCode);
            return wantedLobby.connectUser(user);
        }
        return false;
    }

    /**
     * Disconnect a user from a lobby.
     *
     * @param user the user
     * @return disconnected or not
     */
    public boolean disconnectFromLobby(User user) {
        for (Lobby lobby : lobbies) {
            for (User currUser : lobby.getOnlineUsers()) {
                if (currUser.equals(user)) {
                    lobby.disconnectUser(currUser);
                    checkLobbyDestroy(lobby.getLobbyCode());
                    return true;
                }
            }
        }
        throw new RuntimeException("User not connected");
    }

    /**
     * Check if a lobby should be destroyed.
     *
     * @param lobbyCode the lobby code
     * @return destroyed or not
     */
    public boolean checkLobbyDestroy(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                if (lobby.getOnlineUsers().isEmpty()) {
                    removeLobby(lobbyCode);
                    System.out.println("Destroyed lobby " + lobbyCode + " on game "+lobby.getGameId().gameId());
                    return true;
                }
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    /**
     * Get lobby by lobby code.
     *
     * @param lobbyCode the lobby code
     * @return the lobby
     */
    public Lobby getLobbyByLobbyCode(LobbyCode lobbyCode) {
        if (doesLobbyExist(lobbyCode)) {
            for (Lobby lobby : lobbies) {
                if (lobby.getLobbyCode().equals(lobbyCode)) {
                    return lobby;
                }
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    /**
     * Get lobby by client.
     *
     * @param client the client
     * @return the lobby
     */
    public Lobby getLobbyByClient(SocketIOClient client) {
        for (Lobby lobby : lobbies){
            for (User user : lobby.getOnlineUsers()){
                if (user.getClient().getSessionId().equals(client.getSessionId())){
                    return lobby;
                }
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    /**
     * Remove a lobby.
     *
     * @param lobbyCode the lobby code
     * @return removed or not
     */
    public boolean removeLobby(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return lobbies.remove(lobby);
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    /**
     * Create a lobby.
     *
     * @param gameId the game id
     * @return the lobby code
     */
    public String createLobby(GameId gameId) {
        LobbyCode lobbyCode = generateLobbyCode();
        Lobby lobby = new Lobby(lobbyCode, gameId);
        while (lobbies.contains(lobby)) {
            lobbyCode = generateLobbyCode();
            lobby.setLobbyCode(lobbyCode);
        }
        lobbies.add(lobby);
        System.out.println("Created lobby " + lobbyCode + " on game "+gameId.gameId());
        return lobby.getLobbyCode().toString();
    }

    /**
     * Generate a lobby code.
     *
     * @return the lobby code
     */
    private LobbyCode generateLobbyCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        return new LobbyCode(code.toString());
    }
}
