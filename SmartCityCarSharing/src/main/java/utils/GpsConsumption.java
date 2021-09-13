package utils;

public class GpsConsumption {

    public static double consumptionCalc(Double currentBatteryLevel, Double previousBatteryLevel){
        return currentBatteryLevel - previousBatteryLevel;
    }


    public static double consumptionKwh(double totalConsumption, double batteryCapacity, double totalDistance) {
        return (totalConsumption*batteryCapacity)/(100*totalDistance);
    }
}
