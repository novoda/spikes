package com.novoda.loadgauge;

import android.os.SystemClock;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Ads1015SingleEndedReader implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;

    Ads1015SingleEndedReader(I2cDevice i2cDevice, Gain gain) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
    }

    @Override
    public int read(Channel channel) {
        // Start with default values
        int config = ADS1015_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
            ADS1015_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
            ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
            ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
            ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
            ADS1015_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)
        config |= gain.value;
        config |= channel.value;
        config |= ADS1015_REG_CONFIG_OS_SINGLE;

        writeRegister(ADS1015_REG_POINTER_CONFIG, config);
        waitForConversionToComplete(CONVERSION_DELAY);
        return readRegister(ADS1015_REG_POINTER_CONVERT);
    }

    private void writeRegister(int reg, int value) {
        try {
            byte lsb = (byte) (value & 0xFF);
            byte msb = (byte) (value >> 8);
            byte[] b = new byte[]{msb, lsb};
            i2cBus.writeRegBuffer(reg, b, b.length);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + reg + " with value " + value, e);
        }
    }

    private void waitForConversionToComplete(long millis) {
        SystemClock.sleep(millis);
    }

    private int readRegister(int reg) {
        try {
            byte[] b = new byte[2];
            i2cBus.readRegBuffer(reg, b, b.length);
            return ByteBuffer.allocate(b.length)
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(b)
                    // Shift 12-bit results right 4 bits for the ADS1015
                    .getShort(0) >> BIT_SHIFT;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + reg, e);
        }
    }

    @Override
    public void startComparator(int thresholdInMv, ComparatorCallback callback) {
        throw new UnsupportedOperationException("Not my responsibility");
    }

    @Override
    public void close() {
        try {
            i2cBus.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
