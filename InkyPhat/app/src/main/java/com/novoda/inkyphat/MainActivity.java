package com.novoda.inkyphat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String INKY_PHAT_DISPLAY = "SPI0.0";
    private static final boolean SPI_COMMAND = false;
    private static final boolean SPI_DATA = true;

    private static final int WIDTH = 212;
    private static final int HEIGHT = 104;

    private SpiDevice spiBus;
    private Gpio chipBusyPin;
    private Gpio chipResetPin;
    private Gpio chipCommandPin;
    private static final int NUMBER_OF_PIXEL_REGIONS = WIDTH * HEIGHT / 8;

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
            // Start turn on Display
            Log.d("TUT", "start to turn display on");

            busyWait();

            // _POWER_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            byte[] buffer = new byte[]{0x01};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x02, 0x00, 0x0A, 0x00};
            spiBus.write(buffer, buffer.length);

            // _BOOSTER_SOFT_START
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x06};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x07, 0x07, 0x07};
            spiBus.write(buffer, buffer.length);

            // _POWER_ON
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x04};
            spiBus.write(buffer, buffer.length);

            busyWait();

            // _PANEL_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x00};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{(byte) 0b11001111};
            spiBus.write(buffer, buffer.length);

            // _VCOM_DATA_INTERVAL_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x50};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{(byte) 0b00000111 | 0b00000000}; // Set border to white by default
            spiBus.write(buffer, buffer.length);

            // _OSCILLATOR_CONTROL
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x30};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x29};
            spiBus.write(buffer, buffer.length);

            // _RESOLUTION_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x61};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x68, 0x00, (byte) 0xD4};
            spiBus.write(buffer, buffer.length);

            // _VCOM_DC_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{(byte) 0x82};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x0A};
            spiBus.write(buffer, buffer.length);

            // _PARTIAL_EXIT
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{(byte) 0x92};
            spiBus.write(buffer, buffer.length);

            Log.d("TUT", "display on");
            // stop turn on Display

            // start drawing
            // start black data transmission
            // _DATA_START_TRANSMISSION_1
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x10};
            spiBus.write(buffer, buffer.length);
            // TODO self._send_data(buf_black)
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[NUMBER_OF_PIXEL_REGIONS]; // assumption that it addresses every pixel linearly
            for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
                buffer[i] = (byte) 0b11111111; // Make every pixel black
            }
            spiBus.write(buffer, buffer.length);
            Log.d("TUT", "finish update black pixels");
            // stop black data transmission

            // start red data transmission
            // _DATA_START_TRANSMISSION_2
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x13};
            spiBus.write(buffer, buffer.length);
            // TODO self._send_data(buf_red)
            chipCommandPin.setValue(SPI_DATA);

            buffer = new byte[NUMBER_OF_PIXEL_REGIONS]; // assumption that it addresses every pixel linearly
            for (int i = 0; i < NUMBER_OF_PIXEL_REGIONS; i++) {
                buffer[i] = (byte) 0b10101010; // Make every second pixel red
            }
            spiBus.write(buffer, buffer.length);
            Log.d("TUT", "finish update red pixels");
            // stop red data transmission

            // _DISPLAY_REFRESH
            Log.d("TUT", "refresh display");
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x12};
            spiBus.write(buffer, buffer.length);
            // stop drawing

            busyWait();

            // turn off display (display_fini)

            Log.d("TUT", "turn off display");
            busyWait();

            // _VCOM_DATA_INTERVAL_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x50};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x00};
            spiBus.write(buffer, buffer.length);
            // _POWER_SETTING
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x01};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(SPI_DATA);
            buffer = new byte[]{0x02, 0x00, 0x00, 0x00};
            spiBus.write(buffer, buffer.length);
            //_POWER_OFF
            chipCommandPin.setValue(SPI_COMMAND);
            buffer = new byte[]{0x02};
            spiBus.write(buffer, buffer.length);

        } catch (
                IOException e)

        {
            throw new IllegalStateException("cannot init", e);
        }

    }

    private void busyWait() throws IOException {
        Log.d("TUT", "Wait for the e-paper driver to be ready to receive commands/data.");
        while (!chipBusyPin.getValue()) {
            // wait
            Log.v("TUT", "waiting");
        }
        Log.d("TUT", "READY");
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
