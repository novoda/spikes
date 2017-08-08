package com.novoda.tpbot.bot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

class MovementServiceBinder {

    private final Context context;
    private MovementServiceConnection movementServiceConnection;

    MovementServiceBinder(Context context) {
        this.context = context;
    }

    void bind(DeviceConnection deviceConnection) {
        if (movementServiceConnection == null) {
            movementServiceConnection = new MovementServiceConnection(deviceConnection);
        }
        Intent botServiceIntent = new Intent(context, BoundAndroidMovementService.class);
        context.bindService(botServiceIntent, movementServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class MovementServiceConnection implements ServiceConnection {

        private final DeviceConnection deviceConnection;

        private MovementServiceConnection(DeviceConnection deviceConnection) {
            this.deviceConnection = deviceConnection;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundAndroidMovementService.ServiceBinder binder = (BoundAndroidMovementService.ServiceBinder) service;
            binder.setDeviceConnection(deviceConnection);
            binder.onDependenciesBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Do nothing.
        }
    }

    void unbind() {
        if (BoundAndroidMovementService.isBound()) {
            context.unbindService(movementServiceConnection);
            context.stopService(new Intent(context, BotService.class));
            movementServiceConnection = null;
        }
    }

}
