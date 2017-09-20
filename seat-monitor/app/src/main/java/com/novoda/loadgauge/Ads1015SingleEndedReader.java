package com.novoda.loadgauge;

import android.os.SystemClock;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015SingleEndedReader implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;
    private final Channel channel;

    Ads1015SingleEndedReader(I2cDevice i2cDevice,
                             Gain gain,
                             Channel channel) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.channel = channel;
    }

    @Override
    public int readSingleEnded() {
        // Start with default values
        short config = ADS1015_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
            ADS1015_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
            ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
            ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
            ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
            ADS1015_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)

        // Set PGA/voltage range
        config |= gain.value;

        // Set single-ended input channel
        config |= channel.value;

        // Set 'start single-conversion' bit
        config |= ADS1015_REG_CONFIG_OS_SINGLE;

        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, (short) config);

        // Wait for the conversion to complete
        delay(CONVERSION_DELAY);

        // Read the conversion results
        // Shift 12-bit results right 4 bits for the ADS1015
        return readRegister(ADS1015_REG_POINTER_CONVERT) >> BIT_SHIFT;
    }

    private void writeRegister(int reg, short value) {
        try {
            i2cBus.writeRegWord(reg, value);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + reg + " with value " + value, e);
        }
    }

    private void delay(long millis) {
        SystemClock.sleep(millis);
    }

    private short readRegister(int reg) {
        try {
            return i2cBus.readRegWord(reg);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + reg, e);
        }
    }

    @Override
    public int readDifferential() {
        throw new UnsupportedOperationException("Not my responsibility");
    }

    @Override
    public void startComparatorDifferential(int threshold, final ComparatorCallback callback) {
        throw new UnsupportedOperationException("Not my responsibility");
    }

    @Override
    public void startComparatorSingleEnded(int thresholdInMv, ComparatorCallback callback) {
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
