package com.novoda.pianohero;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class TouchButton {

    private Listener listener;
    private Gpio buttonBus;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void open() {
        try {
            buttonBus = new PeripheralManagerService().openGpio("GPIO_174");
            buttonBus.setDirection(Gpio.DIRECTION_IN);
            buttonBus.setActiveType(Gpio.ACTIVE_HIGH);
            buttonBus.setEdgeTriggerType(Gpio.EDGE_RISING);
            buttonBus.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    Log.d("!!", "TEST");
                    if (listener == null) {
                        return true;
                    }
                    listener.onTouch();
                    return true;
                }

                @Override
                public void onGpioError(Gpio gpio, int error) {
                    // do nothing
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void close() {
        try {
            buttonBus.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    interface Listener {
        void onTouch();
    }
}
