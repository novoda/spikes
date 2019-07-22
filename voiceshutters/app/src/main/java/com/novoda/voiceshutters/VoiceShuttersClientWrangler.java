package com.novoda.voiceshutters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

class VoiceShuttersClientWrangler {

    private static final UUID SERVICE_UUID_VOICE_SHUTTER = UUID.fromString("abceee88-df93-11e7-80c1-eeeeeee093ae");
    private static final UUID CHAR_UUID_WIFI_SSID = UUID.fromString("aaaaa512-aaaa-aaaa-80c1-9a214cf093ae");
    private static final UUID CHAR_UUID_WIFI_PASS = UUID.fromString("baaaa512-aaaa-aaaa-80c1-9a214cf093ae");
    private static final UUID CHAR_UUID_USER_ID = UUID.fromString("caaaa512-aaaa-aaaa-80c1-9a214cf093ae");
    private static final UUID CHAR_UUID_DEVICE_ID = UUID.fromString("daaaa512-aaaa-aaaa-80c1-9a214cf093ae");

    private final ClientWrangler clientWrangler;

    VoiceShuttersClientWrangler(ClientWrangler clientWrangler) {
        this.clientWrangler = clientWrangler;
    }

    public void scanForDevices(ClientWrangler.ConnectSelectionCallback callback) {
        clientWrangler.scanForDevices(callback);
    }

    void stopScan() {
        clientWrangler.stopScan();
    }

    void sendData(Context context, BluetoothDevice device, String ssid, String password, String userId) {

        // TODO generate device id (cloud function?) then save it to firestore
        // we then need to confirm it has been used for a device or not (and remove if not)

        ClientWrangler.Data data = new ClientWrangler.Data(SERVICE_UUID_VOICE_SHUTTER);
        data.put(CHAR_UUID_WIFI_SSID, ssid)
                .put(CHAR_UUID_WIFI_PASS, password)
                .put(CHAR_UUID_USER_ID, userId)
                .put(CHAR_UUID_DEVICE_ID, "deviceId-TODO generate this");

        clientWrangler.sendData(context, device, data);

        // TODO after this data has been sent to the device, we need to save the device id to Firestore

    }

}
