package me.river.royaltisapi.core.data.json;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser {
    /**
     * This method takes in a string of JSON data and parses it into an instance of the GameData class.
     *
     * @param jsonData the input JSON data
     * @return an instance of the GameData class containing the parsed data
     */
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
                                    int borderId = (int) Math.round( (double) elementMap.get("id"));
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
                                    middlePoint = new MiddlePoint(new Coordinates(mapCenterCoordMap.get("latitude"), mapCenterCoordMap.get("longitude")));
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

        return new GameData(borders, lootboxes, middlePoint, gameName);
    }
}