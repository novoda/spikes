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
    public int read() {
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
        writeRegister(ADS1015_REG_POINTER_CONFIG, config);

        // Wait for the conversion to complete
        delay(CONVERSION_DELAY);

        // Read the conversion results
        return readRegister(ADS1015_REG_POINTER_CONVERT);
    }

//    static void writeRegister(uint8_t i2cAddress, uint8_t reg, uint16_t value) {
//        Wire.beginTransmission(i2cAddress);
//        i2cwrite((uint8_t) reg);
//        i2cwrite((uint8_t) (value >> 8));
//        i2cwrite((uint8_t) (value & 0xFF));
//        Wire.endTransmission();
//    }

    private void writeRegister(int reg, short value) {
        try {
//            i2cBus.write(new byte[]{(byte) reg}, 1);
//            i2cBus.write(new byte[]{(byte) (value >> 8)}, 1);
//            i2cBus.write(new byte[]{(byte) (value & 0xFF)}, 1);
            
            i2cBus.writeRegWord(reg, value);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + reg + " with value " + value, e);
        }
    }

    private void delay(long millis) {
        SystemClock.sleep(millis);
    }

//    static uint16_t readRegister(uint8_t i2cAddress, uint8_t reg) {
//        Wire.beginTransmission(i2cAddress);
//        i2cwrite(ADS1015_REG_POINTER_CONVERT);
//        Wire.endTransmission();
//        Wire.requestFrom(i2cAddress, (uint8_t) 2);
//        return ((i2cread() << 8) | i2cread());
//    }

    private int readRegister(int reg) {
        try {
            i2cBus.write(new byte[]{(byte) reg}, 1);
            byte[] out = new byte[2];
            i2cBus.read(out, 2);
            short result = (short) ((out[0] << 8) | out[1]);
            // Shift 12-bit results right 4 bits for the ADS1015
            return result >>> BIT_SHIFT;
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
