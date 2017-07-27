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

    private SpiDevice spiBus;
    private Gpio chipBusyPin;
    private Gpio chipResetPin;
    private Gpio chipCommandPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();
        try {

            for (String s : service.getSpiBusList()) {
                Log.d("TUT", "NAME: " + s);
            }

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


        try {
            turnDisplayOn();
            update();
            turnOffDisplay();
        } catch (IOException e) {
            throw new IllegalStateException("cannot init", e);
        }

    }


    private void turnDisplayOn() throws IOException {
        Log.d("TUT", "start to turn display on");

        busyWait();

        // _POWER_SETTING
        sendCommand((byte) 0x01, new byte[]{0x02, 0x00, 0x0A, 0x00});
        byte[] buffer;
        // _BOOSTER_SOFT_START
        sendCommand((byte) 0x06, new byte[]{0x07, 0x07, 0x07});
        // _POWER_ON
        sendCommand((byte) 0x04);

        busyWait();

        // _PANEL_SETTING
        sendCommand((byte) 0x00, new byte[]{(byte) 0b11001111});

        // _VCOM_DATA_INTERVAL_SETTING
        sendCommand((byte) 0x50, new byte[]{(byte) 0b00000111 | 0b00000000}); // Set border to white by default

        // _OSCILLATOR_CONTROL
        sendCommand((byte) 0x30, new byte[]{0x29});

        // _RESOLUTION_SETTING
        sendCommand((byte) 0x61, new byte[]{0x68, 0x00, (byte) 0xD4});

        // _VCOM_DC_SETTING
        sendCommand((byte) 0x82, new byte[]{0x0A});

        // _PARTIAL_EXIT
        sendCommand((byte) 0x92);

        Log.d("TUT", "display on");
    }

    private void update() throws IOException {
        byte[] buffer;

        // start black data transmission
        // _DATA_START_TRANSMISSION_1
        buffer = new byte[NUMBER_OF_PIXEL_REGIONS]; // assumption that it addresses every pixel linearly
        for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
            buffer[i] = (byte) 0b11111111; // Make every pixel black
        }
        sendCommand((byte) 0x10, buffer);
        Log.d("TUT", "finish update black pixels");
        // stop black data transmission

        // start red data transmission
        // _DATA_START_TRANSMISSION_2

        buffer = new byte[NUMBER_OF_PIXEL_REGIONS]; // assumption that it addresses every pixel linearly
        for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
            buffer[i] = (byte) 0b10101010; // Make every second pixel red
        }
        sendCommand((byte) 0x13, buffer);
        Log.d("TUT", "finish update red pixels");
        // stop red data transmission

        // _DISPLAY_REFRESH
        Log.d("TUT", "refresh display");
        sendCommand((byte) 0x12);
        busyWait();
    }

    private void turnOffDisplay() throws IOException {
        Log.d("TUT", "turn off display");
        busyWait();

        // _VCOM_DATA_INTERVAL_SETTING
        sendCommand((byte) 0x50, new byte[]{0x00});
        // _POWER_SETTING
        sendCommand((byte) 0x01, new byte[]{0x02, 0x00, 0x00, 0x00});
        //_POWER_OFF
        sendCommand((byte) 0x02);
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
