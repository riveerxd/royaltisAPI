package me.river.royaltisapi.core.data.records;

public record GameProps(int gameId, int count, int interval, String lobbyCode) {
    public LobbyCode getLobbyCode() {
        return new LobbyCode(this.lobbyCode);
    }
}