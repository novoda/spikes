package com.novoda.seatmonitor;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CloudIotCoreCommunicator {

    static class Builder {

        private Context context;
        private String serverURI;
        private String projectId;
        private String cloudRegion;
        private String registryId;
        private String deviceId;
        private int privateKeyRawFileId;

        public Builder withContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder withServerURI(String serverURI) {
            this.serverURI = serverURI;
            return this;
        }

        public Builder withProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder withCloudRegion(String cloudRegion) {
            this.cloudRegion = cloudRegion;
            return this;
        }

        public Builder withRegistryId(String registryId) {
            this.registryId = registryId;
            return this;
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withPrivateKeyRawFileId(int privateKeyRawFileId) {
            this.privateKeyRawFileId = privateKeyRawFileId;
            return this;
        }

        public CloudIotCoreCommunicator build() {
            if (context == null) {
                throw new IllegalStateException("context must not be null");
            }

            if (serverURI == null) {
                throw new IllegalStateException("serverURI must not be null");
            }

            if (projectId == null) {
                throw new IllegalStateException("projectId must not be null");
            }
            if (cloudRegion == null) {
                throw new IllegalStateException("cloudRegion must not be null");
            }
            if (registryId == null) {
                throw new IllegalStateException("registryId must not be null");
            }
            if (deviceId == null) {
                throw new IllegalStateException("deviceId must not be null");
            }
            String clientId = "projects/" + projectId + "/locations/" + cloudRegion + "/registries/" + registryId + "/devices/" + deviceId;

            if (privateKeyRawFileId == 0) {
                throw new IllegalStateException("privateKeyRawFileId must not be 0");
            }
            InputStream inStream = context.getResources().openRawResource(privateKeyRawFileId);
            byte[] privateKeyBytes = Base64.decode(inputToString(inStream), Base64.DEFAULT);

            MqttAndroidClient client = new MqttAndroidClient(context, serverURI, clientId);
            return new CloudIotCoreCommunicator(client, projectId, deviceId, privateKeyBytes);
        }

        private String inputToString(InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    private final MqttAndroidClient client;
    private final String projectId;
    private final String deviceId;
    private final byte[] privateKeyBytes;

    CloudIotCoreCommunicator(MqttAndroidClient client, String projectId, String deviceId, byte[] privateKeyBytes) {
        this.client = client;
        this.projectId = projectId;
        this.deviceId = deviceId;
        this.privateKeyBytes = privateKeyBytes;
    }

    void practiceMQtt() {
        monitorConnection();
        connect();
    }

    private void monitorConnection() {
        client.setCallback(new MqttCallback() {
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
    }

    private void connect() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            // Note that the the Google Cloud IoT Core only supports MQTT 3.1.1, and Paho requires that we explictly set this.
            // If you don't set MQTT version, the server will immediately close its connection to your device.
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            // With Google Cloud IoT Core, the username field is ignored, however it must be set for the
            // Paho client library to send the password field. The password field is used to transmit a JWT to authorize the device.
            connectOptions.setUserName("unused-but-necessary");
            connectOptions.setPassword(createJwtRsa(projectId, privateKeyBytes).toCharArray());

            IMqttToken iMqttToken = client.connect(connectOptions);
            iMqttToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("TUT", "success, connected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("TUT", "failure, not connected", exception);
                }
            });
            iMqttToken.waitForCompletion(TimeUnit.SECONDS.toMillis(30));

            sendMessage();
            Log.d("TUT", "MQTT hacking is complete master.");

        } catch (MqttException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
        Log.d("TUT", "MQTT setup is complete master.");
    }

    private static String createJwtRsa(String projectId, byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        LocalDateTime now = LocalDateTime.now();
        // Create a JWT to authenticate this device. The device will be disconnected after the token
        // expires, and will have to reconnect with a new token. The audience field should always be set
        // to the GCP project id.
        Date issueDate = Date.from(now.minusDays(1).toInstant(ZoneOffset.MIN)); // TODO DATE HACK????
        Log.d("TUT", "JWT issue date: " + issueDate);
        JwtBuilder jwtBuilder =
                Jwts.builder()
                        .setIssuedAt(issueDate)
                        .setExpiration(Date.from(now.plusMinutes(20).toInstant(ZoneOffset.MIN)))
                        .setAudience(projectId);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, privateKey).compact();
    }

    private void sendMessage() {
        String topic = "/devices/" + deviceId + "/events";
        String payload = "registryId" + "/" + deviceId + "-payload-" + "1HelloWorld"; // TODO does this need to be a sepcific format?
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        try {
            Log.d("TUT", "publish");
            client.publish(topic, message);
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disconnect() {
        try {
            Log.d("TUT", "dcon");
            client.disconnect();
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

}
