package com.novoda.inkyphat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String INKY_PHAT_DISPLAY = "SPI3.1";

    private SpiDevice spiBus;
    private Gpio chipBusyPin;
    private Gpio chipResetPin;
    private Gpio chipCommandPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            spiBus.setMode(SpiDevice.MODE2);
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

            busyWait();

            // _POWER_SETTING
            chipCommandPin.setValue(false);
            byte[] buffer = new byte[]{0x01};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x02, 0x00, 0x00, 0x00};
            spiBus.write(buffer, buffer.length);

            // _BOOSTER_SOFT_START
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x02, 0x00, 0x00, 0x00};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x07, 0x07, 0x07};
            spiBus.write(buffer, buffer.length);

            // _POWER_ON
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x04};
            spiBus.write(buffer, buffer.length);

            busyWait();

            // _PANEL_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x00};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{(byte) 0b11001111};
            spiBus.write(buffer, buffer.length);

            // _VCOM_DATA_INTERVAL_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x50};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{(byte) 0b00000111 | 0b00000000}; // Set border to white by default
            spiBus.write(buffer, buffer.length);

            // _OSCILLATOR_CONTROL
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x30};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x29};
            spiBus.write(buffer, buffer.length);

            // _RESOLUTION_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x61};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x68, 0x00, (byte) 0xD4};
            spiBus.write(buffer, buffer.length);

            // _VCOM_DC_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{(byte) 0x82};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x0A};
            spiBus.write(buffer, buffer.length);

            // _PARTIAL_EXIT
            chipCommandPin.setValue(false);
            buffer = new byte[]{(byte) 0x92};
            spiBus.write(buffer, buffer.length);

            // stop turn on Display

            // start drawing
            // start black data transmission
            // _DATA_START_TRANSMISSION_1
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x10};
            spiBus.write(buffer, buffer.length);
            // TODO self._send_data(buf_black)
            chipCommandPin.setValue(true);
            buffer = new byte[]{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}; // A guess
            spiBus.write(buffer, buffer.length);
            // stop black data transmission

            // start red data transmission
            // _DATA_START_TRANSMISSION_2
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x13};
            spiBus.write(buffer, buffer.length);
            // TODO self._send_data(buf_red)
            chipCommandPin.setValue(true);
            buffer = new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1}; // A guess
            spiBus.write(buffer, buffer.length);
            // stop red data transmission

            // _DISPLAY_REFRESH
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x12};
            spiBus.write(buffer, buffer.length);
            // stop drawing

            // turn off display (display_fini)
            // _VCOM_DATA_INTERVAL_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x50};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x00};
            spiBus.write(buffer, buffer.length);
            // _POWER_SETTING
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x01};
            spiBus.write(buffer, buffer.length);
            chipCommandPin.setValue(true);
            buffer = new byte[]{0x02, 0x00, 0x00, 0x00};
            spiBus.write(buffer, buffer.length);
            //_POWER_OFF
            chipCommandPin.setValue(false);
            buffer = new byte[]{0x02};
            spiBus.write(buffer, buffer.length);

        } catch (IOException e) {
            throw new IllegalStateException("cannot init", e);
        }

    }

    private void busyWait() throws IOException {
        Log.d("TUT", "Wait for the e-paper driver to be ready to receive commands/data.");
        while (!chipBusyPin.getValue()) {
            // wait
        }
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
