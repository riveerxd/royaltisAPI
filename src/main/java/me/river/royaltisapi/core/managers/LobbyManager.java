package me.river.royaltisapi.core.managers;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.Lobby;
import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.data.GameId;
import me.river.royaltisapi.core.data.LobbyCode;

import java.util.HashSet;
import java.util.Random;

public class LobbyManager {
    private HashSet<Lobby> lobbies = new HashSet<>();

    public boolean doesLobbyExist(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return true;
            }
        }
        throw new RuntimeException("Lobby does not exist");
    }

    public GameId getGameIdByLobbyCode(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return lobby.getGameId();
            }
        }
        throw new RuntimeException("Lobby does not exist");
    }

    public boolean connectToLobby(User user, LobbyCode lobbyCode) {
        if (doesLobbyExist(lobbyCode)) {
            Lobby wantedLobby = getLobbyByLobbyCode(lobbyCode);
            return wantedLobby.connectUser(user);
        }
        return false;
    }

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

    public boolean checkLobbyDestroy(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                if (lobby.getOnlineUsers().isEmpty()) {
                    removeLobby(lobbyCode);
                    System.out.println("Destroyed lobby " + lobbyCode);
                    return true;
                }
            }
        }
        throw new RuntimeException("Lobby not found");
    }

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

    public boolean removeLobby(LobbyCode lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return lobbies.remove(lobby);
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    public String createLobby(GameId gameId) {
        LobbyCode lobbyCode = generateLobbyCode();
        Lobby lobby = new Lobby(lobbyCode, gameId);
        while (lobbies.contains(lobby)) {
            lobbyCode = generateLobbyCode();
            lobby.setLobbyCode(lobbyCode);
        }
        lobbies.add(lobby);
        System.out.println(lobbies);
        return lobby.getLobbyCode().toString();
    }

    private LobbyCode generateLobbyCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        return new LobbyCode(code.toString());
    }
}
