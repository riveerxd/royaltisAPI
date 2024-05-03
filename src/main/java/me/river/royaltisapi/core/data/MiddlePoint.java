package me.river.royaltisapi.core.data;

public class MiddlePoint {
    private int gameId;
    private Coordinates coordinates;

    public MiddlePoint(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public MiddlePoint(int gameId, Coordinates coordinates) {
        this.gameId = gameId;
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "MiddlePoint{" +
                "gameId=" + gameId +
                ", coordinates=" + coordinates +
                '}';
    }
}
