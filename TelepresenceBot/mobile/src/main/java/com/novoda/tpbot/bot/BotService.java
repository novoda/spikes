package com.novoda.tpbot.bot;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BotService extends Service {

    private final IBinder binder = new BotServiceBinder();

    private BotPresenter botPresenter;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void start() {
        botPresenter.startPresenting();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        botPresenter.stopPresenting();
        return super.onUnbind(intent);
    }

    class BotServiceBinder extends Binder {

        void setBotPresenter(BotPresenter botPresenter) {
            BotService.this.botPresenter = botPresenter;
        }

        void onDependenciesBound() {
            BotService.this.start();
        }

    }
}
