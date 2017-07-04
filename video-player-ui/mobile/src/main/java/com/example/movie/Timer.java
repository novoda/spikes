package com.example.movie;

import android.os.Handler;

class Timer {

    private final Handler handler;

    private Callback callback;

    Timer(Handler handler) {
        this.handler = handler;
    }

    public void schedule(Callback callback, long millisDelay) {
        this.callback = callback;
        handler.postDelayed(ping, millisDelay);
    }

    private final Runnable ping = new Runnable() {
        @Override
        public void run() {
            callback.onCountdownComplete();
        }
    };

    public void stop() {
        handler.removeCallbacks(ping);
        this.callback = null;
    }

    public interface Callback {

        void onCountdownComplete();

    }

}
