package com.novoda.loadgauge;

public class WiiLoadSensor {

    private final Ads1015 ads1015;

    private WeightChangeCallback callback;
    private int milliVoltsAtRest;

    public WiiLoadSensor(Ads1015 ads1015) {
        this.ads1015 = ads1015;
    }

    public void calibrateToZero() {
        // Get the current load on the sensor store this value as "0"
        milliVoltsAtRest = ads1015.readDifferential();
    }

    public void monitorForWeightChangeOver(float weightInKg, WeightChangeCallback callback) {
        this.callback = callback;

        // TODO convert weightInKg to mV
        int thresholdInMv = 0 + milliVoltsAtRest;

//        ads1015.startComparatorDifferential(thresholdInMv, comparatorCallback);
        ads1015.startComparatorSingleEnded(ads1015.readSingleEnded() + 1, comparatorCallback);
    }

    private final Ads1015.ComparatorCallback comparatorCallback = new Ads1015.ComparatorCallback() {
        @Override
        public void onThresholdHit(float valueInMv) {
            if (callback == null) {
                throw new IllegalStateException("Didn't expect comparator to be called with no callback", new NullPointerException("callback is null"));
            }
            // negate the cushion presence (i.e. use the calibration value)
//            float realValueInMv = valueInMv - milliVoltsAtRest;
//            Log.d("TUT", "Value minus rest: " + realValueInMv + "mV");
            // TODO convert valueInMv to kg
            float weightInKg = 50;

            callback.onWeightChanged(weightInKg);
        }
    };

    public void stopMonitoring() {
        ads1015.close();
    }

    public interface WeightChangeCallback {
        void onWeightChanged(float newWeightInKg);
    }

}
