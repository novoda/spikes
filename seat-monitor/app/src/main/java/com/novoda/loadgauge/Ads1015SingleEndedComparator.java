package com.novoda.loadgauge;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

class Ads1015SingleEndedComparator implements Ads1015 {

    private final I2cDevice i2cBus;
    private final Gain gain;
    private final Channel channel;

    Ads1015SingleEndedComparator(I2cDevice i2cDevice,
                                 Gain gain,
                                 Channel channel) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
        this.channel = channel;
    }

    @Override
    public void startComparatorSingleEnded(int thresholdInMv, ComparatorCallback callback) {
        throw new IllegalStateException("TODO");
    }

    @Override
    public int readDifferential() {
        throw new UnsupportedOperationException("Not my responsibility");
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
    public void close() {
        try {
            i2cBus.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
