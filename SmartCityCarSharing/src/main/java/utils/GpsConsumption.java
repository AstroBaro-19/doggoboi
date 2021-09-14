package utils;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsConsumption {

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
     *
     * @param totalConsumption as BatteryLevel %
     * @param batteryCapacity in Kwh
     * @param totalDistance in Kilometers
     * @return Battery's consumption in Kwh/Km
     */
    public static double consumptionKwh(double totalConsumption, double batteryCapacity, double totalDistance) {
        return (totalConsumption*batteryCapacity)/(100*totalDistance);
    }
}
