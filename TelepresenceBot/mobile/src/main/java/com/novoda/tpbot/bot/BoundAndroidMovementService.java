package com.novoda.tpbot.bot;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
            throw new IllegalStateException(BotPresenter.class.getSimpleName() + " must be bound before calling onDependenciesBound()");
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
