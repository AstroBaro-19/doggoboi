package consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jenetics.jpx.WayPoint;
import message.TelemetryMessage;
import model.GpsLocationDescriptor;
import resource.GpsGpxSensorResource;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GpsDistance;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gps Monitoring
 *
 *@authors -Alessandro Baroni,Simone Brunelli,Riccardo Mari
 *@project -Smart City Car Sharing
 */

public class GpsMonitoringConsumer {

    private final static Logger logger = LoggerFactory.getLogger(GpsMonitoringConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    //E.g. fleet/vehicle/e0c7433d-8457-4a6b-8084-595d500076cc/telemetry/gps
    private static final String TARGET_TOPIC = "single/vehicle/+/telemetry/gps";

    private static ObjectMapper mapper;

    private static ArrayList<GpsLocationDescriptor> gpsLocationDescriptorArrayList =new ArrayList<>();
    private static ListIterator<GpsLocationDescriptor> gpsLocationDescriptorListIterator=null;

    private static int i=0;
    private static double totalDistance=0;



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

            mapper = new ObjectMapper();

            //Subscribe to the target topic #. In that case the consumer will receive (if authorized) all the message
            //passing through the broker
            logger.info("Subscribing to topic: {}", TARGET_TOPIC);

            client.subscribe(TARGET_TOPIC, (topic, msg) -> {

                //logger.info("Received Data (Topic: {}) -> Data: {}", topic, new String(msg.getPayload()));

                // Telemetry Message's De-serialization
                Optional<TelemetryMessage<GpsLocationDescriptor>> telemetryMessageOptional = parseTelemetryMessagePayload(msg);

                if(telemetryMessageOptional.isPresent() && telemetryMessageOptional.get().getType().equals(GpsGpxSensorResource.RESOURCE_TYPE)){

                    GpsLocationDescriptor gpsLocationDescriptor = telemetryMessageOptional.get().getDataValue();

                    logger.info("New Gps Telemetry Data Received ! Data: {}",gpsLocationDescriptor);


                    gpsLocationDescriptorArrayList.add(gpsLocationDescriptor);

                    logger.info("CurrentPoint: {}",gpsLocationDescriptor);
                    logger.info("PointforCalc-> Lat: {} - Long: {}",gpsLocationDescriptorArrayList.get(i).getLatitude(),gpsLocationDescriptorArrayList.get(i).getLongitude());

                    try {
                        if (gpsLocationDescriptorArrayList.size()>1){

                            double distance = GpsDistance.distancePath(
                                    gpsLocationDescriptor.getLatitude(),
                                    gpsLocationDescriptorArrayList.get(i).getLatitude(),
                                    gpsLocationDescriptor.getLongitude(),
                                    gpsLocationDescriptorArrayList.get(i).getLongitude(),
                                    gpsLocationDescriptor.getElevation(),
                                    gpsLocationDescriptorArrayList.get(i).getElevation()
                            );
                            totalDistance += distance;

                            logger.info("Updating Total Distance: {} meters", totalDistance);

                            i++;
                        }
                        else {
                            logger.info("Waiting for new Gps Waypoints ...");
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static Optional<TelemetryMessage<GpsLocationDescriptor>> parseTelemetryMessagePayload(MqttMessage mqttMessage){

        try{

            if(mqttMessage == null)
                return Optional.empty();

            byte[] payloadByteArray = mqttMessage.getPayload();
            String payloadString = new String(payloadByteArray);

            return Optional.ofNullable(mapper.readValue(payloadString, new TypeReference<TelemetryMessage<GpsLocationDescriptor>>() {}));

        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


    //TODO - publishControlMessage used to notify the SmartObject vehicle every km about energy consumption/km

    /**
     *
     * @param mqttClient
     * @param topic
     * @param controlMessage
     * @throws MqttException
     * @throws JsonProcessingException
     *
     *
     *
     * private static void publishControlMessage(IMqttClient mqttClient, String topic, ControlMessage controlMessage) throws MqttException, JsonProcessingException {
     *
     *         new Thread(new Runnable() {
     *             @Override
     *             public void run() {
     *
     *                try{
     *
     *                    logger.info("Sending to topic: {} -> Data: {}", topic, controlMessage);
     *
     *                    if(mqttClient != null && mqttClient.isConnected() && controlMessage != null && topic != null){
     *
     *                        String messagePayload = mapper.writeValueAsString(controlMessage);
     *
     *                        MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
     *                        mqttMessage.setQos(0);
     *
     *                        mqttClient.publish(topic, mqttMessage);
     *
     *                        logger.info("Data Correctly Published to topic: {}", topic);
     *
     *                    }
     *                    else
     *                        logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
     *
     *                }catch (Exception e){
     *                    e.printStackTrace();
     *                }
     *
     *             }
     *         }).start();
     *     }
     *
     *
     */

}
