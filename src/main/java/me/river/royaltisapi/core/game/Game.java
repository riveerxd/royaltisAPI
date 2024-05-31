package me.river.royaltisapi.core.game;

import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.MiddlePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * This class provides utility methods related to game logic.
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    /**
     * Moves the borders of a game towards its middle point.
     *
     * @param borders The original list of borders.
     * @param middlePoint The middle point of the game.
     * @param updateCount The number of updates remaining in the game.
     * @return A new list of borders with updated coordinates.
     */
    public static ArrayList<Border> moveBordersTowardsMiddle(ArrayList<Border> borders, MiddlePoint middlePoint, int updateCount) {
        logger.debug("Moving borders towards middle point. Update count: {}", updateCount);

        if (updateCount <= 0) {
            logger.warn("Invalid update count: {}. Returning original borders.", updateCount);
            return new ArrayList<>(borders);
        }

        Coordinates middleCoords = middlePoint.getCoordinates();

        double moveFactor = 1.0 / updateCount;

        ArrayList<Border> updatedBorders = new ArrayList<>();

        for (Border border : borders) {
            Coordinates currentCoords = border.coords();
            double currentLat = currentCoords.latitude();
            double currentLong = currentCoords.longitude();

            double middleLat = middleCoords.latitude();
            double middleLong = middleCoords.longitude();

            double newLat = currentLat - (currentLat - middleLat) * moveFactor;
            double newLong = currentLong - (currentLong - middleLong) * moveFactor;

            Coordinates updatedCoords = new Coordinates(newLat, newLong);
            Border updatedBorder = new Border(border.id(), border.type(), updatedCoords);
            updatedBorders.add(updatedBorder);

            logger.debug("Border {} moved to: ({}, {})", border.id(), newLat, newLong);
        }

        return updatedBorders;
    }
}
