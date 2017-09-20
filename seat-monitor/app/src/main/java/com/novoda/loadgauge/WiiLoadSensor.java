package com.novoda.loadgauge;

import android.util.Log;

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
        int threshold = 1000 + milliVoltsAtRest;

        ads1015.startComparatorDifferential(threshold, comparatorCallback);
    }

    private final Ads1015.ComparatorCallback comparatorCallback = new Ads1015.ComparatorCallback() {
        @Override
        public void onThresholdHit(int valueInMv) {
            if (callback == null) {
                throw new IllegalStateException("Didn't expect comparator to be called with no callback", new NullPointerException("callback is null"));
            }
            Log.d("TUT", "Theshold hit, value: " + valueInMv + "mV");

            // negate the cushion presence (i.e. use the calibration value)
            int realValueInMv = valueInMv - milliVoltsAtRest;
            Log.d("TUT", "Value minus rest: " + realValueInMv + "mV");
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
