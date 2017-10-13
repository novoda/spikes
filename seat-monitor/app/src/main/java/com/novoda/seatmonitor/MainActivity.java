package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.novoda.loadgauge.Ads1015;
import com.novoda.loadgauge.WiiLoadSensor;

import java.io.IOException;

public class MainActivity extends Activity {

    private WiiLoadSensor wiiLoadSensor0;
    private WiiLoadSensor wiiLoadSensor1;
    private WiiLoadSensor wiiLoadSensor2;
    private WiiLoadSensor wiiLoadSensor3;
    private Button button;
    private CloudIotCoreCommunicator cloudIotCoreComms;

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "oncreate");

        String i2CBus = "I2C1";
        Ads1015.Gain gain = Ads1015.Gain.TWO_THIRDS;
        Ads1015.Factory factory = new Ads1015.Factory();
        Ads1015 ads10150x48 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain);
        wiiLoadSensor0 = new WiiLoadSensor(ads10150x48, Ads1015.Channel.ZERO);
        wiiLoadSensor1 = new WiiLoadSensor(ads10150x48, Ads1015.Channel.ONE);
        wiiLoadSensor2 = new WiiLoadSensor(ads10150x48, Ads1015.Channel.TWO);
        wiiLoadSensor3 = new WiiLoadSensor(ads10150x48, Ads1015.Channel.THREE);

        try {
            button = new Button("GPIO_128", Button.LogicState.PRESSED_WHEN_HIGH);
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    wiiLoadSensor0.calibrateToZero();
//                    wiiLoadSensor1.calibrateToZero();
                    Log.d("TUT", "Calibrated");
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Button FooBar", e);
        }
//        wiiLoadSensor0.monitorWeight(sensorAWeightChangedCallback);
//        wiiLoadSensor1.monitorWeight(sensorBWeightChangedCallback);
//        wiiLoadSensor2.monitorWeight(sensorCWeightChangedCallback);
//        wiiLoadSensor3.monitorWeight(sensorDWeightChangedCallback);

        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {

                    int weight0 = wiiLoadSensor0.readWeight();
                    int weight1 = wiiLoadSensor1.readWeight();
                    int weight2 = wiiLoadSensor2.readWeight();
                    int weight3 = wiiLoadSensor3.readWeight();

                    Log.d("TUT", "P0 " + weight0);
                    Log.d("TUT", "P1 " + weight1);
                    Log.d("TUT", "P2 " + weight2);
                    Log.d("TUT", "P3 " + weight3);
                    Log.d("TUT" , "----");

                    SystemClock.sleep(1000);
                }
            }
        }).start(); // TODO proper threading mechanism

//        cloudIotCoreComms = new CloudIotCoreCommunicator.Builder()
//                .withContext(this)
//                .withProjectId("seat-monitor")
//                .withCloudRegion("europe-west1")
//                .withRegistryId("my-devices")
//                // work-desk-nxp
//                // home-attic-raspberry-pi
//                .withDeviceId("work-desk-nxp")
//                .withPrivateKeyRawFileId(R.raw.rsa_private)
//                .build();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                cloudIotCoreComms.connect();
////                cloudIotCoreComms.publishMessage("123HelloWorld");
//            }
//        }).start();
    }

    private final WiiLoadSensor.WeightChangeCallback sensorAWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor A, weight: " + newWeightInKg);
//            cloudIotCoreComms.publishMessage("{\"weight\":\"" + newWeightInKg + "kg" + "\"}");
        }
    };

    private final WiiLoadSensor.WeightChangeCallback sensorBWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor B, weight: " + newWeightInKg);
//            cloudIotCoreComms.publishMessage("{\"weight\":\"" + newWeightInKg + "kg" + "\"}");
        }
    };

    private final WiiLoadSensor.WeightChangeCallback sensorCWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor C, weight: " + newWeightInKg);
//            cloudIotCoreComms.publishMessage("{\"weight\":\"" + newWeightInKg + "kg" + "\"}");
        }
    };

    private final WiiLoadSensor.WeightChangeCallback sensorDWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor D, weight: " + newWeightInKg);
//            cloudIotCoreComms.publishMessage("{\"weight\":\"" + newWeightInKg + "kg" + "\"}");
        }
    };

    @Override
    protected void onDestroy() {
        wiiLoadSensor0.stopMonitoring();
        wiiLoadSensor1.stopMonitoring();
        wiiLoadSensor2.stopMonitoring();
        wiiLoadSensor3.stopMonitoring();
        try {
            button.close();
        } catch (IOException e) {
            // ignore
        }
//        cloudIotCoreComms.disconnect();
        super.onDestroy();
    }
}
