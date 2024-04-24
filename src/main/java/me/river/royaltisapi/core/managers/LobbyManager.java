package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.Lobby;
import me.river.royaltisapi.core.data.GameId;
import me.river.royaltisapi.core.data.LobbyCode;

import java.util.HashSet;
import java.util.Random;

public class LobbyManager {
    private HashSet<Lobby> lobbies = new HashSet<>();

    public boolean removeLobby(LobbyCode lobbyCode){
        for (Lobby lobby : lobbies){
            if (lobby.getLobbyCode().equals(lobbyCode)){
                return lobbies.remove(lobby);
            }
        }
        throw new RuntimeException("Lobby not found");
    }

    public String createLobby(GameId gameId){
        LobbyCode lobbyCode = generateLobbyCode();
        Lobby lobby = new Lobby(lobbyCode, gameId);
        while (lobbies.contains(lobby)){
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
