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
    private MovementServiceManager movementServiceManager;

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return binder;
    }

    private void start() {
        if (movementServiceManager == null) {
            throw new IllegalStateException(BotPresenter.class.getSimpleName() + " must be bound before calling onDependenciesBound()");
        }

        movementServiceManager.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;

        if (movementServiceManager != null) {
            movementServiceManager.stop();
            movementServiceManager = null;
        }

        return super.onUnbind(intent);
    }

    class ServiceBinder extends Binder {

        void setBotPresenter(MovementServiceManager movementServiceManager) {
            BoundAndroidMovementService.this.movementServiceManager = movementServiceManager;
        }

        void onDependenciesBound() {
            BoundAndroidMovementService.this.start();
        }

    }
}
