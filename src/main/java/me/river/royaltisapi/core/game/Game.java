package me.river.royaltisapi.core.game;

import me.river.royaltisapi.core.data.Border;
import me.river.royaltisapi.core.data.Coordinates;
import me.river.royaltisapi.core.data.MiddlePoint;

import java.util.ArrayList;

public class Game {
    public static ArrayList<Border> moveBordersTowardsMiddle(ArrayList<Border> borders, MiddlePoint middlePoint, int updateCount) {
        if (updateCount <= 0) {
            return new ArrayList<>(borders);
        }

        Coordinates middleCoords = middlePoint.getCoordinates();

        double moveFactor = 1.0 / updateCount;

        ArrayList<Border> updatedBorders = new ArrayList<>();

        for (Border border : borders) {
            Coordinates currentCoords = border.getCoords();
            double currentLat = currentCoords.getLatitude();
            double currentLong = currentCoords.getLongitude();

            double middleLat = middleCoords.getLatitude();
            double middleLong = middleCoords.getLongitude();

            double newLat = currentLat - (currentLat - middleLat) * moveFactor;
            double newLong = currentLong - (currentLong - middleLong) * moveFactor;

            Coordinates updatedCoords = new Coordinates(newLat, newLong);
            Border updatedBorder = new Border(border.getId(), border.getType(), updatedCoords);
            updatedBorders.add(updatedBorder);
        }

        return updatedBorders;
    }
}
