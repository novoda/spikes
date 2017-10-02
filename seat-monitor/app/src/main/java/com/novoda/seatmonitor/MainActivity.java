package com.novoda.seatmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.novoda.loadgauge.WiiLoadSensor;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class MainActivity extends Activity {

    private WiiLoadSensor wiiLoadSensorA;
    private WiiLoadSensor wiiLoadSensorB;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "oncreate");

//        String i2CBus = "I2C1";
//        Ads1015.Gain gain = Ads1015.Gain.ONE;
//        String alertReadyPin = "GPIO23";
//        Ads1015.Factory factory = new Ads1015.Factory();
//        Ads1015 ads10150x48 = factory.newDifferentialComparatorInstance(i2CBus, 0x48, gain, alertReadyPin, PINS_0_1);
//        wiiLoadSensorA = new WiiLoadSensor(ads10150x48);
//        Ads1015 ads10150x49 = factory.newDifferentialComparatorInstance(i2CBus, 0x49, gain, alertReadyPin, PINS_2_3);
//        wiiLoadSensorB = new WiiLoadSensor(ads10150x49);
//
//        try {
//            button = new Button("GPIO11", Button.LogicState.PRESSED_WHEN_HIGH);
//            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
//                @Override
//                public void onButtonEvent(Button button, boolean pressed) {
//                    wiiLoadSensorA.calibrateToZero();
//                    wiiLoadSensorB.calibrateToZero();
//                }
//            });
//        } catch (IOException e) {
//            throw new IllegalStateException("Button FooBar", e);
//        }
//
//        wiiLoadSensorA.monitorForWeightChangeOver(50, sensorAWeightChangedCallback);

        practiceMQtt();
    }

    private void practiceMQtt() {
        String serverURI = "ssl://mqtt.googleapis.com:8883";
        String projectId = "seat-monitor";
        String cloudRegion = "europe-west1";
        String registryId = "my-devices";
        String deviceId = "home-attic-raspberry-pi";
        String clientId = "projects/" + projectId + "/locations/" + cloudRegion + "/registries/" + registryId + "/devices/" + deviceId + "";
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(MainActivity.this, serverURI, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("TUT", "connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TUT", "message arrived " + topic + " MSG " + message);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("TUT", "delivery complete " + token);

            }
        });

        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            // Note that the the Google Cloud IoT Core only supports MQTT 3.1.1, and Paho requires that we
            // explictly set this. If you don't set MQTT version, the server will immediately close its
            // connection to your device.
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            // With Google Cloud IoT Core, the username field is ignored, however it must be set for the
            // Paho client library to send the password field. The password field is used to transmit a JWT
            // to authorize the device.
            connectOptions.setUserName("unused");
            connectOptions.setPassword(createJwtRsa(projectId, R.raw.rsa_private).toCharArray());

            mqttAndroidClient.connect(connectOptions);
        } catch (MqttException e) {
            Log.e("TUT", "fail", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("TUT", "fail", e);
        } catch (InvalidKeySpecException e) {
            Log.e("TUT", "fail", e);
        }
    }

    private String createJwtRsa(String projectId, int rawFileId) throws NoSuchAlgorithmException, InvalidKeySpecException {
        LocalDateTime now = LocalDateTime.now();
        // Create a JWT to authenticate this device. The device will be disconnected after the token
        // expires, and will have to reconnect with a new token. The audience field should always be set
        // to the GCP project id.
        JwtBuilder jwtBuilder =
                Jwts.builder()
                        .setIssuedAt(Date.from(now.toInstant(ZoneOffset.MIN)))
                        .setExpiration(Date.from(now.plusMinutes(20).toInstant(ZoneOffset.MIN)))
                        .setAudience(projectId);

        InputStream inStream = getResources().openRawResource(rawFileId);

        byte[] keyBytes = Base64.decode(toString(inStream), Base64.DEFAULT);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, privateKey).compact();
    }

    private String toString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private final WiiLoadSensor.WeightChangeCallback sensorAWeightChangedCallback = new WiiLoadSensor.WeightChangeCallback() {
        @Override
        public void onWeightChanged(float newWeightInKg) {
            Log.d("TUT", "sensor A, weight: " + newWeightInKg + "kg");

            // TODO Record that change in firebase / iot core

        }
    };

    @Override
    protected void onDestroy() {
        wiiLoadSensorA.stopMonitoring();
        wiiLoadSensorB.stopMonitoring();
        try {
            button.close();
        } catch (IOException e) {
            // ignore
        }
        super.onDestroy();
    }
}
