package me.river.royaltisapi.core.data.records;

/**
 * Represents a border.
 * @param id The unique identifier of the border.
 * @param type The type of border (e.g., "border").
 * @param coords The coordinates of the border.
 * @param gameId The unique identifier of the game.
 */
public record Border(int id, String type, Coordinates coords, int gameId) {
    /**
     * Creates a new border with the specified ID, type, coordinates, and game ID.
     * @param id The unique identifier of the border.
     * @param type The type of border (e.g., "border").
     * @param coords The coordinates of the border.
     */
    public Border(int id, String type, Coordinates coords) {
        this(id, type, coords, 0);
    }
}