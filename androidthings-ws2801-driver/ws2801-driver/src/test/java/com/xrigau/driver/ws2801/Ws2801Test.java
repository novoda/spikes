package com.xrigau.driver.ws2801;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.things.pio.SpiDevice;
import com.xrigau.driver.ws2801.Ws2801.Direction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.graphics.Color;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class Ws2801Test {

    private static final byte[] ANY_RGB = {0, 1, 42};

    @Mock
    private SpiDevice device;
    @Mock
    private ColorUnpacker unpacker;

    private Ws2801 driver;

    @Before
    public void setUp() throws IOException {
        driver = new Ws2801(device, unpacker, Direction.NORMAL);
        when(unpacker.unpack(anyInt())).thenReturn(ANY_RGB);
    }

    @Test
    public void configures1MHzClockFrequencyWhenCreated() throws Exception {
        verify(device).setFrequency(1_000_000);
    }

    @Test
    public void configuresClockToTransmitOnLeadingEdgeModeWhenCreated() throws Exception {
        verify(device).setMode(SpiDevice.MODE0);
    }

    @Test
    public void configuresBusToSend8BitsPerColorComponentWhenCreated() throws Exception {
        verify(device).setBitsPerWord(8);
    }

    @Test
    public void writesToSpiDeviceWhenWriting() throws Exception {
        int[] anyColors = {Color.RED, Color.DKGRAY, Color.GREEN, Color.WHITE, Color.YELLOW};
        driver.write(anyColors);

        verify(device).write(any(byte[].class), anyInt());
    }

    @Test
    public void reversesColorsWhenUsingReversedDirection() throws Exception {
        driver = new Ws2801(device, unpacker, Direction.REVERSED);

        driver.write(new int[]{0x0, 0x1});

        verify(unpacker).unpack(0x1);
        verify(unpacker).unpack(0X0);
    }

    @Test
    public void doesNotReverseColorsWhenUsingNormalDirection() throws Exception {
        driver = new Ws2801(device, unpacker, Direction.NORMAL);

        driver.write(new int[]{0x0, 0x1});

        verify(unpacker).unpack(0x0);
        verify(unpacker).unpack(0x1);
    }

    @Test
    public void closesSpiDeviceWhenClosing() throws Exception {
        driver.close();

        verify(device).close();
    }

}
