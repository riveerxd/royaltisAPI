package me.river.royaltisapi.core.data;
public class Border {
    private int id;
    private String type;
    private Coordinates coords;
    private int gameId;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public Border(int id, String type, Coordinates coords) {
        this.id = id;
        this.type = type;
        this.coords = coords;
    }

    public Border(int id, String type, Coordinates coords, int gameId) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "Border{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", coords=" + coords +
                '}';
    }
}

