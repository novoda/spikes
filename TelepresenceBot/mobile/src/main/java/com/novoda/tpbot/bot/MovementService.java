package com.novoda.tpbot.bot;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.novoda.notils.logger.simple.Log;
import com.novoda.notils.logger.toast.Toaster;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementService extends Service {

    private static final String ACTION_USB_PERMISSION = "com.novoda.tpbot.USB_PERMISSION";
    private static final List<Integer> SUPPORTED_VENDOR_IDS = Arrays.asList(9025, 10755, 4292); // TODO: read from devices_filter.xml

    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbManager usbManager;
    private UsbSerialDevice serialPort;
    private boolean isSerialStarted;
    private PendingIntent pendingIntent;

    private Toaster toaster;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toaster = Toaster.newInstance(MovementService.this);
        toaster.popToast(MovementService.class.getSimpleName() + " started");

        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        startConnection();

        return START_STICKY;
    }

    public void startConnection() {
        if (isSerialStarted) {
            return;
        }

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (usbDevices.isEmpty()) {
            return;
        }

        for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
            device = entry.getValue();
            int deviceVID = device.getVendorId();

            if (isSupportedDeviceID(deviceVID)) {
                usbManager.requestPermission(device, pendingIntent);
                break;
            } else {
                connection = null;
                device = null;
            }
        }
    }

    private boolean isSupportedDeviceID(int deviceVID) {
        return SUPPORTED_VENDOR_IDS.contains(deviceVID);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            toaster.popToast("Opening serial connection");
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            isSerialStarted = true;
                            toaster.popToast("Serial connection open");
                        } else {
                            Log.d("SERIAL", "Port is not open");
                            toaster.popToast("Port is not open");
                        }
                    } else {
                        Log.d("SERIAL", "Port is null");
                        toaster.popToast("Port is null");
                    }
                } else {
                    Log.d("SERIAL", "Permission not granted");
                    toaster.popToast("Permission not granted");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                toaster.popToast("Device connected");
                startConnection();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                Log.d("SERIAL", "USB device detached");
                toaster.popToast("USB device detached");
                closeConnection();
            }
        }
    };

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                Log.d("SERIAL", "Received from USB serial " + data);
                // TODO forward to the UI
            } catch (UnsupportedEncodingException e) {
                Log.e(e, "Error receiving data from USB serial");
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder(this);
    }

    public void sendCommand(String command) {
        if (serialPort != null) {
            serialPort.write(command.getBytes());
        } else {
            Log.d("SERIAL", "Serial not connected for command " + command);
            toaster.popToast("Serial not connected for command " + command);
            // TODO forward to the human part
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.e("Trying to unregister a BroadcastReceiver which wasn't registered yet");
        }
        closeConnection();
        super.onDestroy();
    }

    private void closeConnection() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
        isSerialStarted = false;
    }

    public static class Binder extends android.os.Binder {
        private final MovementService service;

        Binder(MovementService service) {
            this.service = service;
        }

        public MovementService getService() {
            return service;
        }
    }

}
