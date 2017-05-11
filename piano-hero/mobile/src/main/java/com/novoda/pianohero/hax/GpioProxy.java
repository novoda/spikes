package com.novoda.pianohero.hax;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * // This class handles communication with the GPIO (General Purpose Input/Output)
 * // pins on a Raspberry Pi. This is this pin layout on a RPi v2:
 * //
 * // NOTE: For this project (controlling RGB LED Matrix Panels), only Output is used.
 * //       Will need to develop the Input (read) functions when required.
 * <p>
 * // This code is based on an example found at:
 * //   http://elinux.org/Rpi_Datasheet_751_GPIO_Registers
 */
public class GpioProxy {

    private Gpio busOutputEnabled;
    private Gpio busSerialClock;
    private Gpio busDataLatch;
    private Gpio busRowAddressA;
    private Gpio busRowAddressB;
    private Gpio busRowAddressC;
    private Gpio busRowAddressD;
    private Gpio busLedR1;
    private Gpio busLedB1;
    private Gpio busLedG1;
    private Gpio busLedR2;
    private Gpio busLedG2;
    private Gpio busLedB2;

    public GpioProxy() {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            busOutputEnabled = service.openGpio("BCM4");
            busSerialClock = service.openGpio("BCM19");
            busDataLatch = service.openGpio("BCM26");
            busRowAddressA = service.openGpio("BCM23");
            busRowAddressB = service.openGpio("BCM24");
            busRowAddressC = service.openGpio("BCM5");
            busRowAddressD = service.openGpio("BCM6");
            busLedR1 = service.openGpio("BCM21");
            busLedB1 = service.openGpio("BCM20");
            busLedG1 = service.openGpio("BCM16");
            busLedR2 = service.openGpio("BCM22");
            busLedG2 = service.openGpio("BCM27");
            busLedB2 = service.openGpio("BCM17");
            setGpioPinLow(busOutputEnabled);
            setGpioPinHigh(busSerialClock);
            setGpioPinHigh(busDataLatch);
            setGpioPinHigh(busRowAddressA);
            setGpioPinHigh(busRowAddressB);
            setGpioPinHigh(busRowAddressC);
            setGpioPinHigh(busRowAddressD);
            setGpioPinHigh(busLedR1);
            setGpioPinHigh(busLedB1);
            setGpioPinHigh(busLedG1);
            setGpioPinHigh(busLedR2);
            setGpioPinHigh(busLedG2);
            setGpioPinHigh(busLedB2);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setGpioPinLow(Gpio pin) throws IOException {
        pin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        pin.setActiveType(Gpio.ACTIVE_LOW);
    }

    private static void setGpioPinHigh(Gpio pin) throws IOException {
        pin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        pin.setActiveType(Gpio.ACTIVE_HIGH);
    }

    // Sets pins which are 1. Ignores pins which are 0.
    //  Converted from Macro: #define GPIO_SET *(gpio+7)
    void setBits(int value) {
        // *(gpio + 7) = value;
        // TODO
    }

    // Clears pins which are 1. Ignores pins which are 0.
    //  Converted from Macro: #define GPIO_CLR *(gpio+10)
    void clearBits(int value) {
//    *(gpio + 10) = value;
//         TODO
    }

    void writeRowAddress(int rowAddress) {
        try {
            // rowAddress is always 4 pins
            busRowAddressD.setValue((rowAddress & 8) == 8);
            busRowAddressC.setValue((rowAddress & 4) == 4);
            busRowAddressB.setValue((rowAddress & 2) == 2);
            busRowAddressA.setValue((rowAddress & 1) == 1);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    void writePixel(RGBmatrixPanel.PixelPins pins) {
        try {
            busLedR1.setValue(pins.r1);
            busLedG1.setValue(pins.g1);
            busLedB1.setValue(pins.b1);

            busLedR2.setValue(pins.r2);
            busLedG2.setValue(pins.g2);
            busLedB2.setValue(pins.b2);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    void writeClock(boolean value) {
        try {
            busSerialClock.setValue(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    void writeOutputEnabled(boolean value) {
        try {
            busOutputEnabled.setValue(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    void writeLatch(boolean value) {
        try {
            busDataLatch.setValue(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
