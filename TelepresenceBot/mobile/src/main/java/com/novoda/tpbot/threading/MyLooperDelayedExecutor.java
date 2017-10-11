package com.novoda.tpbot.threading;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

public class MyLooperDelayedExecutor implements Executor.DelayedExecutor {

    private final Handler handler;
    private final Map<Action, Runnable> runnables;

    public static MyLooperDelayedExecutor newInstance() {
        Handler handler = new Handler();
        return new MyLooperDelayedExecutor(handler, new HashMap<Action, Runnable>());
    }

    private MyLooperDelayedExecutor(Handler handler, Map<Action, Runnable> runnables) {
        this.handler = handler;
        this.runnables = runnables;
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

    @Override
    public void executeAfterDelay(final Action action, long delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                action.perform();
            }
        };
        handler.postDelayed(runnable, delay);
        runnables.put(action, runnable);
    }

    @Override
    public void destroyDelayedAction(Action action) {
        Runnable runnable = runnables.get(action);
        handler.removeCallbacks(runnable);
        runnables.remove(action);
    }
}
