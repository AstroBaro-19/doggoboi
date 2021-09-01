package TestGps;

import io.jenetics.jpx.GPX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpxTest_modified_track {

    private static final Logger logger = LoggerFactory.getLogger(GpxTest_modified_track.class);

    private static String GPX_FILE_NAME="tracks/Milan_Cathedral.gpx";

    public static void main(String[] args) {
        try {
            GPX.read(GPX_FILE_NAME).wayPoints().forEach(wayPoint -> {
                logger.info("Latitude: {} - Longitude: {} - Elevation:{} - Time: {}",
                        wayPoint.getLatitude(),
                        wayPoint.getLongitude(),
                        wayPoint.getElevation().get(),
                        wayPoint.getTime().get());
            });
        } catch (IOException e) {
            logger.error("An error occurred. Message: {}",e.getLocalizedMessage());
        }
    }
}
