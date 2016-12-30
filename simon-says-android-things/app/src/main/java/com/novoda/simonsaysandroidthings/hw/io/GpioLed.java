package com.novoda.simonsaysandroidthings.hw.io;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class GpioLed implements Led {

    private final Gpio gpio;

    public static GpioLed create(String pinName, PeripheralManagerService service) {
        try {
            Gpio gpio = service.openGpio(pinName);
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            return new GpioLed(gpio);

        } catch (IOException e) {
            throw new RuntimeException("Couldn't create GpioLed with pin " + pinName, e);
        }
    }

    GpioLed(Gpio gpio) {
        this.gpio = gpio;
    }

    @Override
    public void turnOn() {
        try {
            gpio.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void turnOff() {
        try {
            gpio.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            gpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
