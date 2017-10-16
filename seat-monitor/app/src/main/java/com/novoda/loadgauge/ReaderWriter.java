package com.novoda.loadgauge;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class ReaderWriter {

    private static final int ADS1015_BIT_SHIFT = 4;

    /**
     * @param device the device to write to
     * @param reg    the register to write to
     * @param value  a 16 bit value held in a 32 bit int
     */
    void writeRegister(I2cDevice device, int reg, int value) {
        try {
            byte lsb = (byte) (value & 0xFF);
            byte msb = (byte) (value >> 8);
            byte[] b = new byte[]{msb, lsb};
            device.writeRegBuffer(reg, b, b.length);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + reg + " with value " + value, e);
        }
    }

    int readRegister(I2cDevice device, int reg) {
        try {
            byte[] b = new byte[2];
            device.readRegBuffer(reg, b, b.length);
            return ByteBuffer.allocate(b.length)
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(b)
                    // Shift 12-bit results right 4 bits for the ADS1015
                    .getShort(0) >> ADS1015_BIT_SHIFT;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + reg, e);
        }
    }

}
