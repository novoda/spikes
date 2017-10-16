package com.novoda.loadgauge;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015SingleEndedComparator implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;
    private final Gpio alertReadyGpioBus;
    private final Channel channel;
    private final Ads1015SingleEndedReader singleEndedReader;
    private final ReaderWriter readerWriter;

    private ComparatorCallback callback;

    Ads1015SingleEndedComparator(I2cDevice i2cDevice,
                                 Gain gain,
                                 Gpio alertReadyGpioBus,
                                 Channel channel,
                                 Ads1015SingleEndedReader singleEndedReader) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.alertReadyGpioBus = alertReadyGpioBus;
        this.channel = channel;
        this.singleEndedReader = singleEndedReader;
        readerWriter = new ReaderWriter();
    }

    @Override
    public void startComparator(int thresholdInMv, ComparatorCallback callback) {
        this.callback = callback;
        try {
            alertReadyGpioBus.registerGpioCallback(thresholdHitCallback);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        startComparatorSingleEnded(thresholdInMv);
    }

    private void startComparatorSingleEnded(int thresholdInMv) {
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

        // Set single-ended input channel
        config |= channel.value;

        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, config);
    }

    private final GpioCallback thresholdHitCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            if (callback == null) {
                throw new IllegalStateException("Didn't expect to be called with no callback", new NullPointerException("callback is null"));
            }
            float multiplier = 3.0F;  // TODO multiplier should be based on Gain
//            configDifferential();
            int value = read(channel);
            Log.d("TUT", "Threshold hit raw " + value);
            float valueInMv = value * multiplier;
            Log.d("TUT", "Threshold hit out " + valueInMv + "mV");
            callback.onThresholdHit(valueInMv);
            return true;
        }
    };

    private void writeRegister(int reg, short value) {
        readerWriter.writeRegister(i2cBus, reg, value);
    }

    @Override
    public int read(Channel channel) {
        return singleEndedReader.read(channel);
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
