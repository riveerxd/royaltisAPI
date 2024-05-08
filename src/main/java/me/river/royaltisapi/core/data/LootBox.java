package me.river.royaltisapi.core.data;

import java.util.List;
import java.util.Objects;

public class LootBox {
    private int id;
    private int mysqlID;
    private String type;
    private Coordinates coords;
    private List<Item> items;
    private int gameId;

    public LootBox(int id, String type, Coordinates coords, List<Item> items) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.items = items;
    }

    public LootBox(int id, String type, Coordinates coords, List<Item> items, int gameId) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.items = items;
        this.gameId = gameId;
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

    public void setMysqlID(int mysqlID) {
        this.mysqlID = mysqlID;
    }

    public int getMysqlID() {
        return mysqlID;
    }

    public static class Item {
        private String name;
        private int itemId;

        public Item(String name) {
            this.name = name;
        }

        public Item(String name, int lootboxId) {
            this.name = name;
            this.itemId = lootboxId;
        }

        public String getName() {
            return name;
        }

        public int getItemId() {
            return itemId;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", lootboxId=" + itemId +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return itemId == item.itemId;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(itemId);
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

