package com.novoda.ads1015;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015SingleEndedReader implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;
    private final ReaderWriter readerWriter;

    Ads1015SingleEndedReader(I2cDevice i2cDevice, Gain gain, ReaderWriter readerWriter) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.readerWriter = readerWriter;
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
        return readRegister(ADS1015_REG_POINTER_CONVERT);
    }

    private void writeRegister(int reg, int value) {
        readerWriter.writeRegister(i2cBus, reg, value);
    }

    private int readRegister(int reg) {
        return readerWriter.readRegister(i2cBus, reg);
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
