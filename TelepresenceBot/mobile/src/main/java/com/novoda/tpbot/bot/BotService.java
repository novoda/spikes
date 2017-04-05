package com.novoda.tpbot.bot;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.novoda.support.Observable;
import com.novoda.support.Observer;
import com.novoda.tpbot.Result;

import static com.novoda.support.Observable.unsubscribe;

public class BotService extends IntentService {

    private static final String EXTRA_SERVER_ADDRESS = "EXTRA_SERVER_ADDRESS";

    private BotTelepresenceService botTelepresenceService;
    private Observable<Result> connectionObservable;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BotService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        botTelepresenceService = SocketIOTelepresenceService.getInstance();
    }

    @Override
    public void onDestroy() {
        unsubscribe(connectionObservable);
        botTelepresenceService.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_SERVER_ADDRESS)) {
            String serverAddress = intent.getStringExtra(EXTRA_SERVER_ADDRESS);

            connectionObservable = botTelepresenceService.connectTo(serverAddress)
                    .attach(new ConnectionObserver())
                    .start();
        }
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            Log.e(getClass().getSimpleName(), "Pass to movement service: " + result);
        }

    }

}
