package com.novoda.simonsaysandroidthings.hw.io;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class GpioButton implements Button {

    private final Gpio gpio;
    private Listener listener;

    public static Button create(String pinName, PeripheralManagerService service) {
        try {
            Gpio gpio = service.openGpio(pinName);
            gpio.setDirection(Gpio.DIRECTION_IN);
            gpio.setActiveType(Gpio.ACTIVE_LOW);
            gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            return new GpioButton(gpio);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create GpioButton with pin " + pinName, e);
        }
    }

    GpioButton(Gpio gpio) {
        this.gpio = gpio;

        try {
            this.gpio.registerGpioCallback(gpioCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            if (listener == null) {
                return false;
            }
            try {
                if (gpio.getValue()) {
                    listener.onButtonPressed(GpioButton.this);
                } else {
                    listener.onButtonReleased(GpioButton.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            super.onGpioError(gpio, error);
        }
    };

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void close() {
        gpio.unregisterGpioCallback(gpioCallback);
        try {
            gpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
