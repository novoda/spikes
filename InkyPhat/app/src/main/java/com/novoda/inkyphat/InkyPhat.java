package com.novoda.inkyphat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

interface InkyPhat extends AutoCloseable {
    /**
     * Width in pixels
     */
    int WIDTH = 104;
    /**
     * Height in pixels
     */
    int HEIGHT = 212;

    void setPixel(int x, int y, Palette color);

    void refresh();

    @Override
    void close();

    class Factory {
        static InkyPhat create(String spiBus, String gpioBusyPin, String gpioResetPin, String gpioCommandPin) {
            PeripheralManagerService service = new PeripheralManagerService();
            try {
                SpiDevice device = service.openSpiDevice(spiBus);

                Gpio chipBusyPin = service.openGpio(gpioBusyPin);
                Gpio chipResetPin = service.openGpio(gpioResetPin);
                Gpio chipCommandPin = service.openGpio(gpioCommandPin);

                return new InkyPhatTriColourDisplay(device, chipBusyPin, chipResetPin, chipCommandPin);
            } catch (IOException e) {
                throw new IllegalStateException("InkyPhat connection cannot be opened.", e);
            }
        }
    }

    enum Palette {
        BLACK, RED, WHITE
    }

    enum Orientation {
        LANDSCAPE, PORTRAIT
    }

    final class PaletteImage {

        private final Palette[] colors;
        private final int width;

        PaletteImage(Palette[] colors, int width) {
            this.colors = colors;
            this.width = width;
        }

        public void drawAt(InkyPhat inkyPhat, int x, int y) {
            int rowCount = 0;
            int pixelCount = 0;
            for (int i = 0; i < colors.length; i++) {
                int localX = x + i;
                int localY = y + i + rowCount;

                if (localX > InkyPhat.WIDTH || localY > InkyPhat.HEIGHT) { // TODO check orientation
                    continue;
                }

                InkyPhat.Palette color = colors[i];
                inkyPhat.setPixel(localX, localY, color);

                pixelCount++;
                if (pixelCount == width) {
                    rowCount++;
                    pixelCount = 0;
                }
            }
        }

    }
}
