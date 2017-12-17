package com.novoda.voiceshutters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class ClientWrangler {

    private static final long SCAN_DURATION = TimeUnit.SECONDS.toMillis(15);

    private final BluetoothAdapter adapter;
    private final BluetoothLeScanner scanner;
    private final Handler handler;

    private boolean currentlyScanning;
    private MyScanCallback scanCallback;
    private boolean connected;
    private boolean characteristicsInitialised;
    private BluetoothGatt bluetoothGatt;
    private ConnectSelectionCallback connectSelectionCallback;

    ClientWrangler(BluetoothAdapter adapter, BluetoothLeScanner scanner, Handler handler) {
        this.adapter = adapter;
        this.scanner = scanner;
        this.handler = handler;
    }

    public void scanForDevices(ConnectSelectionCallback callback) {
        this.connectSelectionCallback = callback;
        if (currentlyScanning) {
            Log.e("TUT", "Already scanning");
            return;
        }
        Log.d("TUT", "starting scan");
        startScan();
    }

    private void startScan() {
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
//                .setServiceUuid(new ParcelUuid(SERVICE_UUID_VOICE_SHUTTER))
                .setDeviceName("VoiceShutters")
                .build();
        filters.add(filter);
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        scanCallback = new MyScanCallback();
        scanner.startScan(filters, settings, scanCallback);
        currentlyScanning = true;
        handler.postDelayed(this::stopScan, SCAN_DURATION);
    }

    private static class MyScanCallback extends ScanCallback {

        private final Map<String, BluetoothDevice> results = new HashMap<>();

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            results.put(deviceAddress, device);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("TUT", "Scan failed with errorCode " + errorCode);
        }

        Map<String, BluetoothDevice> getResults() {
            return results;
        }
    }

    void stopScan() {
        if (currentlyScanning && adapter.isEnabled()) {
            scanner.stopScan(scanCallback);
            currentlyScanning = false;
        }
        scanComplete();
    }

    private void scanComplete() {
        Log.d("TUT", "scan complete");
        Map<String, BluetoothDevice> results = scanCallback.getResults();
        for (String deviceAddress : results.keySet()) {
            BluetoothDevice device = results.get(deviceAddress);
            Log.d("TUT", "Found device: " + device.getName() + "/" + deviceAddress + "/" + Arrays.toString(device.getUuids()));
        }
        connectSelectionCallback.onDevicesFoundForSelection(new ArrayList<>(results.values()));
    }

    interface ConnectSelectionCallback {
        void onDevicesFoundForSelection(List<BluetoothDevice> devices);
    }

    void sendData(Context context, BluetoothDevice device, Data data) {
        bluetoothGatt = device.connectGatt(context, false, new GattClientCallback(data));
    }

    private class GattClientCallback extends BluetoothGattCallback {

        private final Data data;

        private GattClientCallback(Data data) {
            this.data = data;
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_FAILURE) {
                disconnectGattServer(gatt);
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectGattServer(gatt);
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("TUT", "connected");
                connected = true;
                Log.d("TUT", "discovering services");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnectGattServer(gatt);
            }
        }

        private void disconnectGattServer(BluetoothGatt gatt) {
            connected = false;
            gatt.disconnect();
            gatt.close();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("TUT", "discovered services but not in a successful state " + status);
                return;
            }
            Log.d("TUT", "Found services");
            BluetoothGattService service = gatt.getService(data.getServiceUUID());

            boolean init = true;
            for (int i = 0; i < data.getTotal(); i++) {
                init &= setupService(gatt, service, data.getUUID(i));
            }

            characteristicsInitialised = init;
            sendData(data);
        }

        private boolean setupService(BluetoothGatt gatt, BluetoothGattService service, UUID uuid) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            return gatt.setCharacteristicNotification(characteristic, true);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("TUT", "Wrote " + characteristic.getUuid() + " : " + new String(characteristic.getValue()));
        }
    }

    private void sendData(Data data) {
        if (!connected) {
            Log.e("TUT", "Gatt server must be connected to send messages");
        }
        if (!characteristicsInitialised) {
            Log.e("TUT", "Characteristics must be initialised to send messages");
        }
        new Thread(() -> {

            BluetoothGattService service = bluetoothGatt.getService(data.getServiceUUID());
            boolean success = true;
            for (int i = 0; i < data.getTotal(); i++) {
                success &= writeCharacteristic(service, data.getUUID(i), data.getDataValue(i));
            }
            Log.d("TUT", "Wrote chars " + (success ? "success" : "failure"));

        }).start();
    }

    public static class Data {

        private final UUID serviceUUID;
        private final List<UUID> key = new ArrayList<>();
        private final List<String> value = new ArrayList<>();

        public Data(UUID serviceUUID) {
            this.serviceUUID = serviceUUID;
        }

        public Data put(UUID uuid, String data) {
            key.add(uuid);
            value.add(data);
            return this;
        }

        public int getTotal() {
            return key.size();
        }

        public UUID getUUID(int position) {
            return key.get(position);
        }

        public String getDataValue(int position) {
            return value.get(position);
        }

        public UUID getServiceUUID() {
            return serviceUUID;
        }
    }

    private boolean writeCharacteristic(BluetoothGattService service, UUID uuid, String data) {
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        characteristic.setValue(data);
        boolean result = bluetoothGatt.writeCharacteristic(characteristic);
        SystemClock.sleep(760);
        return result;
    }

}
