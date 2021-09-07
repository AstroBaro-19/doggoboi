package resource;

import io.jenetics.jpx.*;
import model.GpsLocationDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GpsUtils;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsGpxSensorResource extends SmartObjectResource<GpsLocationDescriptor>{

    private static final Logger logger = LoggerFactory.getLogger(GpsGpxSensorResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:gps";

    private static final long UPDATE_PERIOD = 1000; //1 Second for the updated value to come out

    private static final long TASK_DELAY_TIME = 5000; //Seconds before starting the periodic update task

    private static final String GPX_FILE_NAME="tracks/Milan_Cathedral.gpx";

    private GpsLocationDescriptor updatedGpsLocationDescriptor= null;

    private Timer updateTimer = null;

    private List<WayPoint> wayPointList= null;

    private ListIterator<WayPoint> wayPointListIterator;

    private double totalDistance = 0;

    // Index-List Iterators {initialization}
    private int i=0;
    private int j=1;


    // Constructors
    public GpsGpxSensorResource() {
        super(UUID.randomUUID().toString(),GpsGpxSensorResource.RESOURCE_TYPE);
        init();
    }

    public GpsGpxSensorResource(String id, String type) {
        super(id, type);
        init();

    }


    /**
     * -> Load from GPX_FILE_NAME the Gpx wayPoints - Creating a List of wayPoints
     * -> PeriodicEventUpdate
     */
    private void init() {
        try {

            this.updatedGpsLocationDescriptor = new GpsLocationDescriptor();


            this.wayPointList=GPX.read(GPX_FILE_NAME).wayPoints().collect(Collectors.toList());


            logger.info("GPX File WayPoints correctly loaded into the list. List size: {}",wayPointList.size());

            //Using a class called Iterator, we are able to go through the collection, in this case a list of Gpx WayPoints
            this.wayPointListIterator=this.wayPointList.listIterator();

            periodicEventUpdate();

        }
        catch (Exception e){
            logger.error("Error during the initialization. Message: {}",e.getLocalizedMessage());
        }
    }

    /**
     * -> Start periodic Location Update from existing and available Gpx wayPoints, using a Timer
     * -> Live: updating distance covered every second (based on available nextPoint in the list)
     */
    private void periodicEventUpdate() {

        logger.info("Starting new Timer task ... Starts in {} ms ... Update Period: {} ms", TASK_DELAY_TIME, UPDATE_PERIOD);

        this.updateTimer = new Timer();

        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (wayPointListIterator.hasNext()){

                    try{
                        WayPoint newCurrentWayPoint= wayPointList.listIterator(i).next();
                        WayPoint newNextWayPoint = wayPointList.listIterator(j).next();

                        //logger.info("current: {} - next: {}",newCurrentWayPoint,newNextWayPoint);

                        updatedGpsLocationDescriptor=new GpsLocationDescriptor(
                                newCurrentWayPoint.getLatitude().doubleValue(),
                                newCurrentWayPoint.getLongitude().doubleValue(),
                                (newCurrentWayPoint.getElevation().isPresent() ? newCurrentWayPoint.getElevation().get().doubleValue() : 0.0),
                                GpsLocationDescriptor.FILE_LOCATION_PROVIDER
                        );

                        //Notify the Listener after data changing
                        notifyUpdate(updatedGpsLocationDescriptor);

                        // Calculate Distance
                        double distance = GpsUtils.distance(
                                newCurrentWayPoint.getLatitude(),
                                newNextWayPoint.getLatitude(),
                                newCurrentWayPoint.getLongitude(),
                                newNextWayPoint.getLongitude(),
                                newCurrentWayPoint.getElevation(),
                                newNextWayPoint.getElevation()
                        );
                        totalDistance += distance;

                        // Increasing the indexes
                        i++;
                        j++;

                        //logger.info("Updating Total Distance: {} meters", totalDistance);
                    }

                    catch (Exception e){

                        updateTimer.cancel();

                        logger.error("No more WayPoints available in the list ... Total Distance covered: {} meters",totalDistance);

                    }
                }
            }
        }, TASK_DELAY_TIME, UPDATE_PERIOD);
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