package me.river.royaltisapi.core.data;

public class GameProps {
    private int gameId;
    private int count;
    private int interval;

    public GameProps(int gameId, int count, int interval) {
        this.gameId = gameId;
        this.count = count;
        this.interval = interval;
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
}
