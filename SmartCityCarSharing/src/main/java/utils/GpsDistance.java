package utils;

import io.jenetics.jpx.*;
import io.jenetics.jpx.geom.Geoid;
import model.GpsLocationDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


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
     * @return Distance in Kilometers
     */
    public static double distancePath(Double latitude1, Double latitude2,
                                      Double longitude1, Double longitude2,
                                      Double elevation1, Double elevation2) {

        final int R = 6371; // Radius of the earth

        if ((Objects.equals(latitude1, latitude2)) && (Objects.equals(longitude1, longitude2))) {
            logger.info("Same Gps WayPoint received. Path is finished ...");
        }

        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convert to meters


        double height = elevation1 - elevation2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance)/1000;  // Convert to Km
    }

    /**
     * Calculate the distance between two points using jpx Library
     *
     * @param gpsLocationDescriptor as GpsWayPoint
     * @param parkingPoint as GpsWayPoint
     * @return Distance in meters (as the crow flies)
     */
    public static double distanceCurrentPark(GpsLocationDescriptor gpsLocationDescriptor, WayPoint parkingPoint) {
        final Point start = WayPoint.of(gpsLocationDescriptor.getLatitude(), gpsLocationDescriptor.getLongitude());
        final Point end = WayPoint.of(parkingPoint.getLatitude(), parkingPoint.getLongitude());
        return Geoid.WGS84.distance(start, end).doubleValue();
    }
}
