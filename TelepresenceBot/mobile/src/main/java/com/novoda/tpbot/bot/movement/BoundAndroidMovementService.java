package com.novoda.tpbot.bot.movement;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.novoda.tpbot.bot.device.DeviceConnection;

public class BoundAndroidMovementService extends Service {

    private static boolean isBound;

    private final IBinder binder = new ServiceBinder();

    @Nullable
    private DeviceConnection deviceConnection;

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return binder;
    }

    private void start() {
        if (deviceConnection == null) {
            throw new IllegalStateException(DeviceConnection.class.getSimpleName() + " must be bound before calling onDependenciesBound()");
        }

        deviceConnection.connect();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;

        if (deviceConnection != null) {
            deviceConnection.disconnect();
            deviceConnection = null;
        }

        return super.onUnbind(intent);
    }

    class ServiceBinder extends Binder {

        void setDeviceConnection(DeviceConnection deviceConnection) {
            BoundAndroidMovementService.this.deviceConnection = deviceConnection;
        }

        void onDependenciesBound() {
            BoundAndroidMovementService.this.start();
        }

    }
}
