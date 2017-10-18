package com.novoda.ads1015;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

import static com.novoda.ads1015.Ads1015.*;

interface Comparator {
    void start(Ads1015.Channel channel, int thresholdInMv, Ads1015.ComparatorCallback callback);

    void close();

    class ChannelComparator implements Comparator {

        private final RegisterReaderWriter registerReaderWriter;
        private final ConfigBuilder configBuilder;
        private final I2cDevice i2cBus;
        private final Gpio alertReadyGpioBus;
        private final Gain gain;

        private ComparatorCallback callback;

        ChannelComparator(RegisterReaderWriter registerReaderWriter,
                          ConfigBuilder configBuilder,
                          I2cDevice i2cBus,
                          Gpio alertReadyGpioBus,
                          Gain gain) {
            this.registerReaderWriter = registerReaderWriter;
            this.configBuilder = configBuilder;
            this.i2cBus = i2cBus;
            this.alertReadyGpioBus = alertReadyGpioBus;
            this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        }

        @Override
        public void start(Channel channel, int thresholdInMv, ComparatorCallback callback) {
            this.callback = callback;
            try {
                alertReadyGpioBus.registerGpioCallback(thresholdHitCallback);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            writeRegister(ADS1015_REG_POINTER_HITHRESH, (short) thresholdInMv); // TODO is this cast right
            writeRegister(ADS1015_REG_POINTER_CONFIG, configBuilder.comparator(gain, channel));
        }

        private final GpioCallback thresholdHitCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                if (callback == null) {
                    throw new IllegalStateException("Didn't expect to be called with no callback", new NullPointerException("callback is null"));
                }
                callback.onThresholdHit(readValueInMv());
                return true;
            }
        };

        private void writeRegister(int reg, int value) {
            registerReaderWriter.writeRegister(i2cBus, reg, value);
        }

        private float readValueInMv() {
            float multiplier = 3.0F;  // TODO multiplier should be based on Gain
//      writeRegister(ADS1015_REG_POINTER_CONFIG, configBuilder.comparator(gain, channel));
            int value = readRegister(ADS1015_REG_POINTER_CONVERT);
            Log.d("TUT", "Threshold hit raw " + value);
            return value * multiplier;
        }

        private int readRegister(int reg) {
            return registerReaderWriter.readRegister(i2cBus, reg);
        }

        @Override
        public void close() {
            try {
                alertReadyGpioBus.unregisterGpioCallback(thresholdHitCallback);
                alertReadyGpioBus.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    class Incomplete implements Comparator {

        private final String gpioPin;
        private final IOException cause;

        Incomplete(String gpioPin, IOException cause) {
            this.gpioPin = gpioPin;
            this.cause = cause;
        }

        @Override
        public void start(Channel channel, int thresholdInMv, ComparatorCallback callback) {
            throw new IllegalStateException(
                    gpioPin + " could not be connected to. " +
                            "You need the GPIO pin to get a callback for when the " + thresholdInMv + " threshold is hit. " +
                            "Fix this or you cannot use the comparator with your current config or hardware setup.", cause);
        }

        @Override
        public void close() {
            // Nothing to close
        }
    }
}
