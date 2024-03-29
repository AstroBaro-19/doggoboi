package resource;

import consumer.DataCollectorTripManagerConsumer;
import io.jenetics.jpx.*;
import model.GpsLocationDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String GPX_FILE_NAME="tracks/Milan.gpx";

    private GpsLocationDescriptor updatedGpsLocationDescriptor= null;

    private Timer updateTimer = null;

    private  List<WayPoint> wayPointList=null;

    private  ListIterator<WayPoint> wayPointListIterator;


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

            this.wayPointList = GPX.read(GPX_FILE_NAME).wayPoints().collect(Collectors.toList());

            logger.info("GPX File WayPoints correctly loaded into the list. List size: {}",wayPointList.size());

            this.wayPointListIterator = this.wayPointList.listIterator();

            periodicEventUpdate();

        }
        catch (Exception e){

            logger.error("Error during the initialization. Message: {}",e.getLocalizedMessage());
        }
    }

    /**
     * -> Start periodic Location Update from existing and available Gpx wayPoints, using a Timer
     * -> If the path is finished, Timer will be canceled, no more Gps Location updates
     * -> Sending twice last WayPoint will trigger the SummaryData's Control Message
     */
    private void periodicEventUpdate() {

        logger.info("Starting new Timer task ... Starts in {} ms ... Update Period: {} ms", TASK_DELAY_TIME, UPDATE_PERIOD);

        this.updateTimer = new Timer();

        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(wayPointListIterator.hasNext()){

                    WayPoint currentWayPoint = wayPointListIterator.next();

                    updatedGpsLocationDescriptor = new GpsLocationDescriptor(
                            currentWayPoint.getLatitude().doubleValue(),
                            currentWayPoint.getLongitude().doubleValue(),
                            (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0),
                            GpsLocationDescriptor.FILE_LOCATION_PROVIDER);

                    notifyUpdate(updatedGpsLocationDescriptor);

                }
                else {
                    DataCollectorTripManagerConsumer.isPathFinished = true;

                    //Notify the Consumer this is the last point on the list, receiving the same coordinates twice
                    notifyUpdate(updatedGpsLocationDescriptor);

                    updateTimer.cancel();

                }
            }
        }, TASK_DELAY_TIME, UPDATE_PERIOD);
    }


    @Override
    public GpsLocationDescriptor loadUpdatedValue() {
        return updatedGpsLocationDescriptor;
    }

    public static void main(String[] args) {
        GpsGpxSensorResource gpsGpxSensorResource = new GpsGpxSensorResource();
        logger.info("New {} Resource Created with Id: {} Updated Value: {}",
                gpsGpxSensorResource.getType(),
                gpsGpxSensorResource.getId(),
                gpsGpxSensorResource.loadUpdatedValue());

        //Adding new Resource Listener
        gpsGpxSensorResource.addDataListener(new ResourceDataListener<GpsLocationDescriptor>() {
            @Override
            public void onDataChange(ResourceDataListener<GpsLocationDescriptor> resource, GpsLocationDescriptor updatedValue) {
                if (resource!=null && updatedValue!=null)
                    logger.info("Device Id: {} - New updated GPX WayPoint received: {}",gpsGpxSensorResource.getId() ,updatedValue);
                else
                    logger.error("onDataChange Callback: Null Resource or Updated Value...");
            }
        });

    }

}