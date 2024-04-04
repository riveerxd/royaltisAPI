package me.river.royaltisapi.core.data;
public class Border {
    private double id;
    private String type;
    private Coordinates coords;

    public double getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public Border(double id, String type, Coordinates coords) {
        this.id = id;
        this.type = type;
        this.coords = coords;
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

