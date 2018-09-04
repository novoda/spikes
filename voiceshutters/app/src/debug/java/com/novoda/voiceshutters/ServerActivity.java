package com.novoda.voiceshutters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerActivity extends AppCompatActivity {

    private static final UUID CHARACTERISTIC_WIFI_SSID_UUID = UUID.fromString("aaaaa512-aaaa-aaaa-80c1-9a214cf093ae");
    private static final UUID SERVICE_UUID = UUID.fromString("eeeeee88-df93-11e7-80c1-eeeeeee093ae");

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer bluetoothGattServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e("TUT", "Bluetooth is not available.");
            finish();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Log.e("TUT", "Bluetooth needs to be enabled.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }
        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            Log.e("TUT", "Cannont act as server. Bluetooth advertising not supported.");
            finish();
            return;
        }
        setupServer();
        setupAdvertising();
    }

    private void setupServer() {
        ServerCallback callback = new ServerCallback();
        bluetoothGattServer = bluetoothManager.openGattServer(this, callback);
        callback.setServer(bluetoothGattServer);
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic writeSSIDCharacteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_WIFI_SSID_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        );
        service.addCharacteristic(writeSSIDCharacteristic);
        bluetoothGattServer.addService(service);
    }

    private void setupAdvertising() {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED) // how often to ping out
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW) // range of ping
                .build();
        ParcelUuid parcelUuid = new ParcelUuid(SERVICE_UUID);
        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, callback);
    }

    private static final AdvertiseCallback callback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("TUT", "Server advertising started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("TUT", "Server advertising failed with errorCode: " + errorCode);
        }
    };

    private static class ServerCallback extends BluetoothGattServerCallback {

        private final List<BluetoothDevice> devices = new ArrayList<>();

        private BluetoothGattServer server;

        public void setServer(BluetoothGattServer server) {
            this.server = server;
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                devices.add(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                devices.remove(device);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            if (characteristic.getUuid().equals(CHARACTERISTIC_WIFI_SSID_UUID)) {
                Log.d("TUT", "Received " + new String(value));
                if (server == null) {
                    throw new IllegalStateException(new NullPointerException("server was null, never expected that"));
                }
                server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertising();
        stopServer();
    }

    private void stopAdvertising() {
        bluetoothLeAdvertiser.stopAdvertising(callback);
    }

    private void stopServer() {
        bluetoothGattServer.close();
    }
}
