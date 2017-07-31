package com.novoda.inkyphat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

public class InkyPhat implements AutoCloseable {

    /**
     * Width in pixels
     */
    public static final int WIDTH = 104;
    /**
     * Height in pixels
     */
    public static final int HEIGHT = 212;

    private static final boolean SPI_COMMAND = false;
    private static final boolean SPI_DATA = true;

    private static final int PIXELS_PER_REGION = 8;
    private static final int NUMBER_OF_PIXEL_REGIONS = WIDTH * HEIGHT / PIXELS_PER_REGION;

    private static final byte PANEL_SETTING = (byte) 0x00;
    private static final byte POWER_SETTING = (byte) 0x01;
    private static final byte POWER_OFF = (byte) 0x02;
    private static final byte POWER_ON = (byte) 0x04;
    private static final byte BOOSTER_SOFT_START = (byte) 0x06;
    private static final byte DATA_START_TRANSMISSION_1 = (byte) 0x10;
    private static final byte DATA_START_TRANSMISSION_2 = (byte) 0x13;
    private static final byte DISPLAY_REFRESH = (byte) 0x12;
    private static final byte OSCILLATOR_CONTROL = (byte) 0x30;
    private static final byte VCOM_DATA_INTERVAL_SETTING = (byte) 0x50;
    private static final byte RESOLUTION_SETTING = (byte) 0x61;
    private static final byte VCOM_DC_SETTING = (byte) 0x82;
    private static final byte PARTIAL_EXIT = (byte) 0x92;

    private final Palette[][] pixelBuffer = new Palette[WIDTH][HEIGHT];

    private final SpiDevice spiBus;
    private final Gpio chipBusyPin;
    private final Gpio chipResetPin;
    private final Gpio chipCommandPin;

    public enum Palette {
        BLACK, RED, WHITE
    }

    public static final class PaletteImage {

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

                if (localX > InkyPhat.WIDTH || localY > InkyPhat.HEIGHT) {
                    return;
                }

                Palette color = colors[i];
                inkyPhat.setPixel(localX, localY, color);

                pixelCount++;
                if (pixelCount == width) {
                    rowCount++;
                    pixelCount = 0;
                }
            }
        }

    }

    public static InkyPhat create(String spiBus, String gpioBusyPin, String gpioResetPin, String gpioCommandPin) {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            SpiDevice device = service.openSpiDevice(spiBus);

            Gpio chipBusyPin = service.openGpio(gpioBusyPin);
            Gpio chipResetPin = service.openGpio(gpioResetPin);
            Gpio chipCommandPin = service.openGpio(gpioCommandPin);

            return new InkyPhat(device, chipBusyPin, chipResetPin, chipCommandPin);
        } catch (IOException e) {
            throw new IllegalStateException("InkyPhat connection cannot be opened.", e);
        }
    }

    InkyPhat(SpiDevice spiBus, Gpio chipBusyPin, Gpio chipResetPin, Gpio chipCommandPin) {
        this.spiBus = spiBus;
        this.chipBusyPin = chipBusyPin;
        this.chipResetPin = chipResetPin;
        this.chipCommandPin = chipCommandPin;
        init();
    }

    private void init() {
        try {
            spiBus.setMode(SpiDevice.MODE0);
            chipCommandPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            chipCommandPin.setActiveType(Gpio.ACTIVE_HIGH);
            chipResetPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            chipResetPin.setActiveType(Gpio.ACTIVE_HIGH);
            chipBusyPin.setDirection(Gpio.DIRECTION_IN);
            chipBusyPin.setActiveType(Gpio.ACTIVE_HIGH);
        } catch (IOException e) {
            throw new IllegalStateException("InkyPhat cannot be configured.", e);
        }
    }

    public void setPixel(int x, int y, Palette color) {
        if (x > WIDTH) {
            throw new IllegalStateException(x + " cannot be drawn. Max width is " + WIDTH);
        }
        if (y > HEIGHT) {
            throw new IllegalStateException(y + " cannot be drawn. Max height is " + HEIGHT);
        }
        pixelBuffer[x][y] = color;
    }

    public void refresh() {
        try {
            turnDisplayOn();
            update();
            turnDisplayOff();
        } catch (IOException e) {
            throw new IllegalStateException("cannot init", e);
        }
    }

    private void turnDisplayOn() throws IOException {
        busyWait();

        sendCommand(POWER_SETTING, new byte[]{0x07, 0x00, 0x0A, 0x00});
        sendCommand(BOOSTER_SOFT_START, new byte[]{0x07, 0x07, 0x07});
        sendCommand(POWER_ON);

        busyWait();

        sendCommand(PANEL_SETTING, new byte[]{(byte) 0b11001111});
        sendCommand(VCOM_DATA_INTERVAL_SETTING, new byte[]{(byte) 0b00000111 | (byte) 0b00000000}); // Set border to white by default

        sendCommand(OSCILLATOR_CONTROL, new byte[]{0x29});
        sendCommand(RESOLUTION_SETTING, new byte[]{0x68, 0x00, (byte) 0xD4});
        sendCommand(VCOM_DC_SETTING, new byte[]{0x0A});

        sendCommand(PARTIAL_EXIT);
    }

    private void update() throws IOException {
        byte[] black = convertPixelBufferToDisplayColor(Palette.BLACK);
        sendCommand(DATA_START_TRANSMISSION_1, black);
        byte[] red = convertPixelBufferToDisplayColor(Palette.RED);
        sendCommand(DATA_START_TRANSMISSION_2, red);

        sendCommand(DISPLAY_REFRESH);
    }

    private byte[] convertPixelBufferToDisplayColor(Palette color) {
        return mapPaletteArrayToDisplayByteArray(flatten(pixelBuffer), color);
    }

    private static Palette[] flatten(Palette[][] twoDimensionalPaletteArray) {
        Palette[] flattenedArray = new Palette[WIDTH * HEIGHT];
        int index = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Palette color = twoDimensionalPaletteArray[x][y];
                flattenedArray[index++] = color;
            }
        }
        return flattenedArray;
    }

    /**
     * Every 8 pixels of the display is represented by a byte
     *
     * @param palette an array colors expecting to be drawn
     * @param choice  the color we are filtering for
     * @return a byte array representing the palette of a single color
     */
    private static byte[] mapPaletteArrayToDisplayByteArray(Palette[] palette, Palette choice) {
        byte[] display = new byte[NUMBER_OF_PIXEL_REGIONS];
        int bitPosition = 7;
        int segment = 0;
        byte colorByte = 0b00000000;
        for (Palette color : palette) {
            if (color == choice) {
                colorByte |= 1 << bitPosition;
            }
            bitPosition--;
            if (bitPosition == -1) {
                display[segment++] = colorByte;
                bitPosition = 7;
                colorByte = 0b00000000;
            }
        }
        return display;
    }

    private void turnDisplayOff() throws IOException {
        busyWait();

        sendCommand(VCOM_DATA_INTERVAL_SETTING, new byte[]{0x00});
        sendCommand(POWER_SETTING, new byte[]{0x02, 0x00, 0x00, 0x00});
        sendCommand(POWER_OFF);
    }

    private void sendCommand(byte command) throws IOException {
        chipCommandPin.setValue(SPI_COMMAND);
        byte[] buffer = new byte[]{command};
        spiBus.write(buffer, buffer.length);
    }

    private void sendCommand(byte command, byte[] data) throws IOException {
        sendCommand(command);
        sendData(data);
    }

    private void sendData(byte[] data) throws IOException {
        chipCommandPin.setValue(SPI_DATA);
        spiBus.write(data, data.length);
    }

    /**
     * Wait for the e-paper driver to be ready to receive commands/data.
     *
     * @throws IOException error accessing GPIO
     */
    private void busyWait() throws IOException {
        while (true) {
            if (chipBusyPin.getValue()) {
                break;
            }
        }
    }

    @Override
    public void close() {
        try {
            spiBus.close();
            chipBusyPin.close();
            chipResetPin.close();
            chipCommandPin.close();
        } catch (IOException e) {
            throw new IllegalStateException("InkyPhat connection cannot be closed, you may experience errors on next launch.", e);
        }
    }
}
