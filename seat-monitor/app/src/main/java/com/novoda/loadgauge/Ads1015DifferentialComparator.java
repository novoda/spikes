package com.novoda.loadgauge;

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
    private final ReaderWriter readerWriter;

    private ComparatorCallback callback;

    Ads1015DifferentialComparator(I2cDevice i2cDevice,
                                  Gpio alertReadyGpioBus,
                                  Gain gain,
                                  DifferentialPins differentialPins,
                                  ReaderWriter readerWriter) {
        this.i2cBus = i2cDevice;
        this.alertReadyGpioBus = alertReadyGpioBus;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.differentialPins = differentialPins;
        this.readerWriter = readerWriter;
    }


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
        writeRegister(ADS1015_REG_POINTER_HITHRESH, (short) thresholdInMv); // TODO is this cast right

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

    private int readADCDifferential() {
        // Read the conversion results
        int res = readRegister(ADS1015_REG_POINTER_CONVERT);
        // Shift 12-bit results right 4 bits for the ADS1015,
        // making sure we keep the sign bit intact
        if (res > 0x07FF) {
            // negative number - extend the sign to 16th bit
            res |= 0xF000;
        }
        return res;
    }

    private void writeRegister(int reg, short value) {
        readerWriter.writeRegister(i2cBus, reg, value);
    }

    private int readRegister(int reg) {
        return readerWriter.readRegister(i2cBus, reg);
    }

    @Override
    public int read(Channel channel) {
        throw new UnsupportedOperationException("Not my responsibility");
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
