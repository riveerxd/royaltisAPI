package me.river.royaltisapi.core.data.json;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.Border;
import me.river.royaltisapi.core.data.Coordinates;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser {
    public GameData parseJsonToGameData(String jsonData) {
        Gson gson = new Gson();

        List<Object> data = gson.fromJson(jsonData, List.class);

        ArrayList<Border> borders = new ArrayList<>();
        ArrayList<LootBox> lootboxes = new ArrayList<>();

        if (data instanceof List) {
            for (Object element : data) {
                if (element instanceof List) {
                    for (Object innerElement : (List) element) {
                        if (innerElement instanceof Map) {
                            Map<String, Object> elementMap = (Map<String, Object>) innerElement;
                            String type = (String) elementMap.get("type");

                            if (type.equals("border")) {
                                double id = (double) elementMap.get("id");
                                Map<String, Double> coordsMap = (Map<String, Double>) elementMap.get("coords");
                                Border border = new Border(id, type, new Coordinates(coordsMap.get("latitude"), coordsMap.get("longitude")));
                                borders.add(border);
                            } else if (type.equals("lootbox")) {
                                Map<String, Object> lootBoxMap = (Map<String, Object>) elementMap;
                                double id = (double) lootBoxMap.get("id");
                                Map<String, Double> coordsMap = (Map<String, Double>) lootBoxMap.get("coords");
                                List<Map<String, String>> itemsMaps = (List<Map<String, String>>) lootBoxMap.get("items");

                                List<LootBox.Item> items = new ArrayList<>();
                                for (Map<String, String> itemMap : itemsMaps) {
                                    String itemName = itemMap.get("name");
                                    items.add(new LootBox.Item(itemName));
                                }

                                LootBox lootBox = new LootBox(id, type, new Coordinates(coordsMap.get("latitude"), coordsMap.get("longitude")), items);
                                lootboxes.add(lootBox);
                            }
                        }
                    }
                }
            }
        }
        GameData gameData = new GameData(borders, lootboxes, "test1");
        return gameData;
    }
}