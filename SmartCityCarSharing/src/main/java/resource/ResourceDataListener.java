package resource;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Creating generic Interface to handle generic Sensor Resources with several data types: Double, String, Integer, ...
 * @param <T>
 */
public interface ResourceDataListener<T> {
    // (modified resource, modified value)
    public void onDataChange(ResourceDataListener<T> resource, T updatedValue) throws MqttException;
}
