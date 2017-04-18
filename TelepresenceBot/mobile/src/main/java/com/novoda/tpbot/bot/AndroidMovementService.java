package com.novoda.tpbot.bot;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.novoda.notils.logger.simple.Log;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.support.Optional;

public class AndroidMovementService extends Service implements MovementService {

    static final String ACTION_USB_PERMISSION = "com.novoda.tpbot.USB_PERMISSION";

    private Toaster toaster;
    private UsbManager usbManager;
    private SupportedDeviceRetriever supportedDeviceRetriever;
    private SerialPortMonitor serialPortMonitor;

    private Optional<UsbDevice> supportedUsbDevice = Optional.absent();

    @Override
    public void onCreate() {
        super.onCreate();
        toaster = Toaster.newInstance(AndroidMovementService.this);
        toaster.popToast(AndroidMovementService.class.getSimpleName() + " started");

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        supportedDeviceRetriever = new SupportedDeviceRetriever(usbManager);
        SerialPortCreator serialPortCreator = new SerialPortCreator();
        serialPortMonitor = new SerialPortMonitor(usbManager, dataReceiver, serialPortCreator);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createBroadcastReceiver();
        startConnection();
        return START_STICKY;
    }

    private void createBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MovementServiceIntentHandler.IntentHandler intentHandler = MovementServiceIntentHandler.get(intent.getAction());
            intentHandler.handle(intent, AndroidMovementService.this);
        }
    };

    private void startConnection() {
        if (supportedUsbDevice.isPresent()) {
            return;
        }

        supportedUsbDevice = supportedDeviceRetriever.retrieveFirstSupportedUsbDevice();

        if (supportedUsbDevice.isPresent()) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(supportedUsbDevice.get(), pendingIntent);
        } else {
            Log.d(getClass().getSimpleName(), "Could not find a supported Usb device.");
        }
    }

    private final SerialPortMonitor.DataReceiver dataReceiver = new SerialPortMonitor.DataReceiver() {
        @Override
        public void onReceive(String data) {
            // TODO: forward feedback to the UI.
        }
    };

    @Override
    public void onPermissionGranted() {
        serialPortMonitor.tryToMonitorSerialPortFor(supportedUsbDevice.get());
    }

    @Override
    public void onPermissionDenied() {
        Log.d(getClass().getSimpleName(), "Permission not granted");
        toaster.popToast("Permission not granted");
    }

    @Override
    public void onDeviceAttached() {
        toaster.popToast("Device connected");
        startConnection();
    }

    @Override
    public void onDeviceDetached() {
        Log.d(getClass().getSimpleName(), "USB device detached");
        toaster.popToast("USB device detached");
        closeConnection();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder(this);
    }

    public void sendCommand(String command) {
        serialPortMonitor.tryToSendCommand(command);
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
        serialPortMonitor.stopMonitoring();
    }

    static class Binder extends android.os.Binder {
        private final AndroidMovementService service;

        Binder(AndroidMovementService service) {
            this.service = service;
        }

        AndroidMovementService getService() {
            return service;
        }
    }

}
