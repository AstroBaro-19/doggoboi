package consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.TelemetryMessage;
import model.GpsLocationDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.BatterySensorResource;
import resource.GpsGpxSensorResource;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class totalConsumer {

    private final static Logger logger = LoggerFactory.getLogger(totalConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    //E.g. fleet/vehicle/e0c7433d-8457-4a6b-8084-595d500076cc/telemetry/#
    private static final String TARGET_TOPIC = "single/vehicle/+/telemetry/#";

    //------------------------------------------------------------------------
    private static ObjectMapper mapperGps;

    private static ArrayList<GpsLocationDescriptor> gpsLocationDescriptorArrayList =new ArrayList<>();

    private static int i=0;

    private static double totalDistance=0;

    private static final String ALARM_MESSAGE_CONTROL_TYPE = "battery_alarm_message";

    private static ObjectMapper mapperBattery;

    private static boolean isAlarmNotified = false;
    //----------------------------------------------------------------------------

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

            mapperGps = new ObjectMapper();
            mapperBattery = new ObjectMapper();


            //Subscribe to the target topic #. In that case the consumer will receive (if authorized) all the message
            //passing through the broker
            logger.info("Subscribing to topic: {}", TARGET_TOPIC);

            client.subscribe(TARGET_TOPIC, (topic, msg) -> {

                logger.info("Received Data (Topic: {}) -> Data: {}", topic, new String(msg.getPayload()));


                //De-serialization
                try {
                    Optional<TelemetryMessage<Double>> telemetryMessageOptionalBattery = parseTelemetryMessagePayloadBattery(msg);
                    if(telemetryMessageOptionalBattery.isPresent() && telemetryMessageOptionalBattery.get().getType().equals(BatterySensorResource.RESOURCE_TYPE)) {

                        Double newBatteryLevel = telemetryMessageOptionalBattery.get().getDataValue();
                        logger.info("New Battery Telemetry Data Received ! Battery Level: {}", newBatteryLevel);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Optional<TelemetryMessage<GpsLocationDescriptor>> telemetryMessageOptionalGps = parseTelemetryMessagePayloadGps(msg);
                    if(telemetryMessageOptionalGps.isPresent() && telemetryMessageOptionalGps.get().getType().equals(GpsGpxSensorResource.RESOURCE_TYPE)) {

                        GpsLocationDescriptor gpsLocationDescriptor = telemetryMessageOptionalGps.get().getDataValue();

                        logger.info("New Gps Telemetry Data Received ! Data: {}", gpsLocationDescriptor);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }






            });






        }

        catch (Exception e){
            e.printStackTrace();}
    }


    private static Optional<TelemetryMessage<GpsLocationDescriptor>> parseTelemetryMessagePayloadGps(MqttMessage mqttMessage){

        try{

            if(mqttMessage == null)
                return Optional.empty();

            byte[] payloadByteArray = mqttMessage.getPayload();
            String payloadString = new String(payloadByteArray);

            return Optional.ofNullable(mapperGps.readValue(payloadString, new TypeReference<TelemetryMessage<GpsLocationDescriptor>>() {}));

        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


    private static Optional<TelemetryMessage<Double>> parseTelemetryMessagePayloadBattery(MqttMessage mqttMessage){

        try{

            if(mqttMessage == null)
                return Optional.empty();

            byte[] payloadByteArray = mqttMessage.getPayload();
            String payloadString = new String(payloadByteArray);

            return Optional.ofNullable(mapperBattery.readValue(payloadString, new TypeReference<TelemetryMessage<Double>>() {}));

        }catch (Exception e){
            return Optional.empty();
        }
    }


}


