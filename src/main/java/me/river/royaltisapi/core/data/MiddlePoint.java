package me.river.royaltisapi.core.data;

public class MiddlePoint {
    private Coordinates coordinates;

    public MiddlePoint(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
