package me.river.royaltisapi.core.data.records;

public record LobbyCode(String lobbyCode) {
    @Override
    public String toString() {
        return this.lobbyCode;
    }
}
