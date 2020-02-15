package com.novoda.tpbot.bot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class BoundAndroidBotService extends Service {

    private static boolean isBound;

    private final IBinder binder = new BotServiceBinder();

    @Nullable
    private BotPresenter botPresenter;

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return binder;
    }

    private void start() {
        if (botPresenter == null) {
            throw new IllegalStateException(BotPresenter.class.getSimpleName() + " must be bound before calling onDependenciesBound()");
        }

        botPresenter.startPresenting();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;

        if (botPresenter != null) {
            botPresenter.stopPresenting();
            botPresenter = null;
        }

        return super.onUnbind(intent);
    }

    class BotServiceBinder extends Binder {

        void setBotPresenter(BotPresenter botPresenter) {
            BoundAndroidBotService.this.botPresenter = botPresenter;
        }

        void onDependenciesBound() {
            BoundAndroidBotService.this.start();
        }

    }
}
