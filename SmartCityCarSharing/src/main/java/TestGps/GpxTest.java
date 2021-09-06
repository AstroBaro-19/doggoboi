package TestGps;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.geom.Geoid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

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

            final Length length = GPX.read("tracks/demo.gpx").tracks()
                    .flatMap(Track::segments)
                    .findFirst()
                    .map(TrackSegment::points).orElse(Stream.empty())
                    .collect(Geoid.WGS84.toPathLength());

            logger.info("Length: {}",length);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
