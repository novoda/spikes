package com.novoda.dungeoncrawler;

import android.util.Log;

import com.yheriatovych.reductor.Dispatcher;
import com.yheriatovych.reductor.Middleware;
import com.yheriatovych.reductor.Store;

public class MiddlewareLogger implements Middleware<Redux.GameState> {

    private long timeOfLastAction = 0;

    @Override
    public Dispatcher create(Store<Redux.GameState> store, Dispatcher nextDispatcher) {
        return action -> {
            long diff = System.currentTimeMillis() - timeOfLastAction;
            Log.d("TUT", "action " + store.getState().stage + "[" + diff + "]");
            nextDispatcher.dispatch(action);
            timeOfLastAction = System.currentTimeMillis();
        };
    }
}
