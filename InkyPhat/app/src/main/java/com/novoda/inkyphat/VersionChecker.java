package com.novoda.inkyphat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

class VersionChecker {

    private final PeripheralManager service;

    VersionChecker(PeripheralManager service) {
        this.service = service;
    }

    Version checkVersion(String gpioBusyPin, String gpioResetPin) throws IOException {
        try (Gpio busyPin = service.openGpio(gpioBusyPin);
             Gpio resetPin = service.openGpio(gpioResetPin)) {
            busyPin.setDirection(Gpio.DIRECTION_IN);
            resetPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            resetPin.setValue(true);

            if (busyPin.getValue()) {
                return Version.ONE;
            } else {
                return Version.TWO;
            }
        }
    }

    enum Version {
        /**
         * Original hardware
         **/
        ONE,
        /**
         * Hardware released December 2017
         **/
        TWO
    }

}
