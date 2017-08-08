package com.novoda.tpbot.bot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.novoda.tpbot.controls.LastServerPersistence;
import com.novoda.tpbot.controls.LastServerPreferences;

class BotServiceBinder {

    private final Context context;

    private BotServiceConnection botServiceConnection;

    BotServiceBinder(Context context) {
        this.context = context;
    }

    void bind(BotView botView, String serverAddress) {
        if (botServiceConnection == null) {
            botServiceConnection = new BotServiceConnection(botView, serverAddress);
        }
        Intent botServiceIntent = new Intent(context, BotService.class);
        context.bindService(botServiceIntent, botServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class BotServiceConnection implements ServiceConnection {

        private final BotView botView;
        private final String serverAddress;

        private BotServiceConnection(BotView botView, String serverAddress) {
            this.botView = botView;
            this.serverAddress = serverAddress;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LastServerPersistence lastServerPersistence = LastServerPreferences.newInstance(context);
            BotPresenter botPresenter = new BotPresenter(
                    SocketIOTelepresenceService.getInstance(),
                    botView,
                    lastServerPersistence,
                    serverAddress
            );
            BotService.BotServiceBinder binder = (BotService.BotServiceBinder) service;
            binder.setBotPresenter(botPresenter);
            binder.onDependenciesBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Do nothing.
        }
    }

    void unbind() {
        if (BotService.isBound()) {
            context.unbindService(botServiceConnection);
            context.stopService(new Intent(context, BotService.class));
            botServiceConnection = null;
        }
    }

}
