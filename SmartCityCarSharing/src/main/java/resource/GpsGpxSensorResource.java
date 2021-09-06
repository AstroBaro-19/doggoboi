package resource;

import io.jenetics.jpx.*;
import io.jenetics.jpx.geom.Geoid;
import model.GpsLocationDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GpsUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsGpxSensorResource extends SmartObjectResource<GpsLocationDescriptor>{

    private static final Logger logger = LoggerFactory.getLogger(BatterySensorResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:gps";

    private static final long UPDATE_PERIOD = 1000; //1 Second for the updated value to come out

    private static final long TASK_DELAY_TIME = 5000; //Seconds before starting the periodic update task

    private static final String GPX_FILE_NAME="tracks/Milan_Cathedral.gpx";

    private GpsLocationDescriptor updatedGpsLocationDescriptor= null;

    private Timer updateTimer = null;

    private List<WayPoint> wayPointList= null;

    private ListIterator<WayPoint> wayPointListIterator;
    private int i=-1;

    public GpsGpxSensorResource() {
        super(UUID.randomUUID().toString(),GpsGpxSensorResource.RESOURCE_TYPE);
        init();
    }

    public GpsGpxSensorResource(String id, String type) {
        super(id, type);
        init();

    }

    /**
     * Load from GPX_FILE_NAME the Gpx wayPoints - Creating a List of wayPoints
     * Start periodic Location Update from existing and available Gpx wayPoints, using a Timer
     */
    private void init() {
        try {

            this.updatedGpsLocationDescriptor = new GpsLocationDescriptor();

            this.wayPointList=GPX.read(GPX_FILE_NAME).wayPoints().collect(Collectors.toList());
            logger.info("GPX File WayPoints correctly loaded into the list. List size: {}",wayPointList.size());

            //Using a class called Iterator, we are able to go through the collection, in this case a list of Gpx WayPoints
            this.wayPointListIterator=this.wayPointList.listIterator();

            periodicEventUpdate();

            //forLoopEventUpdate();

        }
        catch (Exception e){
            logger.error("Error during the initialization. Message: {}",e.getLocalizedMessage());
        }
    }

    private void periodicEventUpdate() {

        logger.info("Starting new Timer task ... Starts in {} ms ... Update Period: {} ms", TASK_DELAY_TIME, UPDATE_PERIOD);

        this.updateTimer = new Timer();

        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (wayPointListIterator.hasNext()){

                    WayPoint currentWayPoint = wayPointListIterator.next();
                    i++;

                    updatedGpsLocationDescriptor=new GpsLocationDescriptor(
                            currentWayPoint.getLatitude().doubleValue(),
                            currentWayPoint.getLongitude().doubleValue(),
                            (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0),
                            GpsLocationDescriptor.FILE_LOCATION_PROVIDER
                    );

                    //Notify the Listener after data changing
                    notifyUpdate(updatedGpsLocationDescriptor);

                    GpsUtils.distance(
                            currentWayPoint.getLatitude(),
                            wayPointList.listIterator().next().getLatitude(),
                            currentWayPoint.getLongitude(),
                            wayPointList.listIterator().next().getLongitude()
                            );

                }

                else{

                    logger.info("No more WayPoints available ...");

                    //Stopping the periodic event
                    updateTimer.cancel();

                    //distance covered
                    try {

                        final Length path_length = GPX.read(GPX_FILE_NAME).wayPoints().collect(Geoid.WGS84.toPathLength());
                        logger.info("Length: {}",path_length);
                    }

                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, TASK_DELAY_TIME, UPDATE_PERIOD);

    }














    private void forLoopEventUpdate() throws InterruptedException {
        for (int i=0; i< wayPointList.size(); i++){
            if (wayPointListIterator.hasNext()){

                WayPoint currentWayPoint = wayPointListIterator.next();
                logger.info("currentWayPoint: {}",currentWayPoint);

                updatedGpsLocationDescriptor=new GpsLocationDescriptor(
                        currentWayPoint.getLatitude().doubleValue(),
                        currentWayPoint.getLongitude().doubleValue(),
                        (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0),
                        GpsLocationDescriptor.FILE_LOCATION_PROVIDER
                );

                //Notify the Listener after data changing
                notifyUpdate(updatedGpsLocationDescriptor);
                Thread.sleep(1000);

            }

    }
        logger.info("No more WayPoints available ...");

        //distance covered
        try {

            final Length path_length = GPX.read(GPX_FILE_NAME).wayPoints().collect(Geoid.WGS84.toPathLength());
            logger.info("Length: {}",path_length);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
}


    @Override
    public GpsLocationDescriptor loadUpdatedValue() {
        return updatedGpsLocationDescriptor;
    }

    public static void main(String[] args) {
        GpsGpxSensorResource gpsGpxSensorResource= new GpsGpxSensorResource();
        logger.info("New {} Resource Created with Id: {} Updated Value: {}",
                gpsGpxSensorResource.getType(),
                gpsGpxSensorResource.getId(),
                gpsGpxSensorResource.loadUpdatedValue());

        //Adding new Resource Listener
        gpsGpxSensorResource.addDataListener(new ResourceDataListener<GpsLocationDescriptor>() {
            @Override
            public void onDataChange(ResourceDataListener<GpsLocationDescriptor> resource, GpsLocationDescriptor updatedValue) {
                if (resource!=null && updatedValue!=null)
                    logger.info("Device Id: {} New updated GPX WayPoint received: {}",gpsGpxSensorResource.getId() ,updatedValue);
                else
                    logger.error("onDataChange Callback: Null Resource or Updated Value...");
            }
        });

    }

}
