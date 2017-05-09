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

    private RGBmatrixPanel.GpioPins outputBits;

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

    /* Hardware registers for peripherals start at this address */
    private static final int BCM2708_PERI_BASE = 0x20000000;

    /* Offset for the GPIO controller */
    private static final int GPIO_BASE = BCM2708_PERI_BASE + 0x200000;
    // Page and Block size are both 4kb
//#define PAGE_SIZE (4*1024)
    private static final int BLOCK_SIZE = 4 * 1024;

    public GpioProxy() {
        this.outputBits = new RGBmatrixPanel.GpioPins();
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
            setGpioPin(busOutputEnabled);
            setGpioPin(busSerialClock);
            setGpioPin(busDataLatch);
            setGpioPin(busRowAddressA);
            setGpioPin(busRowAddressB);
            setGpioPin(busRowAddressC);
            setGpioPin(busRowAddressD);
            setGpioPin(busLedR1);
            setGpioPin(busLedB1);
            setGpioPin(busLedG1);
            setGpioPin(busLedR2);
            setGpioPin(busLedG2);
            setGpioPin(busLedB2);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setGpioPin(Gpio pin) throws IOException {
        pin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        pin.setActiveType(Gpio.ACTIVE_HIGH);
    }

    // Sets bits which are 1. Ignores bits which are 0.
    //  Converted from Macro: #define GPIO_SET *(gpio+7)
    void setBits(int value) {
        // *(gpio + 7) = value;
        // TODO
    }

    // Clears bits which are 1. Ignores bits which are 0.
    //  Converted from Macro: #define GPIO_CLR *(gpio+10)
    void clearBits(int value) {
//    *(gpio + 10) = value;
//         TODO
    }

    RGBmatrixPanel.GpioPins writeOutputBits(RGBmatrixPanel.GpioPins outputs) {
        try {
            outputBits = outputs;

//            for (int b = 0; b < 27; ++b) {
                busOutputEnabled.setValue(outputs.bits.outputEnabled);
                busSerialClock.setValue(outputs.bits.clock);
                busDataLatch.setValue(outputs.bits.latch);

                int rowAddress = outputs.bits.rowAddress;
                // rowAddress is always 4 bits
                if ((rowAddress & 8) == 8) {
                    busRowAddressD.setValue(true);
                } else {
                    busRowAddressD.setValue(false);
                }
                if ((rowAddress & 4) == 4) {
                    busRowAddressC.setValue(true);
                } else {
                    busRowAddressC.setValue(false);
                }
                if ((rowAddress & 2) == 2) {
                    busRowAddressB.setValue(true);
                } else {
                    busRowAddressB.setValue(false);
                }
                if ((rowAddress & 1) == 1) {
                    busRowAddressA.setValue(true);
                } else {
                    busRowAddressA.setValue(false);
                }

                busLedR1.setValue(outputs.bits.r1);
                busLedG1.setValue(outputs.bits.g1);
                busLedB1.setValue(outputs.bits.b1);

                busLedR2.setValue(outputs.bits.r2);
                busLedG2.setValue(outputs.bits.g2);
                busLedB2.setValue(outputs.bits.b2);
//            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return outputBits;
    }
}
