package com.novoda.pianohero;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class GpioButtonClickable implements AndroidThing, Clickable {

    private Listener listener;
    private Gpio buttonBus;

    @Override
    public void open() {
        try {
            buttonBus = new PeripheralManagerService().openGpio("BCM21");
            buttonBus.setDirection(Gpio.DIRECTION_IN);
            buttonBus.setActiveType(Gpio.ACTIVE_HIGH);
            buttonBus.setEdgeTriggerType(Gpio.EDGE_RISING);
            buttonBus.registerGpioCallback(gpioCallback);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private final GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            if (listener != null) {
                listener.onClick();
                return true;
            } else {
                return true; // TODO: should this return false?
            }
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            // do nothing - TODO do we need to implement this? what's the default impl on AndroidThings?
        }
    };

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void close() {
        try {
            buttonBus.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
