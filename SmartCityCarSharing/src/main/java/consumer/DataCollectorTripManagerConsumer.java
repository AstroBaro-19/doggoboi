package consumer;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import message.ControlMessage;
import message.TelemetryMessage;
import model.GpsLocationDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.BatterySensorResource;
import resource.GpsGpxSensorResource;
import utils.GpsConsumption;
import utils.GpsDistance;

import java.util.*;
import java.util.stream.Collectors;

public class DataCollectorTripManagerConsumer {

    private final static Logger logger = LoggerFactory.getLogger(DataCollectorTripManagerConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    //E.g. fleet/vehicle/e0c7433d-8457-4a6b-8084-595d500076cc/telemetry/#
    private static final String TARGET_TOPIC = "single/vehicle/+/telemetry/#";

    private static final String GPX_FILE_PARKING = "tracks/ParkingList.gpx";

    public static List<WayPoint> parkingPointList = null;

    private static ObjectMapper mapper;

    private static final double ALARM_BATTERY_LEVEL = 5.0;

    private static final String CONTROL_TOPIC = "control";

    private static final String ALARM_MESSAGE_CONTROL_TYPE = "battery_alarm_message";

    private static final String SUMMARY_TYPE = "summary_control_message";

    private static ArrayList<GpsLocationDescriptor> gpsLocationDescriptorArrayList =new ArrayList<>();

    private static ArrayList<Double> batteryLevelList =new ArrayList<>();

    private static int gpsIncr = 0;

    private static int batteryIncr = 0;

    private static final double batteryCapacity = 0.5; //KWh

    private static double totalConsumption = 0.0;  // battery %

    private static double currentConsumption =0.0; // battery %

    private static double totalDistance = 0.0;  // Km

    private static boolean isAlarmNotified = false;

    public static boolean isPathFinished = false;

    private static double consumptionKwh_Km = 0.0;  // Kwh/Km

    private static double distanceCurrentPark = 0.0;  // meters

    //Initializing distanceMin for Distance currentPoint - nearest ParkingPoint calculation
    private static double distanceMin = 1000.0;

    private static double distanceParkLat; // Parking Car's Latitude

    private static double distanceParkLong; // Parking Car's Longitude


    public static void main(String [ ] args) {

        logger.info("MQTT Consumer Tester Started ...");

        try{

            //Generate a random MQTT client ID using the UUID class
            String clientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(
                    String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT), //Create the URL from IP and PORT
                    clientId,
                    persistence);

            //Define MQTT Connection Options such as reconnection, persistent/clean session and connection timeout
            //Authentication option can be added -> See AuthProducer example
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected ! Client Id: {}", clientId);

            Map<String, Double> batteryHistoryMap = new HashMap<>();

            mapper = new ObjectMapper();

            //Loading the GPX file of available car parking into a list
            parkingPointList= GPX.read(GPX_FILE_PARKING).wayPoints().collect(Collectors.toList());

            logger.info("GPX File Parking WayPoints correctly loaded into the list. List size: {}",parkingPointList.size());


            //Subscribe to the TARGET_TOPIC: single/vehicle/+/telemetry/#
            logger.info("Subscribing to topic: {}", TARGET_TOPIC);

            client.subscribe(TARGET_TOPIC, (topic, msg) -> {

                //logger.info("Received Data (Topic: {}) -> Data: {}", topic, new String(msg.getPayload()));

                //De-serialization plus filtering on received message (msg)
                Optional<TelemetryMessage<Object>> telemetryMessageOptional = parseTelemetryMessagePayload(msg);


                try {

                    /*
                     * De-serialization of Battery's TelemetryMessage
                     */
                    if (telemetryMessageOptional.isPresent() && telemetryMessageOptional.get().getType().equals(BatterySensorResource.RESOURCE_TYPE)) {

                        Double newBatteryLevel = (Double) telemetryMessageOptional.get().getDataValue();

                        logger.info("New Battery Telemetry Data Received - Battery Level: {}", newBatteryLevel);


                        /*
                         * New User run on vehicle, reset the main variables
                         */

                        if (!batteryLevelList.isEmpty() && newBatteryLevel > batteryLevelList.get(batteryIncr)){

                            logger.info("Vehicle recharged, starting new Vehicle Run ...");

                            batteryLevelList.clear();

                            batteryIncr = 0;

                            currentConsumption = 0;

                            isAlarmNotified = false;

                        }


                        batteryLevelList.add(newBatteryLevel);

                        /*
                         * Updating the Battery level consumption using the updated values constantly received by the consumer
                         */
                        try {
                            if (batteryLevelList.size() > 1) {
                                double consumptionBattery = GpsConsumption.consumptionCalc(
                                        batteryLevelList.get(batteryIncr),
                                        newBatteryLevel
                                );

                                totalConsumption += consumptionBattery;

                                currentConsumption += consumptionBattery;

                                consumptionKwh_Km = GpsConsumption.consumptionKwhKm(
                                        totalConsumption,
                                        batteryCapacity,
                                        totalDistance
                                );

                                logger.info("totalBattery_Consumption: {} %  - currentRun_Consumption: {} % - TotalDistance Covered: {} Km - ConsumptionPerKm: {} Kwh/Km",
                                        totalConsumption,
                                        currentConsumption,
                                        totalDistance,
                                        consumptionKwh_Km);

                                batteryIncr++;

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //If is the first value
                        if (!batteryHistoryMap.containsKey(topic) || newBatteryLevel > batteryHistoryMap.get(topic)) {
                            logger.info("New Battery Level Saved for: {}", topic);
                            batteryHistoryMap.put(topic, newBatteryLevel);
                            isAlarmNotified = false;
                        } else {

                            if (isBatteryLevelAlarm(batteryHistoryMap.get(topic), newBatteryLevel) && !isAlarmNotified) {
                                logger.info("BATTERY LEVEL ALARM DETECTED ! Sending Control Notification ...");
                                isAlarmNotified = true;

                                //Sending Topic = single/vehicle/fa18f676-8198-4e9f-90e0-c50a5e419b94/control
                                String controlTopic = String.format("%s/%s", topic.replace("/telemetry/battery", ""), CONTROL_TOPIC);


                                publishControlMessage(client, controlTopic, new ControlMessage(ALARM_MESSAGE_CONTROL_TYPE, new HashMap<>() {
                                    {
                                        put("Parking_Lat", distanceParkLat);
                                        put("Parking_Long", distanceParkLong);
                                        put("Distance (meters)", distanceMin);

                                    }
                                }));
                            }
                        }

                    }

                    /*
                     * De-serialization of Gps's TelemetryMessage
                     */
                    if (telemetryMessageOptional.isPresent() && telemetryMessageOptional.get().getType().equals(GpsGpxSensorResource.RESOURCE_TYPE)) {
                        GpsLocationDescriptor gpsLocationDescriptor = (GpsLocationDescriptor) telemetryMessageOptional.get().getDataValue();
                        logger.info("New Gps Telemetry Data Received - Data: {}", gpsLocationDescriptor);

                        // Adding new received WayPoint into the List
                        gpsLocationDescriptorArrayList.add(gpsLocationDescriptor);


                        /*
                         * Updating Distance between two points, using the gpsLocationDescriptorArrayList
                         * -> Checking the size of the list if the distance calculation is possible
                         */
                        try {
                            if (gpsLocationDescriptorArrayList.size() > 1) {

                                double distance = GpsDistance.distancePath(
                                        gpsLocationDescriptor.getLatitude(),
                                        gpsLocationDescriptorArrayList.get(gpsIncr).getLatitude(),
                                        gpsLocationDescriptor.getLongitude(),
                                        gpsLocationDescriptorArrayList.get(gpsIncr).getLongitude(),
                                        gpsLocationDescriptor.getElevation(),
                                        gpsLocationDescriptorArrayList.get(gpsIncr).getElevation()
                                );

                                totalDistance += distance;


                                /*
                                 * Calculating the minimum distance between the currentWayPoint and the parkingWayPoint (for loop)
                                 */
                                distanceMin = 1000.0;

                                for (WayPoint parkingPoint : parkingPointList) {
                                    distanceCurrentPark = GpsDistance.distanceCurrentPark(gpsLocationDescriptor, parkingPoint);

                                    if (distanceMin == 1000.0 || distanceCurrentPark < distanceMin) {
                                        distanceMin = distanceCurrentPark;
                                        distanceParkLat = parkingPoint.getLatitude().doubleValue();
                                        distanceParkLong = parkingPoint.getLongitude().doubleValue();
                                    }

                                }


                                /*
                                 * Sending Control Message to Producer if there are no more WayPoints available
                                 * Check if the WayPoint List has ended by receiving the same Gps WayPoint
                                 * (Path is Finished) --> Data Recap/Summary for the Producer
                                 */

                                if (Objects.equals(gpsLocationDescriptor.getLatitude(), gpsLocationDescriptorArrayList.get(gpsIncr).getLatitude()) &&
                                        Objects.equals(gpsLocationDescriptor.getLongitude(), gpsLocationDescriptorArrayList.get(gpsIncr).getLongitude()) &&
                                        Objects.equals(gpsLocationDescriptor.getElevation(), gpsLocationDescriptorArrayList.get(gpsIncr).getElevation())) {

                                    String controlTopic = String.format("%s/%s", topic.replace("/telemetry/gps", ""), CONTROL_TOPIC);

                                    publishControlMessage(client, controlTopic, new ControlMessage(SUMMARY_TYPE, new HashMap<>() {
                                        {
                                            put("totalBattery_Consumption (%)", totalConsumption);
                                            put("currentRun_Consumption (%)",currentConsumption);
                                            put("TotalDistance Covered (Km)", totalDistance);
                                            put("Consumption (Kwh/Km)", consumptionKwh_Km);
                                        }
                                    }));

                                }

                                gpsIncr++;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Check if the difference's return value exceeds the Alarm Battery Level threshold
     */
    private static boolean isBatteryLevelAlarm(Double originalBatteryValue, Double newBatteryValue){
        return originalBatteryValue - newBatteryValue >= ALARM_BATTERY_LEVEL;
    }

    /**
     * Filtering the incoming messages, only one Consumer (DataCollector&TripManager)
     *
     * @param mqttMessage as Json Message
     * @return de-serialized message
     */
    private static Optional<TelemetryMessage<Object>> parseTelemetryMessagePayload(MqttMessage mqttMessage){

        try{

            if(mqttMessage == null)
                return Optional.empty();

            byte[] payloadByteArray = mqttMessage.getPayload();
            String payloadString = new String(payloadByteArray);

            if (payloadString.contains(GpsGpxSensorResource.RESOURCE_TYPE)){
                return Optional.ofNullable(mapper.readValue(payloadString, new TypeReference<TelemetryMessage<GpsLocationDescriptor>>() {}));

            }
            else {
                return Optional.ofNullable(mapper.readValue(payloadString, new TypeReference<TelemetryMessage<Double>>() {}));
            }

        }catch (Exception e){
            return Optional.empty();
        }

    }


    private static void publishControlMessage(IMqttClient mqttClient, String topic, ControlMessage controlMessage) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    logger.info("Sending to topic: {} -> Data: {}", topic, controlMessage);

                    if(mqttClient != null && mqttClient.isConnected() && controlMessage != null && topic != null){

                        String messagePayload = mapper.writeValueAsString(controlMessage);

                        MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                        mqttMessage.setQos(1);

                        mqttClient.publish(topic, mqttMessage);

                        logger.info("Data Correctly Published to topic: {}", topic);

                    }
                    else
                        logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

}



