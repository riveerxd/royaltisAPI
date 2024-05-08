package me.river.royaltisapi.core.data;

public class GameProps {
    private int gameId;
    private int count;
    private int interval;
    private String lobbyCode;

    public GameProps(int gameId, int count, int interval, LobbyCode lobbyCode) {
        this.gameId = gameId;
        this.count = count;
        this.interval = interval;
        this.lobbyCode = lobbyCode.toString();
    }


    public int getGameId() {
        return gameId;
    }

    public int getCount() {
        return count;
    }

    public int getInterval() {
        return interval;
    }

    public LobbyCode getLobbyCode() {
        return new LobbyCode(this.lobbyCode);
    }
}
