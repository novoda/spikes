package com.novoda.dungeoncrawler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ArduinoLoop {

    private final HandlerThread thread = new HandlerThread("Arudino Loop Mimic");

    private Handler handler;

    /**
     * After creating a setup() function, which initializes and sets the initial values, the loop() function does precisely what its name suggests, and loops consecutively, allowing your program to change and respond. Use it to actively control the Arduino board.
     * https://www.arduino.cc/en/Reference/Loop/
     *
     * @param loopable the method to continuously lopp
     */
    void start(Loopable loopable) {
        thread.start();
        final Looper looper = thread.getLooper();
        handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                loopable.loop();
                handler.post(this);
            }
        });
    }

    void stop() {
        if (handler == null) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        thread.quit();
    }

    interface Loopable {
        void loop();
    }
}
