package device;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.SmartObjectResource;

import java.util.Map;

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
     * Initializing the Smart Object: Id, MqttClient, List of resources
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
                    this.vehicleId!=null &&
                    this.vehicleId.length()>0 &&
                    this.resourceMap!=null &&
                    this.resourceMap.size()>0){

                logger.info("Starting the vehicle emulation...");

                registerToAvailableResource();

            }


        }catch (Exception e){
            logger.error("Error starting the Vehicle Emulatore. Message: {}",e.getLocalizedMessage());
        }
    }

    private void registerToAvailableResource() {
        //TODO - fallo domani
    }

}
