package utils;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;


import java.util.Optional;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsDistance {

    /**
     * Calculate distance between two points in latitude and longitude taking
     * also height as parameter.
     *
     * @param latitude1 in meters
     * @param latitude2 in meters
     * @param longitude1 in meters
     * @param longitude2 in meters
     * @param elevation1 in meters
     * @param elevation2 in meters
     * @return Distance in Meters
     */
    public static double distance(Latitude latitude1, Latitude latitude2,
                                  Longitude longitude1, Longitude longitude2,
                                  Optional<Length> elevation1, Optional<Length> elevation2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(latitude2.doubleValue() - latitude1.doubleValue());
        double lonDistance = Math.toRadians(longitude2.doubleValue() - longitude1.doubleValue());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1.doubleValue())) * Math.cos(Math.toRadians(latitude2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = elevation1.get().doubleValue()  - elevation2.get().doubleValue();

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
