package me.river.royaltisapi.core;

import me.river.royaltisapi.core.data.GameId;
import me.river.royaltisapi.core.data.LobbyCode;

import java.util.HashSet;
import java.util.Objects;

public class Lobby {
    private LobbyCode lobbyCode;
    private GameId gameId;
    private HashSet<User> onlineUsers = new HashSet<>();

    public boolean connectUser(User user){
        onlineUsers.add(user);
        System.out.println("User "+user.getSocketSessionId()+" connected to lobby "+lobbyCode);
        System.out.println(this.onlineUsers);
        return true;
    }

    public boolean disconnectUser(User user){
        return onlineUsers.remove(user);
    }

    public HashSet<User> getOnlineUsers() {
        return onlineUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(lobbyCode, lobby.lobbyCode);
    }

    public void setLobbyCode(LobbyCode lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lobbyCode);
    }

    public Lobby(LobbyCode lobbyCode, GameId gameId) {
        this.lobbyCode = lobbyCode;
        this.gameId = gameId;
    }

    public LobbyCode getLobbyCode() {
        return lobbyCode;
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "lobbyCode=" + lobbyCode +
                ", gameId=" + gameId +
                '}';
    }
}