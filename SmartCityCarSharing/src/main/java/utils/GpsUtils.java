package utils;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-demo-fleet-monitoring
 * @created 04/11/2020 - 20:02
 */
public class GpsUtils {

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        //System.out.println("Distance: " + distance);

        return Math.sqrt(distance);
    }

    /**
     *
     *
     * public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
     *         if ((lat1 == lat2) && (lon1 == lon2)) {
     *             return 0;
     *         }
     *         else {
     *             double theta = lon1 - lon2;
     *             double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
     *             dist = Math.acos(dist);
     *             dist = Math.toDegrees(dist);
     *             dist = dist * 60 * 1.1515;
     *             if (unit.equals("K")) {
     *                 dist = dist * 1.609344;
     *             } else if (unit.equals("N")) {
     *                 dist = dist * 0.8684;
     *             }
     *
     *             System.out.println("Distance: " + dist);
     *
     *             return (dist);
     *         }
     *     }
     */


    public static void distance(Latitude latitude1, Latitude latitude2, Longitude longitude1, Longitude longitude2) {

        final Logger logger = LoggerFactory.getLogger(GpsUtils.class);

        if ((latitude1 == latitude2) && (longitude1 == longitude2)) {
            logger.info("Starting point!");
        }
        else {
            double theta = longitude1.doubleValue() - longitude2.doubleValue();
            double dist = Math.sin(Math.toRadians(latitude1.doubleValue())) * Math.sin(Math.toRadians(latitude2.doubleValue())) + Math.cos(Math.toRadians(latitude1.doubleValue())) * Math.cos(Math.toRadians(latitude2.doubleValue())) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.609344;

            logger.info("Distance: {}", dist);

        }
    }

}
