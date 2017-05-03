package com.novoda.pianohero.hax;

/**
 * // This class handles communication with the GPIO (General Purpose Input/Output)
 * // pins on a Raspberry Pi. This is this pin layout on a RPi v2:
 * //
 * //      01: 3.3V Power              02: 5V Power
 * //      03: GPIO 2 (SDA)            04: 5V Power
 * //      05: GPIO 3 (SCL)            06: Ground
 * //      07: GPIO 4 (GPCLK0)         08: GPIO 14 (TXD)
 * //      09: Ground                  10: GPIO 15 (RXD)
 * //      11: GPIO 17                 12: GPIO 18 (PCM_CLK)
 * //      13: GPIO 27                 14: Ground
 * //      15: GPIO 22                 16: GPIO 23
 * //      17: 3.3V Power              18: GPIO 24
 * //      19: GPIO 10 (MOSI)          20: Ground
 * //      21: GPIO 9 (MISO)           22: GPIO 25
 * //      23: GPIO 11 (SCLK)          24: GPIO 8 (CE0)
 * //      25: Ground                  26: GPIO 7 (CE1)
 * //
 * // NOTE: For this project (controlling RGB LED Matrix Panels), only Output is used.
 * //       Will need to develop the Input (read) functions when required.
 * <p>
 * // This code is based on an example found at:
 * //   http://elinux.org/Rpi_Datasheet_751_GPIO_Registers
 */
class GpioProxy {

    // Bits with GPIO Pins
    private static final int GPIO_BITS =
        ((1 << 2) | (1 << 3) | (1 << 4) | (1 << 7) | (1 << 8) | (1 << 9) |
            (1 << 10) | (1 << 11) | (1 << 14) | (1 << 15) | (1 << 17) | (1 << 18) |
            (1 << 22) | (1 << 23) | (1 << 24) | (1 << 25) | (1 << 27));

    private int outputBits;
    private int gpio;

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

    /* Hardware registers for peripherals start at this address */
    private static final int BCM2708_PERI_BASE = 0x20000000;

    /* Offset for the GPIO controller */
    private static final int GPIO_BASE = BCM2708_PERI_BASE + 0x200000;
    // Page and Block size are both 4kb
//#define PAGE_SIZE (4*1024)
    private static final int BLOCK_SIZE = 4 * 1024;

    public GpioProxy() {
        this.outputBits = 0;
        this.gpio = GPIO_BASE;
    }

    int setupOutputBits(int outputs) {
        // Make sure only available GPIO bits are used for output.
        outputs &= GPIO_BITS;

        outputBits = outputs;

        for (int b = 0; b < 27; ++b) {
            if ((outputs & (1 << b)) != 0) {
                INP_GPIO(b);  // Must use INP_GPIO before using OUT_GPIO.
                OUT_GPIO(b);
            }
        }

        return outputBits;
    }

    /**
     * GPIO setup macros.
     * Always use INP_GPIO(x) before using OUT_GPIO(x)
     */
    private void INP_GPIO(int g) {
//     *(gpio + ((g) / 10)) &= ~(7 << (((g) % 10) * 3));
        // TODO: 02/05/2017
    }

    /**
     * GPIO setup macros.
     * Always use INP_GPIO(x) before using OUT_GPIO(x)
     */
    private void OUT_GPIO(int g) {
//        *(gpio + ((g) / 10)) |= (1 << (((g) % 10) * 3));
        // TODO: 02/05/2017
    }
}
