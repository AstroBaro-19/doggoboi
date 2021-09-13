package resource;


import consumer.DataCollectorTripManagerConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

/**
 * Modelling Battery Sensor Resource extending the generic abstract class "SmartObjectResource"
 */
public class BatterySensorResource extends SmartObjectResource<Double> {

    private static final Logger logger = LoggerFactory.getLogger(BatterySensorResource.class);

    private static final double MIN_BATTERY_LEVEL = 50.0;

    private static final double MAX_BATTERY_LEVEL = 70.0;

    private static final double MIN_BATTERY_LEVEL_CONSUMPTION = 0.1;

    private static final double MAX_BATTERY_LEVEL_CONSUMPTION = 1.0;

    private static final long UPDATE_PERIOD = 5000; //5 Seconds for the updated value to come out

    private static final long TASK_DELAY_TIME = 5000; //Seconds before starting the periodic update task

    public static final String RESOURCE_TYPE = "iot:sensor:battery";

    private double updatedBatteryLevel;

    private Random random = null;

    private Timer updateTimer = null;

    public BatterySensorResource() {
        super(UUID.randomUUID().toString(),BatterySensorResource.RESOURCE_TYPE);
        init();
    }

    public BatterySensorResource(String id, String type) {
        super(id, type);
        init();

    }

    /**
     * Initializing new random Battery Level for the scooter
     * range of the battery: {MIN_BATTERY_LEVEL, MAX_BATTERY_LEVEL}
     *
     * Modify Battery Level using a Timer
     */
    private void init() {

        try{

            this.random = new Random(System.currentTimeMillis());
            this.updatedBatteryLevel= MIN_BATTERY_LEVEL + (this.random.nextDouble()*(MAX_BATTERY_LEVEL - MIN_BATTERY_LEVEL));

            periodicEventUpdate();

        }
        catch(Exception e){
            logger.error("Error occurred during the process. Message: {}",e.getLocalizedMessage());
        }

    }

    /**
     * Periodic Task: Randomize Battery Level consumption
     */
    private void periodicEventUpdate() {

        logger.info("Starting new Timer task ... Starts in {} ms ... Update Period: {} ms", TASK_DELAY_TIME, UPDATE_PERIOD);

        this.updateTimer = new Timer();
        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                updatedBatteryLevel = updatedBatteryLevel - (MIN_BATTERY_LEVEL_CONSUMPTION + (MAX_BATTERY_LEVEL_CONSUMPTION*random.nextDouble()));
                //logger.info("Updated Battery Level: {}", updatedBatteryLevel);

                //Notify the Listener after data changing
                notifyUpdate(updatedBatteryLevel);

            }
        }, TASK_DELAY_TIME, UPDATE_PERIOD);

    }

    @Override
    public Double loadUpdatedValue() {
        return this.updatedBatteryLevel;
    }

    public static void main(String[] args) {

        BatterySensorResource batterySensorResource= new BatterySensorResource();

        logger.info("New {} Resource Created with Id: {} Battery Level: {}",
                batterySensorResource.getType(),
                batterySensorResource.getId(),
                batterySensorResource.loadUpdatedValue());

        //Adding new Resource Listener
        batterySensorResource.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChange(ResourceDataListener<Double> resource, Double updatedValue) {
                if (resource!=null && updatedValue!=null)
                    logger.info("Device Id: {} New updated Battery Level received: {}",batterySensorResource.getId() ,updatedValue);
                else
                    logger.error("onDataChange Callback: Null Resource or Updated Value...");
            }
        });
    }
}
