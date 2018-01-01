package com.novoda.inkyphat;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

class VersionChecker {

    private final PeripheralManagerService service;

    VersionChecker(PeripheralManagerService service) {
        this.service = service;
    }

    Version checkVersion(String gpioBusyPin) throws IOException {
        try (Gpio chipBusyPin = service.openGpio(gpioBusyPin)) {
            chipBusyPin.setDirection(Gpio.DIRECTION_IN);

            boolean initialValue = chipBusyPin.getValue();

            if (initialValue) {
                Log.d("TUT", "version 2 board");
                return Version.TWO;
            } else {
                Log.d("TUT", "version 1 board");
                return Version.ONE;
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
