package com.novoda.tpbot.bot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.novoda.notils.logger.toast.Toaster;

public class MovementService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toaster.newInstance(this).popToast(MovementService.class.getSimpleName() + " started");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder(this);
    }

    public void sendCommand(String command) {
        Toaster.newInstance(this).popToast("command " + command);
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