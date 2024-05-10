package me.river.royaltisapi.core.data.records;

public record Border(int id, String type, Coordinates coords, int gameId) {
    public Border(int id, String type, Coordinates coords) {
        this(id, type, coords, 0);
    }
}