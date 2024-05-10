package me.river.royaltisapi.core;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.GameId;
import me.river.royaltisapi.core.data.LobbyCode;
import me.river.royaltisapi.core.data.LootBox;

import java.util.HashSet;
import java.util.Objects;

public class Lobby {
    private LobbyCode lobbyCode;
    private GameId gameId;
    private HashSet<User> onlineUsers = new HashSet<>();
    private HashSet<LootBox.Item> removedItems = new HashSet<>();

    public boolean addItem(LootBox.Item item){
        return removedItems.add(item);
    }

    public boolean removeItem(LootBox.Item item){
        return removedItems.remove(item);
    }

    public boolean connectUser(User user){
        onlineUsers.add(user);
        System.out.println("User "+user.getSocketSessionId()+" connected to lobby "+lobbyCode);
        System.out.println("online users in lobby" + lobbyCode+ this.onlineUsers);
        return true;
    }

    public boolean disconnectUser(User user){
        return onlineUsers.remove(user);
    }

    public HashSet<User> getOnlineUsers() {
        return onlineUsers;
    }

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

    public GameId getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "lobbyCode=" + lobbyCode +
                ", gameId=" + gameId +
                '}';
    }
}