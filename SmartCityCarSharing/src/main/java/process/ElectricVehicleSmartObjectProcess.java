package process;

import device.ElectricVehicleSmartObject;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.BatterySensorResource;
import resource.GpsGpxSensorResource;
import java.util.HashMap;
import java.util.UUID;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class ElectricVehicleSmartObjectProcess {

    private static final Logger logger = LoggerFactory.getLogger(ElectricVehicleSmartObjectProcess.class);

    private static String MQTT_BROKER_IP = "127.0.0.1";

    private static int MQTT_BROKER_PORT = 1883;

    public static void main(String[] args) {

        try{

            //Generate Random Vehicle UUID
            String vehicleId = "iot_vehicle_0001";

            //Create MQTT Client
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d",
                    MQTT_BROKER_IP,
                    MQTT_BROKER_PORT),
                    vehicleId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to MQTT Broker
            mqttClient.connect(options);

            logger.info("MQTT Client Connected ! Client Id: {}", vehicleId);

            ElectricVehicleSmartObject electricVehicleSmartObject = new ElectricVehicleSmartObject();
            electricVehicleSmartObject.init(vehicleId, mqttClient, new HashMap<>(){
                {
                    put("gps", new GpsGpxSensorResource());
                    put("battery", new BatterySensorResource());
                }
            });

            electricVehicleSmartObject.start();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}