package me.river.royaltisapi.core.data.json;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A utility class for parsing game data from JSON format.
 */
public class JsonParser {
    private static final Logger logger = LogManager.getLogger(JsonParser.class);

    /**
     * Parses a JSON string into a {@link GameData} object.
     *
     * @param jsonData the JSON string containing game data
     * @return the parsed {@link GameData} object
     */
    public static GameData parseJsonToGameData(String jsonData) {
        Gson gson = new Gson();

        List<Object> data = gson.fromJson(jsonData, List.class);

        ArrayList<Border> borders = new ArrayList<>();
        ArrayList<LootBox> lootboxes = new ArrayList<>();
        MiddlePoint middlePoint = null;
        String gameName = "not set";

        if (data instanceof List) {
            logger.info("Parsing game data from JSON...");
            for (Object element : data) {
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
                                    logger.debug("Parsed border: {}", border);
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
                                    logger.debug("Parsed lootbox: {}", lootBox);
                                    break;

                                case "mapCenter":
                                    Map<String, Double> mapCenterCoordMap = (Map<String, Double>) elementMap.get("coords");
                                    middlePoint = new MiddlePoint(new Coordinates(mapCenterCoordMap.get("latitude"), mapCenterCoordMap.get("longitude")));
                                    logger.debug("Parsed map center: {}", middlePoint);
                                    break;

                                case "gameName":
                                    String gameNameValue = String.valueOf(elementMap.get("value"));
                                    gameName = gameNameValue;
                                    logger.debug("Parsed game name: {}", gameName);
                                    break;

                                default:
                                    logger.warn("Unknown element type: {}", type);
                            }
                        }
                    }
                }
            }
            logger.info("Finished parsing game data.");
        }

        return new GameData(borders, lootboxes, gameName, middlePoint);
    }
}
