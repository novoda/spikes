package com.novoda.loadgauge;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015DifferentialComparator implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gpio alertReadyGpioBus;
    private final Gain gain;
    private final DifferentialPins differentialPins;
    private final Ads1015DifferentialReader differentialReader;

    private ComparatorCallback callback;

    Ads1015DifferentialComparator(I2cDevice i2cDevice,
                                  Gpio alertReadyGpioBus,
                                  Gain gain,
                                  DifferentialPins differentialPins,
                                  Ads1015DifferentialReader differentialReader) {
        this.i2cBus = i2cDevice;
        this.alertReadyGpioBus = alertReadyGpioBus;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.differentialPins = differentialPins;
        this.differentialReader = differentialReader;
    }


    private final GpioCallback thresholdHitCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            if (callback == null) {
                throw new IllegalStateException("Didn't expect to be called with no callback", new NullPointerException("callback is null"));
            }
            Log.d("TUT", "Threshold hit");
            float multiplier = 3.0F;  // TODO multiplier should be based on Gain
//            configDifferential();
            float valueInMv = readADCDifferential() * multiplier;
            callback.onThresholdHit((int) valueInMv);
            return true;
        }
    };

    @Override
    public void startComparator(int thresholdInMv, ComparatorCallback callback) {
        this.callback = callback;
        try {
            alertReadyGpioBus.registerGpioCallback(thresholdHitCallback);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        int differential = readADCDifferential();
        Log.d("TUT", "Start value : " + differential);
        startComparatorDifferential(differential + 1);
    }

    private void startComparatorDifferential(int thresholdInMv) {
        // Set the high threshold register
        // Shift 12-bit results left 4 bits for the ADS1015
        writeRegister(ADS1015_REG_POINTER_HITHRESH, (short) (thresholdInMv << BIT_SHIFT));

        // Start with default values
        short config = ADS1015_REG_CONFIG_CQUE_1CONV | // Comparator enabled and asserts on 1 match
            ADS1015_REG_CONFIG_CLAT_LATCH | // Latching mode
            ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
            ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
            ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
            ADS1015_REG_CONFIG_MODE_CONTIN;   // Continuous conversion mode

        // Set PGA/voltage range
        config |= gain.value;

        config |= differentialPins.value;

        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, config);
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
    public int read() {
        return differentialReader.read();
    }

    @Override
    public void close() {
        try {
            i2cBus.close();
            alertReadyGpioBus.unregisterGpioCallback(thresholdHitCallback);
            alertReadyGpioBus.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
