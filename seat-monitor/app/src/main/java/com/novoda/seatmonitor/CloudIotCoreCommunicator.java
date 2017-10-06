package com.novoda.seatmonitor;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class CloudIotCoreCommunicator {

    private static final String SERVER_URI = "ssl://mqtt.googleapis.com:8883";

    static class Builder {

        private Context context;
        private String projectId;
        private String cloudRegion;
        private String registryId;
        private String deviceId;
        private int privateKeyRawFileId;

        public Builder withContext(Context context) {
            this.context = context;
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
            MqttAndroidClient client = new MqttAndroidClient(context, SERVER_URI, clientId);
            CloudIotCorePasswordGenerator passwordGenerator = new CloudIotCorePasswordGenerator(projectId, context.getResources(), privateKeyRawFileId);
            return new CloudIotCoreCommunicator(client, deviceId, passwordGenerator);
        }

    }

    private final MqttAndroidClient client;
    private final String deviceId;
    private final CloudIotCorePasswordGenerator passwordGenerator;

    CloudIotCoreCommunicator(MqttAndroidClient client, String deviceId, CloudIotCorePasswordGenerator passwordGenerator) {
        this.client = client;
        this.deviceId = deviceId;
        this.passwordGenerator = passwordGenerator;
    }

    void connect() {
        monitorConnection();
        clientConnect();
        subscribeToConfigChanges();
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

    private void clientConnect() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            // Note that the the Google Cloud IoT Core only supports MQTT 3.1.1, and Paho requires that we explictly set this.
            // If you don't set MQTT version, the server will immediately close its connection to your device.
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            // With Google Cloud IoT Core, the username field is ignored, however it must be set for the
            // Paho client library to send the password field. The password field is used to transmit a JWT to authorize the device.
            connectOptions.setUserName("unused-but-necessary");
            connectOptions.setPassword(passwordGenerator.createJwtRsaPassword());

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
            Log.d("TUT", "IoT Core connection established.");
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

    private void subscribeToConfigChanges() {
        try {
            client.subscribe("/devices/" + deviceId + "/config", 1);
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

    public void publishMessage(String message) {
        // The MQTT topic that this device will publish telemetry data to. The MQTT topic name is
        // required to be in the format below. Note that this is not the same as the device registry's
        // Cloud Pub/Sub topic.
        String topic = "/devices/" + deviceId + "/events";
        String payload = message;
        MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
        mqttMessage.setQos(1);
        try {
            Log.d("TUT", "IoT Core message published. To topic: " + topic);
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disconnect() {
        try {
            Log.d("TUT", "IoT Core connection disconnected.");
            client.disconnect();
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

}
