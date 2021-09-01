package TestGps;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpxTest {

    private static final Logger logger = LoggerFactory.getLogger(GpxTest.class);

    public static void main(String[] args) {

        try{

            GPX.read("tracks/demo.gpx").tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .forEach(wayPoint -> {
                        logger.info("Lat: {} - Lng: {} - Time: {}",
                                wayPoint.getLatitude(),
                                wayPoint.getLongitude(),
                                wayPoint.getTime().get());
                    });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
