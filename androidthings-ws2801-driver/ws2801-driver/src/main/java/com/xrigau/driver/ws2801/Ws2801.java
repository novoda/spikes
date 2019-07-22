package com.xrigau.driver.ws2801;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import android.graphics.Color;

import java.io.IOException;

/**
 * Device driver for WS2801 RGB LEDs using 2-wire SPI.
 * <p>
 * For more information on SPI, see:
 * https://en.wikipedia.org/wiki/Serial_Peripheral_Interface_Bus
 * For information on the WS2801 protocol, see:
 * https://cdn-shop.adafruit.com/datasheets/WS2801.pdf
 */
public class Ws2801 implements AutoCloseable {

    // Device SPI Configuration constants
    private static final int SPI_BPW = 8; // Bits per word
    private static final int SPI_FREQUENCY = 1_000_000;
    private static final int SPI_MODE = SpiDevice.MODE0; // Mode 0 seems to work best for WS2801
    private static final int WS2801_PACKET_LENGTH = 3; // R, G & B in any order

    /**
     * Color ordering for the RGB LED messages; the most common modes are BGR and RGB.
     */
    public enum Mode {
        RGB,
        RBG,
        GRB,
        GBR,
        BRG,
        BGR
    }

    public enum Direction {
        NORMAL,
        REVERSED,
    }

    private final SpiDevice device;
    private final ColorUnpacker colorUnpacker;
    // Direction of the led strip;
    private final Direction direction;

    /**
     * Create a new Ws2801 driver.
     *
     * @param spiBusPort Name of the SPI bus
     */
    public static Ws2801 create(String spiBusPort) throws IOException {
        return create(spiBusPort, Mode.RGB);
    }

    /**
     * Create a new Ws2801 driver.
     *
     * @param spiBusPort Name of the SPI bus
     * @param ledMode    The {@link Mode} indicating the red/green/blue byte ordering for the device.
     */
    public static Ws2801 create(String spiBusPort, Mode ledMode) throws IOException {
        return create(spiBusPort, ledMode, Direction.NORMAL);
    }

    /**
     * Create a new Ws2801 driver.
     *
     * @param spiBusPort Name of the SPI bus
     * @param ledMode    The {@link Mode} indicating the red/green/blue byte ordering for the device.
     * @param direction  The {@link Direction} or the LED strip.
     */
    public static Ws2801 create(String spiBusPort, Mode ledMode, Direction direction) throws IOException {
        PeripheralManagerService pioService = new PeripheralManagerService();
        try {
            return new Ws2801(pioService.openSpiDevice(spiBusPort), new ColorUnpacker(ledMode), direction);
        } catch (IOException e) {
            throw new IOException("Unable to open SPI device in bus port " + spiBusPort, e);
        }
    }

    Ws2801(SpiDevice device, ColorUnpacker colorUnpacker, Direction direction) throws IOException {
        this.device = device;
        this.colorUnpacker = colorUnpacker;
        this.direction = direction;
        configure(device);
    }

    private static void configure(SpiDevice device) throws IOException {
        device.setFrequency(SPI_FREQUENCY);
        device.setMode(SPI_MODE);
        device.setBitsPerWord(SPI_BPW);
    }

    /**
     * Writes the current RGB LED data to the peripheral bus.
     *
     * @param colors An array of integers corresponding to a {@link Color}.
     *               The size of the array should match the number of LEDs in the strip.
     * @throws IOException if writing to the SPI interface fails.
     */
    public void write(int[] colors) throws IOException {
        byte[] ledData = new byte[WS2801_PACKET_LENGTH * colors.length];

        for (int i = 0; i < colors.length; i++) {
            int outputPosition = i * WS2801_PACKET_LENGTH;
            int di = direction == Direction.NORMAL ? i : colors.length - i - 1;
            System.arraycopy(colorUnpacker.unpack(colors[di]), 0, ledData, outputPosition, WS2801_PACKET_LENGTH);
        }

        device.write(ledData, ledData.length);
    }

    /**
     * Releases the SPI interface.
     */
    @Override
    public void close() throws IOException {
        device.close();
    }

}
