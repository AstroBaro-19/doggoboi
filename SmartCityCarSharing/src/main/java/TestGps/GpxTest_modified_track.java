package TestGps;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.geom.Geoid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

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

            //prof method
            final Length length = GPX.read("tracks/demo.gpx").tracks()
                    .flatMap(Track::segments)
                    .findFirst()
                    .map(TrackSegment::points).orElse(Stream.empty())
                    .collect(Geoid.WGS84.toPathLength());

            logger.info("Length: {}",length);


            //caf method
            final Length path_length = GPX.read(GPX_FILE_NAME).wayPoints().collect(Geoid.WGS84.toPathLength());
            logger.info("Length: {}",path_length);



        } catch (IOException e) {
            logger.error("An error occurred. Message: {}",e.getLocalizedMessage());
        }
    }
}
