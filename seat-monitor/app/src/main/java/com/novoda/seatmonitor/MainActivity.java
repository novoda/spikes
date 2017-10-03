package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.novoda.loadgauge.Ads1015;
import com.novoda.loadgauge.WiiLoadSensor;

import java.io.IOException;

public class MainActivity extends Activity {

    private WiiLoadSensor wiiLoadSensorA;
    //    private WiiLoadSensor wiiLoadSensorB;
    private Button button;
    private CloudIotCoreCommunicator cloudIotCoreComms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "oncreate");

        String i2CBus = "I2C1";
        Ads1015.Gain gain = Ads1015.Gain.ONE;
        String alertReadyPin = "GPIO_39";
        Ads1015.Factory factory = new Ads1015.Factory();
//        Ads1015 ads10150x48 = factory.newDifferentialComparatorInstance(i2CBus, 0x48, gain, alertReadyPin, PINS_0_1);
//        Ads1015 ads10150x48 = factory.newDifferentialReaderInstance(i2CBus, 0x48, gain, PINS_0_1);
//        Ads1015 ads10150x48 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain, Ads1015.Channel.ZERO);
        Ads1015 ads10150x48 = factory.newSingleEndedComparatorInstance(i2CBus, 0x48, gain, alertReadyPin, Ads1015.Channel.ZERO);
        wiiLoadSensorA = new WiiLoadSensor(ads10150x48);
//        Ads1015 ads10150x49 = factory.newDifferentialComparatorInstance(i2CBus, 0x49, gain, alertReadyPin, PINS_2_3);
//        wiiLoadSensorB = new WiiLoadSensor(ads10150x49);

        try {
            button = new Button("GPIO_128", Button.LogicState.PRESSED_WHEN_HIGH);
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    wiiLoadSensorA.calibrateToZero();
//                    wiiLoadSensorB.calibrateToZero();
                    Log.d("TUT", "Calibrated");
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Button FooBar", e);
        }
        wiiLoadSensorA.monitorForWeightChangeOver(50, sensorAWeightChangedCallback);

        cloudIotCoreComms = new CloudIotCoreCommunicator.Builder()
                .withContext(this)
                .withServerURI("ssl://mqtt.googleapis.com:8883")
                .withProjectId("seat-monitor")
                .withCloudRegion("europe-west1")
                .withRegistryId("my-devices")
                // work-desk-nxp
                // home-attic-raspberry-pi
                .withDeviceId("work-desk-nxp")
                .withPrivateKeyRawFileId(R.raw.rsa_private)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cloudIotCoreComms.connect();
//                cloudIotCoreComms.sendMessage("123HelloWorld");
            }
        }).start();
    }

    private final WiiLoadSensor.WeightChangeCallback sensorAWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor A, weight: " + newWeightInKg + "kg");

            cloudIotCoreComms.sendMessage(newWeightInKg + "kg");
        }
    };

    @Override
    protected void onDestroy() {
        wiiLoadSensorA.stopMonitoring();
//        wiiLoadSensorB.stopMonitoring();
        try {
            button.close();
        } catch (IOException e) {
            // ignore
        }
        cloudIotCoreComms.disconnect();
        super.onDestroy();
    }
}
