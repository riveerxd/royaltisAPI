package me.river.royaltisapi.core.game;

import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.MiddlePoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void moveBordersTowardsMiddle_validUpdateCount() {
        // Create sample borders
        ArrayList<Border> borders = new ArrayList<>(List.of(
                new Border(1, "type", new Coordinates(50.0, 10.0)),
                new Border(2, "type", new Coordinates(-30.0, -20.0))
        ));

        MiddlePoint middlePoint = new MiddlePoint(new Coordinates(10.0, -5.0));
        int updateCount = 5;

        ArrayList<Border> updatedBorders = Game.moveBordersTowardsMiddle(borders, middlePoint, updateCount);

        // Assertions
        assertEquals(2, updatedBorders.size()); // Same number of borders

        // Border 1 should move closer to the middle
        assertEquals(42.0, updatedBorders.get(0).coords().latitude());
        assertEquals(7.0, updatedBorders.get(0).coords().longitude());

        // Border 2 should also move closer to the middle
        assertEquals(-22.0, updatedBorders.get(1).coords().latitude());
        assertEquals(-17.0, updatedBorders.get(1).coords().longitude());
    }

    @Test
    void moveBordersTowardsMiddle_zeroUpdateCount() {
        ArrayList<Border> borders = new ArrayList<>(List.of(
                new Border(1, "type", new Coordinates(50.0, 10.0)),
                new Border(2, "type", new Coordinates(-30.0, -20.0))
        ));
        MiddlePoint middlePoint = new MiddlePoint(new Coordinates(10.0, -5.0));
        int updateCount = 0;

        ArrayList<Border> updatedBorders = Game.moveBordersTowardsMiddle(borders, middlePoint, updateCount);

        // The borders should not have changed
        assertEquals(borders, updatedBorders);
    }

    @Test
    void moveBordersTowardsMiddle_negativeUpdateCount() {
        ArrayList<Border> borders = new ArrayList<>(List.of(
                new Border(1, "type", new Coordinates(50.0, 10.0)),
                new Border(2, "type", new Coordinates(-30.0, -20.0))
        ));
        MiddlePoint middlePoint = new MiddlePoint(new Coordinates(10.0, -5.0));
        int updateCount = -3;

        ArrayList<Border> updatedBorders = Game.moveBordersTowardsMiddle(borders, middlePoint, updateCount);

        // The borders should not have changed
        assertEquals(borders, updatedBorders);
    }

    // Add more tests to cover different scenarios, such as:
    // - Borders already at the middle point
    // - Borders very close to the middle point
    // - A large number of borders
}
