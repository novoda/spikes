package com.novoda.pianohero.hax;

import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * This class is for controlling a 32x32 RGB LED Matrix panel using
 * the Raspberry Pi GPIO.
 * <p>
 * This code is based on a cool example found at:
 * https://github.com/mattdh666/rpi-led-matrix-panel/
 * <p>
 * https://cdn-learn.adafruit.com/downloads/pdf/32x16-32x32-rgb-led-matrix.pdf
 */
public class RGBmatrixPanel {

    private final ShapeDrawer shapeDrawer;

    public static class Color {
        public int red;
        public int green;
        public int blue;
    }

//#include "RgbMatrix.h"

    // WIDTH and HEIGHT of the RBG Matrix.
    // If chaining multiple boards together, this is the overall WIDTH x HEIGHT.
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    // The 32x32 RGB Matrix is broken into two 16x32 sub-panels.
    private static final int ROWS_PER_SUB_PANEL = 16;
    private static final int COLS_PER_SUB_PANEL = 32;
    // Number of Daisy-Chained Boards
    private static final int CHAINED_BOARDS_COUNT = 1;
    // Number of Columns
    private static final int COLUMN_COUNT = CHAINED_BOARDS_COUNT * COLS_PER_SUB_PANEL;
    // Pulse WIDTH Modulation (PWM) Resolution
    private static final int PWM_BITS = 7; //max is 7

    private final GpioProxy gpioProxy;

    // The following data structure represents the pins on the Raspberry Pi GPIO.
    // Each RGB LED Panel requires writing to 2 LED's at a time, so the data
    // structure represents 2 pixels on an RGB LED matrix. The data structure maps
    // the GPIO pins to the LED matrix controls.
    //
    // The GPIO pins are mapped to the LED Matrix as follows:
    //
    //   GPIO 4             -->  OE (Output Enabled)
    //   GPIO 19            -->  CLK (Serial Clock)
    //   GPIO 26            -->  LAT (Data Latch)
    //   GPIO 23            -->  A  --|
    //   GPIO 24            -->  B    |   Row
    //   GPIO 5             -->  C    | Address
    //   GPIO 6             -->  D  --|
    //   GPIO 21            -->  R1 (LED 1: Red)
    //   GPIO 20            -->  G1 (LED 1: Green)
    //   GPIO 16            -->  B1 (LED 1: Blue)
    //   GPIO 22            -->  R2 (LED 2: Red)
    //   GPIO 27            -->  G2 (LED 2: Green)
    //   GPIO 17            -->  B2 (LED 2: Blue)

    static class PixelPins {
        boolean r1;
        boolean g1;
        boolean b1;

        boolean r2;
        boolean g2;
        boolean b2;
    }

    // Because a 32x32 Panel is composed of two 16x32 sub-panels, and each
    // 32x32 Panel requires writing an LED from each sub-panel at a time, the
    // following data structure represents two rows: n and n+16.
    static class TwoRows {
        PixelPins[] column = new PixelPins[COLUMN_COUNT];
    }

    static class Display {
        TwoRows[] row = new TwoRows[ROWS_PER_SUB_PANEL];
    }

    private Display[] plane = new Display[PWM_BITS];
    private Display[] fadeInPlane = new Display[PWM_BITS]; //2nd plane for handling fadeIn

    // Members for writing text
    private int textCursorX, textCursorY;
    private Color fontColor;
    private int fontSize;
    private int fontWidth;
    private int fontHeight;
    private boolean wordWrap;

    //#finish include "RgbMatrix.h"
//
//#include "FONT_3_X_5.h"
//#include "FONT_4_X_6.h"
//#include "FONT_5_X_7.h"
//
//#include<assert.h>
//#include<math.h>
//#include<stdint.h>
//#include<stdlib.h>
//#include<stdio.h>
//#include<string.h>
//#include<time.h>
//#include<unistd.h>
//
//#include<algorithm>
//
//#
//    define _USE_MATH_DEFINES
//
//#
//
//    define pgm_read_byte(addr) (*(const unsigned char *)(addr))
//
// Clocking in a row takes about 3.4usec (TODO: per board)
// Because clocking the data in is part of the 'wait time', we need to
// substract that from the row sleep time.
    private static final int ROW_CLOCK_TIME = 3400;
    //
    private static final long[] ROW_SLEEP_NANOS = {
            // Only using the first PWM_BITS elements.
            (1 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (2 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (4 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (8 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (16 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (32 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            (64 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME,
            // Too much flicker with 8 pins. We should have a separate screen pass
            // with this bit plane. Or interlace. Or trick with -OE switch on in the
            // middle of row-clocking, thus have ROW_CLOCK_TIME / 2
            (128 * ROW_CLOCK_TIME) - ROW_CLOCK_TIME, // too much flicker.
    };

    public RGBmatrixPanel(GpioProxy gpioProxy) {
        this.gpioProxy = gpioProxy;

        for (int i = 0; i < plane.length; i++) {
            Display display = new Display();
            for (int j = 0; j < display.row.length; j++) {
                TwoRows twoRows = new TwoRows();
                for (int k = 0; k < twoRows.column.length; k++) {
                    twoRows.column[k] = new PixelPins();
                }
                display.row[j] = twoRows;
            }
            plane[i] = display;
        }

        //Initialize text members
        textCursorX = 0;
        textCursorY = 0;
        Color white = new Color();
        white.red = 255;
        white.green = 255;
        white.blue = 255;
        fontColor = white;
        fontSize = 1;
        fontWidth = 3;
        fontHeight = 5;
        wordWrap = true;

        shapeDrawer = new ShapeDrawer(WIDTH, HEIGHT, PWM_BITS, plane);
    }

    /*
     * Clear the entire display
     */
    public void clearDisplay() {
        // TODO look at cpp again to do this
//        for (Display display : plane) {
//            for (TwoRows rows : display.row) {
//                for (PixelPins pins : rows.column) {
//                    pins.r1 = false;
//                    pins.g1 = false;
//                    pins.b1 = false;
//                    pins.r2 = false;
//                    pins.g2 = false;
//                    pins.b2 = false;
//                    gpioProxy.writePixel(pins); // TODO does this even work?
//                }
//            }
//        }
    }

    public void updateDisplay() {
        PixelPins pixelPins = new PixelPins();

        for (int row = 0; row < ROWS_PER_SUB_PANEL; ++row) {
            // Rows can't be switched very quickly without ghosting, so we do the
            // full PWM of one row before switching rows.
            for (int b = 0; b < PWM_BITS; b++) {
                TwoRows rowData = plane[b].row[row];

                // Clock in the row. The time this takes is the smallest time we can
                // leave the LEDs on, thus the smallest timeconstant we can use for
                // PWM (doubling the sleep time with each bit).
                // So this is the critical path; I'd love to know if we can employ some
                // DMA techniques to speed this up.
                // (With this code, one row roughly takes 3.0-3.4usec to clock in).
                //
                // However, in particular for longer chaining, it seems we need some more
                // wait time to settle.

                long stabilizeWait = TimeUnit.NANOSECONDS.toMillis(156); //TODO: mateo was 256

                for (int col = 0; col < COLUMN_COUNT; ++col) {
                    PixelPins colPins = rowData.column[col];

                    // Clear bits (clock)
                    gpioProxy.writeClock(false);
                    // Clear bits
                    pixelPins.r1 = false;
                    pixelPins.g1 = false;
                    pixelPins.b1 = false;

                    pixelPins.r2 = false;
                    pixelPins.g2 = false;
                    pixelPins.b2 = false;

                    gpioProxy.writePixel(pixelPins);
                    sleep(stabilizeWait);

                    // Set bits
                    pixelPins.r1 = colPins.r1;
                    pixelPins.g1 = colPins.g1;
                    pixelPins.b1 = colPins.b1;

                    pixelPins.r2 = colPins.r2;
                    pixelPins.g2 = colPins.g2;
                    pixelPins.b2 = colPins.b2;

                    gpioProxy.writePixel(pixelPins);
                    sleep(stabilizeWait);

                    // Set bits (clock)
                    // The CLK (clock) signal marks the arrival of each bit of data.
                    gpioProxy.writeClock(true);
                    sleep(stabilizeWait);
                }

                // switch off while strobe (latch).
                // OE (output enable) switches the LEDs off when transitioning from one row to the next.
                gpioProxy.writeOutputEnabled(true);

                // select which two rows of the display are currently lit.
                gpioProxy.writeRowAddress(row);

                // strobe - on and off
                // The LAT (latch) signal marks the end of a row of data.
                gpioProxy.writeLatch(true);
                gpioProxy.writeLatch(false);

                // Now switch on for the given sleep time.
                gpioProxy.writeOutputEnabled(false);

                // If we use less pins, then use the upper areas which leaves us more CPU time to do other stuff.
                sleep(ROW_SLEEP_NANOS[b + (7 - PWM_BITS)]);
                Log.d("!!!", "drawing something?");
            }
        }
    }

    private static void sleep(long nanos) {
        // For sleep times above 20usec, nanosleep seems to be fine, but it has
        // an offset of about 20usec (on the RPi distribution I was testing it on).
        // That means, we need to give it 80us to get 100us.
        // For values lower than roughly 30us, this is not accurate anymore and we need to switch to busy wait.
        // idea: compile Linux kernel realtime extensions and watch if the offset-time changes and hope for less jitter.
        if (nanos > 28000) {
            long total = nanos - 20000;
            SystemClock.sleep(TimeUnit.NANOSECONDS.toMillis(total));
        } else {
            // The following loop is determined empirically on a 700Mhz RPi
            for (int i = (int) (nanos >> 2); i != 0; --i) {
//                asm("");  // Force GCC not to optimize this away.
                Log.v("TUT", "what?");
            }
        }
    }

    // Clear the inside of the given Rectangle.
    public void clearRect(int fx, int fy, int fw, int fh) {
//        uint8_t maxX, maxY;
//        maxX = (fx + fw) > WIDTH ? WIDTH : (fx + fw);
//        maxY = (fy + fh) > HEIGHT ? HEIGHT : (fy + fh);
//
//        for (int b = PWM_BITS - 1; b >= 0; b--) {
//            for (int x = fx; x < maxX; x++) {
//                for (int y = fy; y < maxY; y++) {
//                    PixelPins * pins = &plane[b].row[y & 0xf].column[x];
//
//                    if (y < 16) {
//                        // Upper sub-panel
//                        pins.pins.r1 = 0;
//                        pins.pins.g1 = 0;
//                        pins.pins.b1 = 0;
//                    } else {
//                        // Lower sub-panel
//                        pins.pins.r2 = 0;
//                        pins.pins.g2 = 0;
//                        pins.pins.b2 = 0;
//                    }
//                }
//            }
//        }
    }

    //    // Fade whatever is on the display to black.
    void fadeDisplay() {
//        for (int b = PWM_BITS - 1; b >= 0; b--) {
//            for (int x = 0; x < WIDTH; x++) {
//                for (int y = 0; y < HEIGHT; y++) {
//                    PixelPins * pins = &plane[b].row[y & 0xf].column[x];
//
//                    if (y < 16) {
//                        // Upper sub-panel
//                        pins.pins.r1 >>= 1;
//                        pins.pins.g1 >>= 1;
//                        pins.pins.b1 >>= 1;
//                    } else {
//                        // Lower sub-panel
//                        pins.pins.r2 >>= 1;
//                        pins.pins.g2 >>= 1;
//                        pins.pins.b2 >>= 1;
//                    }
//                }
//            }
//            //TODO: make this dependent on PWM_BITS (longer sleep for fewer PWM_BITS).
//            usleep(100000); // 1/10 second
//        }
    }

    //    // Fade whatever is shown inside the given Rectangle.
    void fadeRect(int fx, int fy, int fw, int fh) {
//        uint8_t maxX, maxY;
//        maxX = (fx + fw) > WIDTH ? WIDTH : (fx + fw);
//        maxY = (fy + fh) > HEIGHT ? HEIGHT : (fy + fh);
//
//        for (int b = PWM_BITS - 1; b >= 0; b--) {
//            for (int x = fx; x < maxX; x++) {
//                for (int y = fy; y < maxY; y++) {
//                    PixelPins * pins = &plane[b].row[y & 0xf].column[x];
//
//                    if (y < 16) {
//                        // Upper sub-panel
//                        pins.pins.r1 >>= 1;
//                        pins.pins.g1 >>= 1;
//                        pins.pins.b1 >>= 1;
//                    } else {
//                        // Lower sub-panel
//                        pins.pins.r2 >>= 1;
//                        pins.pins.g2 >>= 1;
//                        pins.pins.b2 >>= 1;
//                    }
//                }
//            }
//            //TODO: make this param and/or dependent on PWM_BITS (longer sleep for fewer PWM_BITS).
//            usleep(100000); // 1/10 second
//        }
    }

    //    // Call this after drawing on the display and before calling fadeIn().
    void setupFadeIn() {
        // Copy the plane and then set all pins to 0.
//        memcpy( & fadeInPlane, &plane, sizeof(plane));
        clearDisplay();
    }

    // Fade in whatever is stored in the fadeInPlane.
    void fadeIn() {
        // Loop over copy of plane and set pins in actual plane.
//        for (int b = 0; b < PWM_BITS; b++) {
//            for (int x = 0; x < WIDTH; x++) {
//                for (int y = 0; y < HEIGHT; y++) {
//                    PixelPins * fiBits = &fadeInPlane[b].row[y & 0xf].column[x];
//                    PixelPins * pins = &plane[b].row[y & 0xf].column[x];
//
//                    if (y < 16) {
//                        // Upper sub-panel
//                        pins.pins.r1 = fiBits.pins.r1;
//                        pins.pins.g1 = fiBits.pins.g1;
//                        pins.pins.b1 = fiBits.pins.b1;
//                    } else {
//                        // Lower sub-panel
//                        pins.pins.r2 = fiBits.pins.r2;
//                        pins.pins.g2 = fiBits.pins.g2;
//                        pins.pins.b2 = fiBits.pins.b2;
//                    }
//                }
//            }
//            //TODO: make this a param and/or dependent on PWM_BITS (longer sleep for fewer PWM_BITS).
//            usleep(100000); // 1/10 second
    }

    //    }
//
//    // Wipe all pixels down off the screen
    void wipeDown() {
//        for (int frame = 0; frame < HEIGHT; frame++) {
//            //Each time through, clear the top row.
//            for (int x = 0; x < WIDTH; x++) {
//                for (int b = PWM_BITS - 1; b >= 0; b--) {
//                    PixelPins * pins = &plane[b].row[(frame) & 0xf].column[x];
//
//                    if (frame < 16) {
//                        // Upper sub-panel
//                        pins.pins.r1 = 0;
//                        pins.pins.g1 = 0;
//                        pins.pins.b1 = 0;
//                    } else {
//                        // Lower sub-panel
//                        pins.pins.r2 = 0;
//                        pins.pins.g2 = 0;
//                        pins.pins.b2 = 0;
//                    }
//                }
//            }
//
//            for (int y = HEIGHT - 1; y > frame; y--) {
//                for (int x = 0; x < WIDTH; x++) {
//                    for (int b = PWM_BITS - 1; b >= 0; b--) {
//                        PixelPins * prevBits = &plane[b].row[(y - 1) & 0xf].column[x];
//                        PixelPins * currBits = &plane[b].row[y & 0xf].column[x];
//
//                        if (y == 16) //Special case when we cross the panels
//                        {
//                            currBits.pins.r2 = prevBits.pins.r1;
//                            currBits.pins.g2 = prevBits.pins.g1;
//                            currBits.pins.b2 = prevBits.pins.b1;
//                        } else if (y < 16) {
//                            // Upper sub-panel
//                            currBits.pins.r1 = prevBits.pins.r1;
//                            currBits.pins.g1 = prevBits.pins.g1;
//                            currBits.pins.b1 = prevBits.pins.b1;
//                        } else {
//                            // Lower sub-panel
//                            currBits.pins.r2 = prevBits.pins.r2;
//                            currBits.pins.g2 = prevBits.pins.g2;
//                            currBits.pins.b2 = prevBits.pins.b2;
//                        }
//                    }
//                }
//            }
//
//            //TODO: make this param and/or dependent on PWM_BITS (longer sleep for fewer PWM_BITS).
//            usleep(25000);
//        }
    }

    //
    public void drawPixel(int x, int y, Color color) {
        shapeDrawer.drawPixel(x, y, color);
    }

    public void drawLine(int x0, int y0, int x1, int y1, Color color) {
        shapeDrawer.drawLine(x0, y0, x1, y1, color);
    }

    // Draw a vertical line
    public void drawVLine(int x, int y, int h, Color color) {
        shapeDrawer.drawVLine(x, y, h, color);
    }

    // Draw a horizontal line
    public void drawHLine(int x, int y, int w, Color color) {
        shapeDrawer.drawHLine(x, y, w, color);
    }

    // Draw the outline of a rectangle (no fill)
    public void drawRect(int x, int y, int w, int h, Color color) {
        shapeDrawer.drawRect(x, y, w, h, color);
    }

    public void fillRect(int x, int y, int w, int h, Color color) {
        shapeDrawer.fillRect(x, y, w, h, color);
    }

    public void fillScreen(Color color) {
        shapeDrawer.fillRect(0, 0, WIDTH, HEIGHT, color);
    }

    // Draw a rounded rectangle with radius r.
    public void drawRoundRect(int x, int y, int w, int h, int r, Color color) {
        shapeDrawer.drawRoundRect(x, y, w, h, r, color);
    }

    public void fillRoundRect(int x, int y, int w, int h, int r, Color color) {
        shapeDrawer.fillRoundRect(x, y, w, h, r, color);
    }

    // Draw the outline of a cirle (no fill) - Midpoint Circle Algorithm
    void drawCircle(int x, int y, int r, Color color) {
        shapeDrawer.drawCircle(x, y, r, color);
    }

    // Draw one of the four quadrants of a circle.
    public void drawCircleQuadrant(int x, int y, int r, int quadrant, Color color) {
        shapeDrawer.drawCircleQuadrant(x, y, r, quadrant, color);
    }

    public void fillCircle(int x, int y, int r, Color color) {
        shapeDrawer.fillCircle(x, y, r, color);
    }

    public void fillCircleHalf(int x, int y, int r, int half, int stretch, Color color) {
        shapeDrawer.fillCircleHalf(x, y, r, half, stretch, color);
    }

    void drawArc(int x, int y, int r, float startAngle, float endAngle, Color color) {
        shapeDrawer.drawArc(x, y, r, startAngle, endAngle, color);
    }

    // Draw the outline of a wedge. //TODO: add inner radius
    public void drawWedge(int x, int y, int r, float startAngle, float endAngle, Color color) {
        shapeDrawer.drawWedge(x, y, r, startAngle, endAngle, color);
    }

    public void drawTriangle(int x1, int y1,
                             int x2, int y2,
                             int x3, int y3, Color color) {
        shapeDrawer.drawTriangle(x1, y1, x2, y2, x3, y3, color);
    }

    void fillTriangle(int x1, int y1,
                      int x2, int y2,
                      int x3, int y3, Color color) {
        shapeDrawer.fillTriangle(x1, y1, x2, y2, x3, y3, color);
    }

    // Special method to create a color wheel on the display.
    void drawColorWheel() {
        shapeDrawer.drawColorWheel();
    }

    void setTextCursor(int x, int y) {
        textCursorX = x;
        textCursorY = y;
    }

    void setFontColor(Color color) {
        fontColor = color;
    }

    public void setFontSize(int size) {
        fontSize = (size >= 3) ? 3 : size; //only 3 sizes for now

        if (fontSize == 1) {
            fontWidth = 3;
            fontHeight = 5;
        } else if (fontSize == 2) //medium (4x6)
        {
            fontWidth = 4;
            fontHeight = 6;
        } else if (fontSize == 3) //large (5x7)
        {
            fontWidth = 5;
            fontHeight = 7;
        }
    }

    public void setWordWrap(boolean wrap) {
        wordWrap = wrap;
    }

    public void writeText(String text) {
        for (char c : text.toCharArray()) {
            writeChar(c);
        }
    }

    // Write a character using the Text cursor and stored Font settings.
    public void writeChar(char c) {
        if (c == '\n') {
            textCursorX = 0;
            textCursorY += fontHeight;
        } else if (c == '\r') {
            ; //ignore
        } else {
            putChar(textCursorX, textCursorY, c, fontSize, fontColor);

            textCursorX += fontWidth + 1;

            if (wordWrap && (textCursorX > (WIDTH - fontWidth))) {
                textCursorX = 0;
                textCursorY += fontHeight + 1;
            }
        }
    }

    // Put a character on the display using glcd fonts.
    private void putChar(int x, int y, char c, int size, Color color) {
        char[] font;
        short fontWidth;
        short fontHeight;

        if (size == 1) { // small
            font = Fonts.FONT_3_X_5;
            fontWidth = 3;
            fontHeight = 5;
        } else if (size == 2) { // medium
            font = Fonts.FONT_4_X_6;
            fontWidth = 4;
            fontHeight = 6;
        } else if (size == 3) { // large
            font = Fonts.FONT_5_X_7;
            fontWidth = 5;
            fontHeight = 7;
        } else {
            throw new IllegalStateException(size + " is not supported. Try a number between 1-3 inclusive.");
        }

        for (int i = 0; i < fontWidth + 1; i++) {
            int line;

            if (i == fontWidth) {
                line = 0x0;
            } else {
                line = font[((c - 0x20) * fontWidth) + i];
            }

            for (int j = 0; j < fontHeight + 1; j++) {
                if ((line & 0x1) == 1) {
                    drawPixel(x + i, y + j, color);
                }

                line >>= 1;
            }
        }
    }

    //Leave output in 24-bit color (#RRGGBB)
    Color colorHSV(long hue, int sat, int val) {
//        uint8_t r, g, b, lo;
//        uint16_t s1, v1;
//
//        // Hue
//        hue %= 1536;             // -1535 to +1535
//        if (hue < 0) hue += 1536; //     0 to +1535
//
//        lo = hue & 255;  // Low byte = primary/secondary color mix
//
//        // High byte = sextant of colorwheel
//        switch (hue >> 8) {
//            case 0:
//                r = 255;
//                g = lo;
//                b = 0;
//                break; // R to Y
//            case 1:
//                r = 255 - lo;
//                g = 255;
//                b = 0;
//                break; // Y to G
//            case 2:
//                r = 0;
//                g = 255;
//                b = lo;
//                break; // G to C
//            case 3:
//                r = 0;
//                g = 255 - lo;
//                b = 255;
//                break; // C to B
//            case 4:
//                r = lo;
//                g = 0;
//                b = 255;
//                break; // B to M
//            default:
//                r = 255;
//                g = 0;
//                b = 255 - lo;
//                break; // M to R
//        }
//
//        // Saturation: add 1 so range is 1 to 256, which allows a bitwise right shift
//        // on the result rather than a costly divide.
//        s1 = sat + 1;
//
//        r = 255 - (((255 - r) * s1) >> 8);
//        g = 255 - (((255 - g) * s1) >> 8);
//        b = 255 - (((255 - b) * s1) >> 8);
//
//        // Value (brightness): Add 1, similar to above.
//        v1 = val + 1;
//
        Color c = new Color();
//        c.red = (r * v1) >> 8;
//        c.green = (g * v1) >> 8;
//        c.blue = (b * v1) >> 8;
//
        return c;
    }

}
