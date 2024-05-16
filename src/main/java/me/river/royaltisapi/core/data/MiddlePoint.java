package me.river.royaltisapi.core.data;

import me.river.royaltisapi.core.data.records.Coordinates;

/**
 * Represents a middle point.
 */
public class MiddlePoint {

    /**
     * The game ID of where this middle point belongs.
     */
    private int gameId;

    /**
     * The coordinates of this middle point.
     */
    private Coordinates coordinates;

    /**
     * Creates a new instance of MiddlePoint with the given coordinates.
     *
     * @param coordinates the coordinates to set
     */
    public MiddlePoint(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Creates a new instance of MiddlePoint with the given game ID and coordinates.
     *
     * @param gameId      the game ID to set
     * @param coordinates the coordinates to set
     */
    public MiddlePoint(int gameId, Coordinates coordinates) {
        this.gameId = gameId;
        this.coordinates = coordinates;
    }

    /**
     * Returns the coordinates of this middle point.
     *
     * @return the coordinates of this middle point
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the game ID of this middle point (optional).
     *
     * @return the game ID of this middle point
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID of this middle point.
     *
     * @param gameId the game ID to set
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Returns a string representation of this middle point.
     *
     * @return a string representation of this middle point
     */
    @Override
    public String toString() {
        return "MiddlePoint{" +
                "gameId=" + gameId +
                ", coordinates=" + coordinates +
                '}';
    }
}