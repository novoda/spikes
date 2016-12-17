package com.novoda.tpbot.bot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.novoda.notils.logger.simple.Log;

public class MovementService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder(this);
    }

    public void sendCommand(String command) {
        Log.d("xxx", command);
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
