package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsDistance {

    private final static Logger logger = LoggerFactory.getLogger(GpsDistance.class);

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
    public static double distancePath(Double latitude1, Double latitude2,
                                      Double longitude1, Double longitude2,
                                      Double elevation1, Double elevation2) {

        final int R = 6371; // Radius of the earth

        if ((latitude1 == latitude2) && (longitude1 == longitude2)) {
            logger.info("Starting point.");
        }

        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters


        double height = elevation1 - elevation2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance)/1000;
    }


}
