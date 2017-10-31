package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.blundell.adc.Ads1015;
import com.google.android.things.contrib.driver.button.Button;
import com.novoda.loadgauge.CloudLoadGauge;
import com.novoda.loadgauge.CloudLoadGauges;
import com.novoda.loadgauge.LoadGauge;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String LOCATION = "Liverpool";

    private Button button;

    private Ads1015 ads10150x48;
    private CloudDataTimer cloudDataTimer;
    private CloudLoadGauges cloudLoadGauges;
    private CloudIotCoreCommunicator cloudIotCoreComms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "oncreate");

        String i2CBus = "I2C1";
        Ads1015.Gain gain = Ads1015.Gain.TWO_THIRDS;
        Ads1015.Factory factory = new Ads1015.Factory();
        ads10150x48 = factory.newAds1015(i2CBus, 0x48, gain);
        CloudLoadGauge loadGauge0 = new CloudLoadGauge(LOCATION + "1", new LoadGauge(ads10150x48, Ads1015.Channel.ZERO));
        CloudLoadGauge loadGauge1 = new CloudLoadGauge(LOCATION + "2", new LoadGauge(ads10150x48, Ads1015.Channel.ONE));
        CloudLoadGauge loadGauge2 = new CloudLoadGauge(LOCATION + "3", new LoadGauge(ads10150x48, Ads1015.Channel.TWO));
        CloudLoadGauge loadGauge3 = new CloudLoadGauge(LOCATION + "4", new LoadGauge(ads10150x48, Ads1015.Channel.THREE));
        cloudLoadGauges = CloudLoadGauges.from(loadGauge0, loadGauge1, loadGauge2, loadGauge3);

        try {
            button = new Button("GPIO_128", Button.LogicState.PRESSED_WHEN_HIGH);
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    cloudLoadGauges.calibrateToZero();
                    Log.d("TUT", "Calibrated");
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Button FooBar", e);
        }

        cloudIotCoreComms = new CloudIotCoreCommunicator.Builder()
                .withContext(this)
                .withProjectId("seat-monitor")
                .withCloudRegion("europe-west1")
                .withRegistryId("my-devices")
                .withDeviceId("work-desk-nxp")
                .withPrivateKeyRawFileId(R.raw.rsa_private)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cloudIotCoreComms.connect();
                startContinuousMonitoring();
            }
        }).start();
    }

    private void startContinuousMonitoring() {
        cloudDataTimer = new CloudDataTimer(LOCATION, cloudIotCoreComms, cloudLoadGauges, restartTimer);
        cloudDataTimer.start();
    }

    private final CloudDataTimer.RestartTimer restartTimer = new CloudDataTimer.RestartTimer() {
        @Override
        public void onFinished() {
            startContinuousMonitoring();
        }
    };

    @Override
    protected void onDestroy() {
        cloudDataTimer.cancel();
        cloudIotCoreComms.disconnect();
        ads10150x48.close();
        try {
            button.close();
        } catch (IOException e) {
            // ignore
        }
        super.onDestroy();
    }
}
