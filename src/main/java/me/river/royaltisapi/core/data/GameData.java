package me.river.royaltisapi.core.data;

import me.river.royaltisapi.core.data.records.Border;

import java.util.ArrayList;

/**
 * GameData represents a game's data, including its borders, loot boxes, and other metadata.
 */
public class GameData {
    /**
     * The list of borders in this game.
     */
    private ArrayList<Border> borders;

    /**
     * The list of loot boxes in this game.
     */
    private ArrayList<LootBox> lootboxes;

    /**
     * The name of the game.
     */
    private String gameName;

    /**
     * The unique identifier for this game.
     */
    private int gameID;

    /**
     * The middle point of the game board.
     */
    private MiddlePoint middlePoint;

    /**
     * Creates a new GameData instance with the specified borders, loot boxes, game name, and middle point.
     *
     * @param borders      The list of borders in this game.
     * @param lootboxes    The list of loot boxes in this game.
     * @param gameName     The name of the game.
     * @param middlePoint  The middle point of the game board.
     */
    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, String gameName, MiddlePoint middlePoint) {
        this.borders = borders;
        this.lootboxes = lootboxes;
        this.gameName = gameName;
        this.middlePoint = middlePoint;
    }

    /**
     * Returns the list of borders in this game.
     *
     * @return The list of borders in this game.
     */
    public ArrayList<Border> getBorders() {
        return borders;
    }

    /**
     * Returns the list of loot boxes in this game.
     *
     * @return The list of loot boxes in this game.
     */
    public ArrayList<LootBox> getLootboxes() {
        return lootboxes;
    }

    /**
     * Returns the name of the game.
     *
     * @return The name of the game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Returns the unique identifier for this game.
     *
     * @return The unique identifier for this game.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Returns the middle point of the game board.
     *
     * @return The middle point of the game board.
     */
    public MiddlePoint getMiddlePoint() {
        return middlePoint;
    }

    /**
     * Sets the unique identifier for this game.
     *
     * @param gameID The unique identifier for this game.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Returns a string representation of the game data.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "GameData{" +
                "\nborders=" + borders +
                ", \nlootboxes=" + lootboxes +
                ", \ngameName='" + gameName + '\'' +
                ", \ngameID=" + gameID +
                ", \nmiddlePoint=" + middlePoint +
                '}';
    }
}
