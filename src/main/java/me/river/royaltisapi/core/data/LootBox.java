package me.river.royaltisapi.core.data;

import java.util.List;

public class LootBox {
    private double id;
    private double mysqlID;
    private String type;
    private Coordinates coords;
    private List<Item> items;

    public LootBox(double id, String type, Coordinates coords, List<Item> items) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.items = items;
    }

    public double getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setMysqlID(double mysqlID) {
        this.mysqlID = mysqlID;
    }

    public double getMysqlID() {
        return mysqlID;
    }

    public static class Item {
        private String name;

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LootBox{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", coords=" + coords +
                ", items=" + items +
                '}';
    }
}

