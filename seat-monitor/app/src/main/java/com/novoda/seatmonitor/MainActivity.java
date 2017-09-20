package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.novoda.loadgauge.Ads1015;

public class MainActivity extends Activity {

    private Handler handler;
    private Ads1015 ads10150x48;
    private Ads1015 ads10150x49;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ads10150x48 = new Ads1015.Factory().newInstance("I2C1", 0x48, Ads1015.Gain.ONE, "GPIO23");
        ads10150x49 = new Ads1015.Factory().newInstance("I2C1", 0x49, Ads1015.Gain.ONE, "GPIO23");
        Log.d("TUT", "oncreate");
        HandlerThread thread = new HandlerThread("backgroundMeasure");
        thread.start();

        // TODO button to calibrate
        // on calibrate = current value = 0
        // have a calibration value (base threshold on this value)

        // TODO instead of looping and polling every second
        // set a threshold and use the Trigger GPIO callback
        // then get the reading

        ads10150x48.startComparatorADCDifferentialBetween0And1(1000, comparatorCallback);

    }

    private final Ads1015.ComparatorCallback comparatorCallback = new Ads1015.ComparatorCallback() {
        @Override
        public void onThresholdHit(int valueInMv) {
            Log.d("TUT", "0x48 0 & 1 Differential " + valueInMv + "mV");

            // negate the cushion presence (i.e. use the calibration value)
            // convert to KG

            // TODO Record that change in firebase / iot core

        }
    };

    @Override
    protected void onDestroy() {
        ads10150x48.close();
        ads10150x49.close();
        super.onDestroy();
    }
}
