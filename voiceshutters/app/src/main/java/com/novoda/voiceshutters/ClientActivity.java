package com.novoda.voiceshutters;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ClientActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 123;
    private static final int REQUEST_FINE_LOCATION = 1234;

    private VoiceShuttersClientWrangler clientWrangler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        Handler handler = new Handler();

        clientWrangler = new VoiceShuttersClientWrangler(new ClientWrangler(adapter, scanner, handler));
        onStartScan(null);
    }

    public void onStartScan(View v) {
        if (hasPermissions()) {
            Toast.makeText(this, "starting", Toast.LENGTH_SHORT).show();
            startScan();
        } else {
            Toast.makeText(this, "missing permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermissions() {
        if (!((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
            Log.e("TUT", "no bluetooth.");
            requestEnableBluetooth();
            return false;
        } else if (!hasLocationPermissions()) {
            Log.e("TUT", "no permissions.");
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private void requestEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        Log.d("TUT", "Requested user enables Bluetooth. Try starting the scan again.");
    }

    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    private void startScan() {
        new Thread(() -> {
            ClientActivity context = ClientActivity.this;
            clientWrangler.scanForDevices(
                    devices -> {
                        if (devices.isEmpty()) {
                            Log.e("TUT", "no devices found");
                            return;
                        }

                        clientWrangler.sendData(
                                context,
                                devices.get(0),
                                "TestWifiSSID",
                                "Password 123",
                                FirebaseAuth.getInstance().getUid()
                        );
                    }
            );
        }).start();
    }

    public void onStopScan(View v) {
        Log.d("TUT", "stopping");
        clientWrangler.stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e("TUT", "No BLE support.");
            finish();
        }
    }
}
