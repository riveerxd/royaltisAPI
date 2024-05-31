package me.river.royaltisapi.core.data.json;

import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.LootBox;
import me.river.royaltisapi.core.data.records.Border;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the `JsonParser` class.
 * Verifies the correct parsing of JSON data into a `GameData` object, ensuring
 * that borders, lootboxes, map center, and game name are extracted accurately.
 */
class JsonParserTest {

    /**
     * Mock JSON data representing a game configuration.
     * Includes borders, lootboxes with items, map center, and game name.
     */
    private static final String mockJsonData = "[[{id:624,coords:{longitude:14.493614472448828,latitude:50.121581998991516},type:border},{id:574,coords:{longitude:14.494679309427738,latitude:50.12710286340908},type:border},{id:648,coords:{longitude:14.509771764278412,latitude:50.12348161448829},type:border},{id:720,coords:{longitude:14.50565155595541,latitude:50.10777726694413},type:border},{id:438,coords:{longitude:14.487595930695532,latitude:50.11208236069811},type:border},{id:998,coords:{longitude:14.485743865370752,latitude:50.11825743673137},type:border}],[{id:110,coords:{longitude:14.504025131464005,latitude:50.11680070483245},type:lootbox,items:[{name:AK-47},{name:P2020}]},{id:598,coords:{longitude:14.493023715913294,latitude:50.1152637358336},type:lootbox,items:[{name:Granade}]}],[{type:mapCenter,id:134,coords:{longitude:14.503072276711464,latitude:50.12021501165355}}],[{type:gameName,value:test}]]";

    /**
     * Tests the `parseJsonToGameData` method with mock JSON data.
     * Verifies that the parsed `GameData` object contains the correct number
     * of borders, lootboxes, items within lootboxes, and the expected map center
     * and game name.
     */
    @Test
    void testParseJsonToGameData() {
        GameData gameData = JsonParser.parseJsonToGameData(mockJsonData);

        // Assertions for borders
        assertEquals(6, gameData.getBorders().size(), "Incorrect number of borders parsed");
        assertTrue(gameData.getBorders().get(0) instanceof Border, "Parsed object is not of Border type");

        // Assertions for lootboxes
        assertEquals(2, gameData.getLootboxes().size(), "Incorrect number of lootboxes parsed");
        LootBox firstLootBox = gameData.getLootboxes().get(0);
        assertEquals(110, firstLootBox.getId(), "Incorrect lootbox ID");
        assertEquals(2, firstLootBox.getItems().size(), "Incorrect number of items in lootbox");
        assertEquals("AK-47", firstLootBox.getItems().get(0).getName(), "Incorrect item name");

        // Assertions for mapCenter and gameName
        assertNotNull(gameData.getMiddlePoint(), "Map center should not be null");
        assertEquals(14.503072276711464, gameData.getMiddlePoint().getCoordinates().longitude(),
                "Incorrect map center longitude");
        assertEquals("test", gameData.getGameName(), "Incorrect game name");
    }
}
