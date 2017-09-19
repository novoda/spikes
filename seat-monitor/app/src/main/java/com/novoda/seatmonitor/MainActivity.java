package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private Handler handler;
    private Ads1015 ads1015;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ads1015 = new Ads1015.Factory().newInstance("I2C1", 0x48, Ads1015.Gain.ONE);
        Log.d("TUT", "oncreate");
        HandlerThread thread = new HandlerThread("backgroundMeasure");
        thread.start();

        handler = new Handler(thread.getLooper());
        handler.post(loop);
    }

    private final Runnable loop = new Runnable() {
        @Override
        public void run() {

            Log.d("TUT", "0 & 1 Differential " + ads1015.readADCDifferentialBetween0And1());
            Log.d("TUT", "2 & 3 Differential " + ads1015.readADCDifferentialBetween2And3());

            // TODO define a "change"
            // If there is a "change"
            // Record that change in firebase

            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
        }
    };

}
