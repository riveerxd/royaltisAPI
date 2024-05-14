package me.river.royaltisapi.core.game;

import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.Coordinates;
import me.river.royaltisapi.core.data.MiddlePoint;

import java.util.ArrayList;

/**
 * The game logic.
 */
public class Game {
    /**
     * Moves the borders towards the middle.
     *
     * @param borders the borders
     * @param middlePoint the middle point
     * @param updateCount the update count
     * @return the updated borders
     */
    public static ArrayList<Border> moveBordersTowardsMiddle(ArrayList<Border> borders, MiddlePoint middlePoint, int updateCount) {
        if (updateCount <= 0) {
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
        }

        return updatedBorders;
    }
}
