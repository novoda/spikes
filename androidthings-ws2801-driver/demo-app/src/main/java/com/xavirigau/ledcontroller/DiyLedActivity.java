package com.xavirigau.ledcontroller;

import static android.content.ContentValues.TAG;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

// Works with LED strip WS2801
public class DiyLedActivity extends Activity {

    private SpiDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            mDevice = manager.openSpiDevice(BoardDefaults.getSPIPort());
            configureSpiDevice(mDevice);
            byte[] b = {10/*R*/, 100 /*B*/, 40/*G*/};
            sendCommand(mDevice, b);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access SPI device", e);
        }
    }

    public void configureSpiDevice(SpiDevice device) throws IOException {
        device.setMode(SpiDevice.MODE2);
        device.setFrequency(1000000);
        device.setBitsPerWord(8);
    }


    // Half-duplex data transfer
    public void sendCommand(SpiDevice device, byte[] buffer) throws IOException {
        Log.e("YOLO", "I'm sending");
        device.write(buffer, buffer.length);
    }

    @Override
    protected void onDestroy() {
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close SPI device", e);
            }
        }

        super.onDestroy();
    }

}
