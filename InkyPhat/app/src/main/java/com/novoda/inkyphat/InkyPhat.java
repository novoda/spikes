package com.novoda.inkyphat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

public interface InkyPhat extends AutoCloseable {
    /**
     * Width in pixels, when in the default orientation of {@link InkyPhat.Orientation#PORTRAIT}
     * If the {@link InkyPhat.Orientation} is set to {@link InkyPhat.Orientation#LANDSCAPE} this will be the Height
     */
    int WIDTH = 104;
    /**
     * Height in pixels, when in the default orientation of {@link InkyPhat.Orientation#PORTRAIT}
     * If the {@link InkyPhat.Orientation} is set to {@link InkyPhat.Orientation#LANDSCAPE} this will be the Width
     */
    int HEIGHT = 212;

    void setImage(int x, int y, PaletteImage image);

    /**
     * Set any pixel in the InkyPhat this is any pixel between {@link #WIDTH} & {@link #HEIGHT}
     * according to the {@link InkyPhat.Orientation} of your display
     * You can set the border multiple times it will only update when {@link #refresh()} is called
     * <p>
     * Note, not calling this method for a pixel will leave that pixel as {@link InkyPhat.Palette#WHITE}
     *
     * @param x     the x co-ordinate (or column) to set the pixel on
     * @param y     the y co-ordinate (or row) to set the pixel on
     * @param color the color you want the pixel to be
     */
    void setPixel(int x, int y, Palette color);

    /**
     * Set the border color around the InkyPhat this is the 1x1 pixels around each side
     * You can set the border multiple times it will only update when {@link #refresh()} is called
     *
     * @param color the color you want the border to be
     */
    void setBorder(Palette color);

    /**
     * Draw to the InkyPhat display
     */
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

                return new InkyPhatTriColourDisplay(device, chipBusyPin, chipResetPin, chipCommandPin, new PixelBuffer());
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

        public Palette getPixel(int position) {
            return colors[position];
        }

        public int totalPixels() {
            return colors.length;
        }

        public int getWidth() {
            return width;
        }
    }
}
