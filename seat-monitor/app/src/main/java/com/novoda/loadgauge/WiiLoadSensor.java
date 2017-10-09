package com.novoda.loadgauge;

import android.os.SystemClock;
import android.util.Log;

public class WiiLoadSensor {

    private final Ads1015 ads1015;

    private WeightChangeCallback callback;
    private int milliVoltsAtRest;
    private boolean running;

    public WiiLoadSensor(Ads1015 ads1015) {
        this.ads1015 = ads1015;
    }

    public void calibrateToZero() {
        // Get the current load on the sensor store this value as "0"
        milliVoltsAtRest = ads1015.readDifferential();
    }

    public void monitorWeight(WeightChangeCallback callback) {
        this.callback = callback;

        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {

                    if (WiiLoadSensor.this.callback == null) {
                        throw new IllegalStateException("Didn't expect comparator to be called with no callback", new NullPointerException("callback is null"));
                    }

                    int result = readWeight();

                    WiiLoadSensor.this.callback.onWeightChanged(50);

                    SystemClock.sleep(1000);
                }
            }
        }).start(); // TODO proper threading mechanism

    }

    private int readWeight() {
        int result = ads1015.readSingleEnded();

        Log.d("TUT", "Current result: " + result);
        // negate the cushion presence (i.e. use the calibration value)
//            float realValueInMv = valueInMv - milliVoltsAtRest;
//            Log.d("TUT", "Value minus rest: " + realValueInMv + "mV");
        // TODO convert valueInMv to kg
        return result;
    }

    public void stopMonitoring() {
        running = false;
        ads1015.close();
    }

    public interface WeightChangeCallback {
        void onWeightChanged(float newWeightInKg);
    }

}
