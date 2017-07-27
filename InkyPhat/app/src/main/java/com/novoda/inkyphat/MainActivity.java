package com.novoda.inkyphat;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final String INKY_PHAT_DISPLAY = "SPI0.0";
    private static final boolean SPI_COMMAND = false;
    private static final boolean SPI_DATA = true;

    static final int WIDTH = 212;
    static final int HEIGHT = 104;
    private static final int NUMBER_OF_PIXEL_REGIONS = WIDTH * HEIGHT / 8;

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

    private SpiDevice spiBus;
    private Gpio chipBusyPin;
    private Gpio chipResetPin;
    private Gpio chipCommandPin;

    public enum Palette {
        WHITE, BLACK, RED
    }

    private Palette[][] pixelBuffer = new Palette[HEIGHT][WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "onCreate");
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            spiBus = service.openSpiDevice(INKY_PHAT_DISPLAY);

            chipCommandPin = service.openGpio("BCM22");
            chipResetPin = service.openGpio("BCM27");
            chipBusyPin = service.openGpio("BCM17");

        } catch (IOException e) {
            throw new IllegalStateException(INKY_PHAT_DISPLAY + " connection cannot be opened.", e);
        }
        try {
            spiBus.setMode(SpiDevice.MODE0);
//            spiBus.setFrequency(1_000_000); // 1Mhz
//            spiBus.setBitsPerWord(8);

            chipCommandPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            chipCommandPin.setActiveType(Gpio.ACTIVE_HIGH);
            chipResetPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            chipResetPin.setActiveType(Gpio.ACTIVE_HIGH);
            chipBusyPin.setDirection(Gpio.DIRECTION_IN);
            chipBusyPin.setActiveType(Gpio.ACTIVE_HIGH);
        } catch (IOException e) {
            throw new IllegalStateException(INKY_PHAT_DISPLAY + " cannot be configured.", e);
        }

        Tests.drawThreeSquares(this);

        try {
            turnDisplayOn();
            Log.d("TUT", "display on");
            update();
            Log.d("TUT", "display refreshed");
            turnOffDisplay();
            Log.d("TUT", "display off");
        } catch (IOException e) {
            throw new IllegalStateException("cannot init", e);
        }

    }

    void setPixel(int x, int y, Palette color) {
        if (x > WIDTH) {
            throw new IllegalStateException(x + " cannot be drawn. Max width is " + WIDTH);
        }
        if (y > HEIGHT) {
            throw new IllegalStateException(y + " cannot be drawn. Max height is " + WIDTH);
        }
        pixelBuffer[y][x] = color;
    }

    private void turnDisplayOn() throws IOException {
        busyWait();

        sendCommand(POWER_SETTING, new byte[]{0x07, 0x00, 0x0A, 0x00});
        sendCommand(BOOSTER_SOFT_START, new byte[]{0x07, 0x07, 0x07});
        sendCommand(POWER_ON);

        busyWait();

        sendCommand(PANEL_SETTING, new byte[]{(byte) 0b11001111});
        sendCommand(VCOM_DATA_INTERVAL_SETTING, new byte[]{(byte) 0b00000111 | 0b00000000}); // Set border to white by default

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

        byte[] buffer = new byte[NUMBER_OF_PIXEL_REGIONS];
        for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
            buffer[i] = (byte) 0b01010101; // Make every other pixel black
        }
        sendCommand(DATA_START_TRANSMISSION_1, buffer);

        buffer = new byte[NUMBER_OF_PIXEL_REGIONS];
        for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
            buffer[i] = (byte) 0b10101010; // Make every other pixel red
        }
        sendCommand(DATA_START_TRANSMISSION_2, buffer);

        sendCommand(DISPLAY_REFRESH);
    }

    private byte[] convertPixelBufferToDisplayColor(Palette color) {
        byte[] display = new byte[NUMBER_OF_PIXEL_REGIONS];
        int bytePosition = 1;
        byte colorByte = 0b00000000;
        for (int x = 0; x < pixelBuffer.length; x++) {
            for (int y = 0; y < pixelBuffer.length; y++) {
                Palette pixelColor = pixelBuffer[y][x];
                if (pixelColor == color) {
                    colorByte |= 1 << bytePosition;
                }
                bytePosition++;

                if (bytePosition == 8) {
                    display[x * y] = colorByte;
                    bytePosition = 0;
                }
            }
        }
        return display;
    }

    private void turnOffDisplay() throws IOException {
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

    private long then = SystemClock.currentThreadTimeMillis();

    private void busyWait() throws IOException {
        Log.d("TUT", "Wait for the e-paper driver to be ready to receive commands/data.");
        while (!chipBusyPin.getValue()) {
            // wait
            long now = SystemClock.currentThreadTimeMillis();
            if (then + TimeUnit.SECONDS.toMillis(1) < now) {
                Log.v("TUT", "waiting 1 second");
                then = now;
            }
        }
        Log.d("TUT", "finished waiting");
    }

    @Override
    protected void onDestroy() {
        try {
            spiBus.close();
            chipBusyPin.close();
            chipResetPin.close();
            chipCommandPin.close();
        } catch (IOException e) {
            Log.e("TUT", INKY_PHAT_DISPLAY + "connection cannot be closed, you may experience errors on next launch.", e);
        }
        super.onDestroy();
    }
}
