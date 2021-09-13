package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.DataCollectorTripManagerConsumer;
import message.TelemetryMessage;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.BatterySensorResource;
import resource.GpsGpxSensorResource;
import resource.ResourceDataListener;
import resource.SmartObjectResource;

import java.util.Map;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class ElectricVehicleSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(ElectricVehicleSmartObject.class);

    private static final String BASIC_TOPIC= "single/vehicle";

    private static final String TELEMETRY_TOPIC= "telemetry";

    private static final String CONTROL_TOPIC= "control";

    private static final String EVENT_TOPIC= "event";

    private String vehicleId;

    // Used for the serialization / de-serialization
    private ObjectMapper mapper;

    private IMqttClient mqttClient;

    private Map<String, SmartObjectResource> resourceMap;


    public ElectricVehicleSmartObject() {
        this.mapper = new ObjectMapper();
    }

    public ElectricVehicleSmartObject(String vehicleId, ObjectMapper mapper) {
        this.vehicleId = vehicleId;
        this.mapper = mapper;
    }

    /**
     * Initializing the Smart Object: {vehicleId, MqttClient, Map of resources K-V}
     * Notes:
     * -> Map<String, SmartObjectResource> resourceMap --> we use the Resource List
     * -> Every class that extends SmartObjectResource (abstract) can be added
     * @param vehicleId
     * @param mqttClient
     * @param resourceMap
     */
    public void init(String vehicleId, IMqttClient mqttClient, Map<String, SmartObjectResource> resourceMap){
        this.vehicleId= vehicleId;
        this.mqttClient= mqttClient;
        this.resourceMap= resourceMap;

        logger.info("Electric Vehicle Smart Object correctly created. Number of resources: {}", resourceMap.keySet().size());
    }

    /**
     * Starts the emulated vehicle
     * -> searches for available resources (then subscribes to it)
     * -> notify possible update value
     */
    public void start(){
        try {

            if (this.mqttClient.isConnected() &&
                    this.vehicleId!=null && this.vehicleId.length()>0 &&
                    this.resourceMap!=null && this.resourceMap.size()>0){

                logger.info("Starting the vehicle emulation...");

                registerToControlChannel();

                registerToAvailableResources();

            }


        }catch (Exception e){
            logger.error("Error starting the Vehicle Emulator. Message: {}",e.getLocalizedMessage());
        }
    }

    private void registerToAvailableResources() {
        try{
            this.resourceMap.entrySet().forEach(resourceEntry ->{

                if (resourceEntry.getKey()!=null && resourceEntry.getValue()!=null){
                    SmartObjectResource smartObjectResource = resourceEntry.getValue();

                    // Registration message
                    logger.info("Registering to Resource {} (Id: {}) notifications...",
                            smartObjectResource.getType(),
                            smartObjectResource.getId());

                    // effective registration
                    if (smartObjectResource.getType().equals(GpsGpxSensorResource.RESOURCE_TYPE) ||
                            smartObjectResource.getType().equals(BatterySensorResource.RESOURCE_TYPE)){
                            smartObjectResource.addDataListener(new ResourceDataListener() {
                                @Override
                                public void onDataChange(ResourceDataListener resource, Object updatedValue) throws MqttException {

                                    String topic = String.format("%s/%s/%s/%s",BASIC_TOPIC,vehicleId,TELEMETRY_TOPIC,resourceEntry.getKey());

                                    try {
                                        publishTelemetryData(topic, new TelemetryMessage(smartObjectResource.getType(),updatedValue));
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                    }

                }

            });
        }
        catch (Exception e){
            logger.error("Error during the registration. Message: {}",e.getLocalizedMessage());
        }
    }

    private void registerToControlChannel() {

        try{

            String deviceControlTopic = String.format("%s/%s/%s", BASIC_TOPIC, vehicleId, CONTROL_TOPIC);

            logger.info("Registering to Control Topic ({}) ... ", deviceControlTopic);

            this.mqttClient.subscribe(deviceControlTopic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    if(message != null)
                        logger.info("[CONTROL CHANNEL] -> Control Message Received -> {}", new String(message.getPayload()));
                    else
                        logger.error("[CONTROL CHANNEL] -> Null control message received !");
                }
            });

        }catch (Exception e){
            logger.error("ERROR Registering to Control Channel ! Msg: {}", e.getLocalizedMessage());
        }
    }


    private void publishTelemetryData(String topic, TelemetryMessage telemetryMessage) throws MqttException, JsonProcessingException {

        logger.info("Sending to topic: {} - data: {}",topic, telemetryMessage);

        // setting correct environment - multiple controls
        if (this.mqttClient!=null && this.mqttClient.isConnected() && telemetryMessage!=null && topic!=null){

            // String creation - serialized Payload
            String messagePayload = mapper.writeValueAsString(telemetryMessage);

            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);
            mqttClient.publish(topic, mqttMessage);

            logger.info("Data correctly published. Published to topic: {}",topic);

        }

    }

}
