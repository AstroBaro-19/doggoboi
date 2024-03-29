package utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsConsumption {

    private final static Logger logger = LoggerFactory.getLogger(GpsConsumption.class);

    /**
     *
     * @param currentBatteryLevel as BatteryLevel %
     * @param previousBatteryLevel as BatteryLevel %
     * @return BatteryLevel %
     */
    public static double consumptionCalc(Double currentBatteryLevel, Double previousBatteryLevel){
        return currentBatteryLevel - previousBatteryLevel;
    }

    /**
     *  @param totalConsumption as BatteryLevel %
     * @param batteryCapacity in Kwh
     * @param totalDistance in Kilometers
     * @return double Consumption [Kwh/Km]
     */
    public static double consumptionKwhKm(double totalConsumption, double batteryCapacity, double totalDistance) {
        return (totalConsumption * batteryCapacity) / (100 * totalDistance);


    }


}
