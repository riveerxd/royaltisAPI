package me.river.royaltisapi.core.game;

import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.MiddlePoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the `Game` class, specifically focusing on the
 * `moveBordersTowardsMiddle` method.
 */
class GameTest {

    /**
     * Tests the correct behavior of `moveBordersTowardsMiddle` when the
     * `updateCount` is valid and positive. Verifies that the borders move
     * closer to the specified middle point by the expected amount.
     */
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
        assertEquals(2, updatedBorders.size(), "The number of borders should remain the same");

        // Border 1 should move closer to the middle
        assertEquals(42.0, updatedBorders.get(0).coords().latitude(), "Border 1 latitude is incorrect");
        assertEquals(7.0, updatedBorders.get(0).coords().longitude(), "Border 1 longitude is incorrect");

        // Border 2 should also move closer to the middle
        assertEquals(-22.0, updatedBorders.get(1).coords().latitude(), "Border 2 latitude is incorrect");
        assertEquals(-17.0, updatedBorders.get(1).coords().longitude(), "Border 2 longitude is incorrect");
    }

    /**
     * Tests the behavior when `updateCount` is zero. Expects the borders
     * to remain unchanged in this case.
     */
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
        assertEquals(borders, updatedBorders, "Borders should be unchanged when updateCount is zero");
    }

    /**
     * Checks the scenario where `updateCount` is negative.
     * Like the zero case, expects no changes to the borders.
     */
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
        assertEquals(borders, updatedBorders, "Borders should be unchanged when updateCount is negative");
    }
}
