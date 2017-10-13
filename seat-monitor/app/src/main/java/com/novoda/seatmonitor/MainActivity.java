package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;
import com.novoda.loadgauge.Ads1015;
import com.novoda.loadgauge.WiiLoadSensor;

import java.io.IOException;

public class MainActivity extends Activity {

    private WiiLoadSensor wiiLoadSensorA;
    private WiiLoadSensor wiiLoadSensorB;
    private WiiLoadSensor wiiLoadSensorC;
    private WiiLoadSensor wiiLoadSensorD;
    private Button button;
    private CloudIotCoreCommunicator cloudIotCoreComms;

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "oncreate");

        String i2CBus = "I2C1";
        I2cDevice i2cDevice;
        try {
            PeripheralManagerService service = new PeripheralManagerService();
            i2cDevice = service.openI2cDevice(i2CBus, 0x48);
        } catch (IOException e) {
            throw new IllegalStateException("Can't open 0x48", e);
        }

        Ads1015.Gain gain = Ads1015.Gain.TWO_THIRDS;
        Ads1015.Factory factory = new Ads1015.Factory();
        Ads1015 ads10150x48Channel0 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain, Ads1015.Channel.ZERO, i2cDevice);
        Ads1015 ads10150x48Channel1 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain, Ads1015.Channel.ONE, i2cDevice);
        Ads1015 ads10150x48Channel2 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain, Ads1015.Channel.TWO, i2cDevice);
        Ads1015 ads10150x48Channel3 = factory.newSingleEndedReaderInstance(i2CBus, 0x48, gain, Ads1015.Channel.THREE, i2cDevice);
        wiiLoadSensorA = new WiiLoadSensor(ads10150x48Channel0);
        wiiLoadSensorB = new WiiLoadSensor(ads10150x48Channel1);
        wiiLoadSensorC = new WiiLoadSensor(ads10150x48Channel2);
        wiiLoadSensorD = new WiiLoadSensor(ads10150x48Channel3);
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
//        wiiLoadSensorA.monitorWeight(sensorAWeightChangedCallback);
//        wiiLoadSensorB.monitorWeight(sensorBWeightChangedCallback);
//        wiiLoadSensorC.monitorWeight(sensorCWeightChangedCallback);
//        wiiLoadSensorD.monitorWeight(sensorDWeightChangedCallback);

        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {

                    int weight0 = wiiLoadSensorA.readWeight();
                    int weight1 = wiiLoadSensorB.readWeight();
                    int weight2 = wiiLoadSensorC.readWeight();
                    int weight3 = wiiLoadSensorD.readWeight();

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
        wiiLoadSensorA.stopMonitoring();
        wiiLoadSensorB.stopMonitoring();
        wiiLoadSensorC.stopMonitoring();
        wiiLoadSensorD.stopMonitoring();
        try {
            button.close();
        } catch (IOException e) {
            // ignore
        }
//        cloudIotCoreComms.disconnect();
        super.onDestroy();
    }
}
