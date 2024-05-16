package me.river.royaltisapi.core.data;

import me.river.royaltisapi.core.data.records.Coordinates;
import java.util.List;
import java.util.Objects;

/**
 * Represents a LootBox.
 */
public class LootBox {

    /**
     * The id of this LootBox.
     */
    private int id;

    /**
     * The mysql ID of this LootBox.
     */
    private int mysqlID;

    /**
     * The type of this LootBox.
     */
    private String type;

    /**
     * The coordinates of this LootBox.
     */
    private Coordinates coords;

    /**
     * The items in this LootBox.
     */
    private List<Item> items;

    /**
     * The game ID of where this LootBox is located.
     */
    private int gameId;

    /**
     * Creates a new instance of LootBox with the given parameters.
     *
     * @param id          the id of this LootBox
     * @param type        the type of this LootBox
     * @param coords      the coordinates of this LootBox
     * @param items       the items in this LootBox
     */
    public LootBox(int id, String type, Coordinates coords, List<Item> items) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.items = items;
    }

    /**
     * Creates a new instance of LootBox with the given parameters and a game ID.
     *
     * @param id          the id of this LootBox
     * @param type        the type of this LootBox
     * @param coords      the coordinates of this LootBox
     * @param items       the items in this LootBox
     * @param gameId      the game ID of this LootBox
     */
    public LootBox(int id, String type, Coordinates coords, List<Item> items, int gameId) {
        this.id = id;
        this.type = type;
        this.coords = coords;
        this.items = items;
        this.gameId = gameId;
    }

    /**
     * Returns the id of this LootBox.
     *
     * @return the id of this LootBox
     */
    public double getId() {
        return id;
    }

    /**
     * Returns the type of this LootBox.
     *
     * @return the type of this LootBox
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the coordinates of this LootBox.
     *
     * @return the coordinates of this LootBox
     */
    public Coordinates getCoords() {
        return coords;
    }

    /**
     * Returns the items in this LootBox.
     *
     * @return the items in this LootBox
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Sets the mysql ID of this LootBox.
     *
     * @param mysqlID the mysql ID to set
     */
    public void setMysqlID(int mysqlID) {
        this.mysqlID = mysqlID;
    }

    /**
     * Returns the mysql ID of this LootBox.
     *
     * @return the mysql ID of this LootBox
     */
    public int getMysqlID() {
        return mysqlID;
    }

    /**
     * Returns a string representation of this LootBox.
     *
     * @return a string representation of this LootBox
     */
    @Override
    public String toString() {
        return "LootBox{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", coords=" + coords +
                ", items=" + items +
                '}';
    }

    /**
     * Represents an item that can be found in a LootBox.
     */
    public static class Item {

        private String name;

        private int itemId;

        /**
         * Creates a new instance of Item with the given name.
         *
         * @param name the name to set
         */
        public Item(String name) {
            this.name = name;
        }

        /**
         * Creates a new instance of Item with the given name and ID.
         *
         * @param name    the name to set
         * @param itemId  the item ID to set
         */
        public Item(String name, int itemId) {
            this.name = name;
            this.itemId = itemId;
        }

        /**
         * Returns the name of this Item.
         *
         * @return the name of this Item
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the ID of this Item.
         *
         * @return the ID of this Item
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * Returns a string representation of this Item.
         *
         * @return a string representation of this Item
         */
        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", itemId=" + itemId +
                    '}';
        }

        /**
         * Compares this Item to another object for equality.
         *
         * @param obj the other object to compare with
         * @return true if this Item is equal to the given object, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Item)) return false;

            Item item = (Item) obj;
            return name.equals(item.name) && itemId == item.itemId;
        }

        /**
         * Returns the hash code of this Item.
         *
         * @return the hash code of this Item
         */
        @Override
        public int hashCode() {
            return Objects.hash(name, itemId);
        }
    }
}