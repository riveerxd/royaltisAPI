package me.river.royaltisapi.core.data;

import java.util.ArrayList;

public class GameData {
    private ArrayList<Border> borders;
    private ArrayList<LootBox> lootboxes;
    private String gameName;
    private int gameID;
    private MiddlePoint middlePoint;

    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, String gameName) {
        this.borders = borders;
        this.lootboxes = lootboxes;
        this.gameName = gameName;
    }

    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, MiddlePoint middlePoint) {
        this.borders = borders;
        this.lootboxes = lootboxes;
        this.gameName = "not set";
        this.middlePoint = middlePoint;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public ArrayList<Border> getBorders() {
        return borders;
    }

    public ArrayList<LootBox> getLootboxes() {
        return lootboxes;
    }

    public String getGameName() {
        return gameName;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "borders=" + borders +
                ", lootboxes=" + lootboxes +
                ", gameName='" + gameName + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
