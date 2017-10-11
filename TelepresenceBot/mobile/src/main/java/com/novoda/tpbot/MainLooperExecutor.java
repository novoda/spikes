package com.novoda.tpbot;

import android.os.Handler;
import android.os.Looper;

public class MainLooperExecutor implements Executor {

    private final Handler handler;

    public static MainLooperExecutor newInstance() {
        Handler handler = new Handler(Looper.getMainLooper());
        return new MainLooperExecutor(handler);
    }

    private MainLooperExecutor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(final Action action) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                action.perform();
            }
        });
    }
}
