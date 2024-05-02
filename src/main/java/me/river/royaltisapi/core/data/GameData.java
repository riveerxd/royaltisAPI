package me.river.royaltisapi.core.data;

import java.util.ArrayList;

public class GameData {
    private ArrayList<Border> borders;
    private ArrayList<LootBox> lootboxes;
    private String gameName;
    private int gameID;
    private MiddlePoint middlePoint;

    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, String gameName, MiddlePoint middlePoint) {
        this.middlePoint = middlePoint;
        this.gameName = gameName;
        this.lootboxes = lootboxes;
        this.borders = borders;
    }

    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, String gameName) {
        this.borders = borders;
        this.lootboxes = lootboxes;
        this.gameName = gameName;
    }

    public GameData(ArrayList<Border> borders, ArrayList<LootBox> lootboxes, MiddlePoint middlePoint, String gameName) {
        this.borders = borders;
        this.lootboxes = lootboxes;
        this.middlePoint = middlePoint;
        this.gameName = gameName;
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

    public MiddlePoint getMiddlePoint() {
        return middlePoint;
    }

    public int getGameID() {
        return gameID;
    }

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
