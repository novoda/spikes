package com.novoda.tpbot.bot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

class BotServiceCreator {

    private final Context context;
    private final BotView botView;
    private final String serverAddress;

    BotServiceCreator(Context context, BotView botView, String serverAddress) {
        this.context = context;
        this.botView = botView;
        this.serverAddress = serverAddress;
    }

    void create() {
        Intent botServiceIntent = new Intent(context, BotService.class);
        context.bindService(botServiceIntent, botServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection botServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BotService.BotServiceBinder binder = (BotService.BotServiceBinder) iBinder;
            BotPresenter botPresenter = new BotPresenter(SocketIOTelepresenceService.getInstance(), botView, serverAddress);
            binder.setBotPresenter(botPresenter);
            binder.onDependenciesBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    void destroy() {
        context.unbindService(botServiceConnection);
        context.stopService(new Intent(context, BotService.class));
    }

}
