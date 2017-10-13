package com.novoda.loadgauge;

public class WiiLoadSensor {

    private final Ads1015 ads1015;
    private final Ads1015.Channel channel;

    private WeightChangeCallback callback;
    private int milliVoltsAtRest;

    public WiiLoadSensor(Ads1015 ads1015, Ads1015.Channel channel) {
        this.ads1015 = ads1015;
        this.channel = channel;
    }

    public void calibrateToZero() {
        // Get the current load on the sensor store this value as "0"
        milliVoltsAtRest = ads1015.read(channel);
    }

    public void monitorWeight(WeightChangeCallback callback) {
        this.callback = callback;
    }

    public int readWeight() {
        int result = ads1015.read(channel);

//        Log.d("TUT", "Current result: " + result);
        // negate the cushion presence (i.e. use the calibration value)
//            float realValueInMv = valueInMv - milliVoltsAtRest;
//            Log.d("TUT", "Value minus rest: " + realValueInMv + "mV");
        // TODO convert valueInMv to kg
        return result;
    }

    public void stopMonitoring() {
        ads1015.close();
    }

    public interface WeightChangeCallback {
        void onWeightChanged(float newWeightInKg);
    }

}
