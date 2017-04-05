package com.novoda.tpbot.bot;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.novoda.support.Observer;
import com.novoda.tpbot.Result;

public class BotService extends Service {

    private final IBinder binder = new BotServiceBinder();

    private BotTelepresenceService botTelepresenceService;
    private BotView botView;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void connectTo(String serverAddress) {
        botTelepresenceService.connectTo(serverAddress)
                .attach(new ConnectionObserver())
                .start();
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            if (result.isError()) {
                botView.onError(result.exception().get().getMessage());
            } else {
                botView.onConnect(result.message().get());
            }
        }

    }

    @Override
    public void onDestroy() {
        botTelepresenceService.disconnect();
        Log.e(getClass().getSimpleName(), "onDestroy()");
        super.onDestroy();
    }

    class BotServiceBinder extends Binder {

        void setBotTelepresenceService(BotTelepresenceService botTelepresenceService) {
            BotService.this.botTelepresenceService = botTelepresenceService;
        }

        void setBotView(BotView botView) {
            BotService.this.botView = botView;
        }

        void startConnection(String serverAddress) {
            BotService.this.connectTo(serverAddress);
        }

    }
}
