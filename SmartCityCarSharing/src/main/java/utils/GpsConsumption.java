package utils;

public class GpsConsumption {

    public static double consumptionCalc(Double currentBatteryLevel, Double previousBatteryLevel){
        return currentBatteryLevel - previousBatteryLevel;
    }

}
