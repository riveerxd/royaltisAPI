package me.river.royaltisapi.core.data.json;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser {
    public static GameData parseJsonToGameData(String jsonData) {
        Gson gson = new Gson();

        List<Object> data = gson.fromJson(jsonData, List.class);

        ArrayList<Border> borders = new ArrayList<>();
        ArrayList<LootBox> lootboxes = new ArrayList<>();
        MiddlePoint middlePoint = null;
        String gameName = "not set";

        if (data instanceof List) {
            for (Object element : data) {
                System.out.println(element);
                if (element instanceof List) {
                    for (Object innerElement : (List) element) {
                        if (innerElement instanceof Map) {
                            Map<String, Object> elementMap = (Map<String, Object>) innerElement;
                            String type = (String) elementMap.get("type");

                            switch (type){
                                case "border":
                                    double borderId = (double) elementMap.get("id");
                                    Map<String, Double> borderCoordsMap = (Map<String, Double>) elementMap.get("coords");
                                    Border border = new Border(borderId, type, new Coordinates(borderCoordsMap.get("latitude"), borderCoordsMap.get("longitude")));
                                    borders.add(border);
                                    break;

                                case "lootbox":
                                    Map<String, Object> lootBoxMap = (Map<String, Object>) elementMap;
                                    int lootboxId = (int) Math.round((double)lootBoxMap.get("id"));
                                    Map<String, Double> lootboxCoordsMap = (Map<String, Double>) lootBoxMap.get("coords");
                                    List<Map<String, String>> itemsMaps = (List<Map<String, String>>) lootBoxMap.get("items");

                                    List<LootBox.Item> items = new ArrayList<>();
                                    for (Map<String, String> itemMap : itemsMaps) {
                                        String itemName = itemMap.get("name");
                                        items.add(new LootBox.Item(itemName));
                                    }
                                    LootBox lootBox = new LootBox(lootboxId, type, new Coordinates(lootboxCoordsMap.get("latitude"), lootboxCoordsMap.get("longitude")), items);
                                    lootboxes.add(lootBox);
                                    break;

                                case "mapCenter":
                                    Map<String, Double> mapCenterCoordMap = (Map<String, Double>) elementMap.get("coords");
                                    MiddlePoint mp = new MiddlePoint(new Coordinates(mapCenterCoordMap.get("latitude"), mapCenterCoordMap.get("longitude")));
                                    break;

                                case "gameName":
                                    String gameNameValue = String.valueOf(elementMap.get("value"));
                                    gameName = gameNameValue;
                                    break;
                            }

                        }
                    }
                }
            }
        }

        GameData gameData = new GameData(borders, lootboxes, middlePoint, gameName);
        return gameData;
    }
}