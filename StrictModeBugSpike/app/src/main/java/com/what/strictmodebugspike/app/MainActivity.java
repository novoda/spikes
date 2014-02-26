package com.what.strictmodebugspike.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    /**
     * Rotating an activity that doesn't do anything! still crashes strict mode:
     * <p/>
     * 02-26 11:55:10.747 E/StrictMode﹕ class com.what.strictmodebugspike.app.MainActivity; instances=2; limit=1
     * android.os.StrictMode$InstanceCountViolation: class com.what.strictmodebugspike.app.MainActivity; instances=2; limit=1
     * at android.os.StrictMode.setClassInstanceLimit(StrictMode.java:1)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("StrictModeFail", "Rotate the device twice I will crash.");
    }
}
