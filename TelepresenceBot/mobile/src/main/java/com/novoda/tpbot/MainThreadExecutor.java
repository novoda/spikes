package com.novoda.tpbot;

import android.os.Handler;
import android.os.Looper;

public class MainThreadExecutor implements Executor {

    private final Handler handler;

    public static MainThreadExecutor newInstance() {
        Handler handler = new Handler(Looper.getMainLooper());
        return new MainThreadExecutor(handler);
    }

    private MainThreadExecutor(Handler handler) {
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
