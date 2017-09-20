package com.novoda.loadgauge;

import android.os.SystemClock;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015DifferentialReader implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;
    private final DifferentialPins differentialPins;

    Ads1015DifferentialReader(I2cDevice i2cDevice,
                              Gain gain,
                              DifferentialPins differentialPins) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.differentialPins = differentialPins;
    }

    @Override
    public int readDifferential() {
        configDifferential();
        return readADCDifferential();

    }

    private void configDifferential() {
        //noinspection PointlessBitwiseExpression Ignore for Readability
        short config = ADS1015_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
            ADS1015_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
            ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
            ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
            ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
            ADS1015_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)

        // Set PGA/voltage range
        config |= gain.value;

        // Set channels
        config |= differentialPins.value;          // AIN0 = P, AIN1 = N

        // Set 'start single-conversion' bit
        config |= ADS1015_REG_CONFIG_OS_SINGLE;

        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, config);
        // Wait for the conversion to complete
        delay(CONVERSION_DELAY);
    }

    private int readADCDifferential() {
        // Read the conversion results
        int res = readRegister(ADS1015_REG_POINTER_CONVERT) >> BIT_SHIFT;
        // Shift 12-bit results right 4 bits for the ADS1015,
        // making sure we keep the sign bit intact
        if (res > 0x07FF) {
            // negative number - extend the sign to 16th bit
            res |= 0xF000;
        }
        return res;
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
    public int readSingleEnded() {
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
