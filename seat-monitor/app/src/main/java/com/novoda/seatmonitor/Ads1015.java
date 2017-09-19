package com.novoda.seatmonitor;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Ads1015 {

    private static final int ADS1015_REG_POINTER_MASK = 0x03;
    private static final int ADS1015_REG_POINTER_CONVERT = 0x00;
    private static final int ADS1015_REG_POINTER_CONFIG = 0x01;
    private static final int ADS1015_REG_POINTER_LOWTHRESH = 0x02;
    private static final int ADS1015_REG_POINTER_HITHRESH = 0x03;

    private static final int ADS1015_REG_CONFIG_OS_MASK = 0x8000;
    private static final int ADS1015_REG_CONFIG_OS_SINGLE = 0x8000;  // Write: Set to start a single-conversion
    private static final int ADS1015_REG_CONFIG_OS_BUSY = 0x0000;  // Read: Bit = 0 when conversion is in progress
    private static final int ADS1015_REG_CONFIG_OS_NOTBUSY = 0x8000;  // Read: Bit = 1 when device is not performing a conversion
    private static final int ADS1015_REG_CONFIG_MUX_MASK = 0x7000;
    private static final int ADS1015_REG_CONFIG_MUX_DIFF_0_1 = 0x0000;  // Differential P = AIN0, N = AIN1 (default)
    private static final int ADS1015_REG_CONFIG_MUX_DIFF_0_3 = 0x1000;  // Differential P = AIN0, N = AIN3
    private static final int ADS1015_REG_CONFIG_MUX_DIFF_1_3 = 0x2000;  // Differential P = AIN1, N = AIN3
    private static final int ADS1015_REG_CONFIG_MUX_DIFF_2_3 = 0x3000;  // Differential P = AIN2, N = AIN3
    private static final int ADS1015_REG_CONFIG_MUX_SINGLE_0 = 0x4000;  // Single-ended AIN0
    private static final int ADS1015_REG_CONFIG_MUX_SINGLE_1 = 0x5000;  // Single-ended AIN1
    private static final int ADS1015_REG_CONFIG_MUX_SINGLE_2 = 0x6000;  // Single-ended AIN2
    private static final int ADS1015_REG_CONFIG_MUX_SINGLE_3 = 0x7000;  // Single-ended AIN3
    private static final int ADS1015_REG_CONFIG_PGA_MASK = 0x0E00;
    private static final int ADS1015_REG_CONFIG_PGA_6_144V = 0x0000;  // +/-6.144V range = Gain 2/3
    private static final int ADS1015_REG_CONFIG_PGA_4_096V = 0x0200;  // +/-4.096V range = Gain 1
    private static final int ADS1015_REG_CONFIG_PGA_2_048V = 0x0400;  // +/-2.048V range = Gain 2 (default)
    private static final int ADS1015_REG_CONFIG_PGA_1_024V = 0x0600;  // +/-1.024V range = Gain 4
    private static final int ADS1015_REG_CONFIG_PGA_0_512V = 0x0800;  // +/-0.512V range = Gain 8
    private static final int ADS1015_REG_CONFIG_PGA_0_256V = 0x0A00;  // +/-0.256V range = Gain 16
    private static final int ADS1015_REG_CONFIG_MODE_MASK = 0x0100;
    private static final int ADS1015_REG_CONFIG_MODE_CONTIN = 0x0000;  // Continuous conversion mode
    private static final int ADS1015_REG_CONFIG_MODE_SINGLE = 0x0100;  // Power-down single-shot mode (default)
    private static final int ADS1015_REG_CONFIG_DR_MASK = 0x00E0;
    private static final int ADS1015_REG_CONFIG_DR_128SPS = 0x0000;  // 128 samples per second
    private static final int ADS1015_REG_CONFIG_DR_250SPS = 0x0020;  // 250 samples per second
    private static final int ADS1015_REG_CONFIG_DR_490SPS = 0x0040;  // 490 samples per second
    private static final int ADS1015_REG_CONFIG_DR_920SPS = 0x0060;  // 920 samples per second
    private static final int ADS1015_REG_CONFIG_DR_1600SPS = 0x0080;  // 1600 samples per second (default)
    private static final int ADS1015_REG_CONFIG_DR_2400SPS = 0x00A0;  // 2400 samples per second
    private static final int ADS1015_REG_CONFIG_DR_3300SPS = 0x00C0;  // 3300 samples per second
    private static final int ADS1015_REG_CONFIG_CMODE_MASK = 0x0010;
    private static final int ADS1015_REG_CONFIG_CMODE_TRAD = 0x0000;  // Traditional comparator with hysteresis (default)
    private static final int ADS1015_REG_CONFIG_CMODE_WINDOW = 0x0010;  // Window comparator
    private static final int ADS1015_REG_CONFIG_CPOL_MASK = 0x0008;
    private static final int ADS1015_REG_CONFIG_CPOL_ACTVLOW = 0x0000;  // ALERT/RDY pin is low when active (default)
    private static final int ADS1015_REG_CONFIG_CPOL_ACTVHI = 0x0008;  // ALERT/RDY pin is high when active
    private static final int ADS1015_REG_CONFIG_CLAT_MASK = 0x0004;  // Determines if ALERT/RDY pin latches once asserted
    private static final int ADS1015_REG_CONFIG_CLAT_NONLAT = 0x0000;  // Non-latching comparator (default)
    private static final int ADS1015_REG_CONFIG_CLAT_LATCH = 0x0004;  // Latching comparator
    private static final int ADS1015_REG_CONFIG_CQUE_MASK = 0x0003;
    private static final int ADS1015_REG_CONFIG_CQUE_1CONV = 0x0000;  // Assert ALERT/RDY after one conversions
    private static final int ADS1015_REG_CONFIG_CQUE_2CONV = 0x0001;  // Assert ALERT/RDY after two conversions
    private static final int ADS1015_REG_CONFIG_CQUE_4CONV = 0x0002;  // Assert ALERT/RDY after four conversions
    private static final int ADS1015_REG_CONFIG_CQUE_NONE = 0x0003;  // Disable the comparator and put ALERT/RDY in high state (default)

    private static final int BIT_SHIFT = 4;
    private static final long conversionDelay = TimeUnit.MILLISECONDS.toMillis(1);

    private final I2cDevice i2cBus;
    private final Gain gain;

    Ads1015(I2cDevice i2cDevice, Gain gain) {
        this.i2cBus = i2cDevice;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
    }

    public enum Gain {
        TWO_THIRDS(ADS1015_REG_CONFIG_PGA_6_144V),
        ONE(ADS1015_REG_CONFIG_PGA_4_096V),
        TWO(ADS1015_REG_CONFIG_PGA_2_048V),
        FOUR(ADS1015_REG_CONFIG_PGA_1_024V),
        EIGHT(ADS1015_REG_CONFIG_PGA_0_512V),
        SIXTEEN(ADS1015_REG_CONFIG_PGA_0_256V);

        final int value;

        Gain(int value) {
            this.value = value;
        }
    }

    public static class Factory {

        public Ads1015 newInstance(String i2CBus, int i2cAddress, Gain gain) {

            PeripheralManagerService service = new PeripheralManagerService();

            I2cDevice i2cDevice;
            try {
                i2cDevice = service.openI2cDevice(i2CBus, i2cAddress);
            } catch (IOException e) {
                throw new IllegalStateException("Can't open 0x48", e);
            }

            return new Ads1015(i2cDevice, gain);
        }

    }

    int readADC_SingleEnded(int channel) {
        if (channel > 3) {
            return 0;
        }

        // Start with default values
        int config = ADS1015_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
                ADS1015_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
                ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
                ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
                ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
                ADS1015_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)

        // Set PGA/voltage range
        config |= gain.value;

        // Set single-ended input channel
        switch (channel) {
            case (0):
                config |= ADS1015_REG_CONFIG_MUX_SINGLE_0;
                break;
            case (1):
                config |= ADS1015_REG_CONFIG_MUX_SINGLE_1;
                break;
            case (2):
                config |= ADS1015_REG_CONFIG_MUX_SINGLE_2;
                break;
            case (3):
                config |= ADS1015_REG_CONFIG_MUX_SINGLE_3;
                break;
        }

        // Set 'start single-conversion' bit
        config |= ADS1015_REG_CONFIG_OS_SINGLE;

        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, (short) config);

        // Wait for the conversion to complete
        delay(conversionDelay);

        // Read the conversion results
        // Shift 12-bit results right 4 bits for the ADS1015
        return readRegister(ADS1015_REG_POINTER_CONVERT) >> BIT_SHIFT;
    }

    public int readADCDifferentialBetween0And1() {
        configDifferential(ADS1015_REG_CONFIG_MUX_DIFF_0_1);
        return readADCDifferential();
    }

    public int readADCDifferentialBetween0And3() {
        configDifferential(ADS1015_REG_CONFIG_MUX_DIFF_0_3);
        return readADCDifferential();
    }

    public int readADCDifferentialBetween1And3() {
        configDifferential(ADS1015_REG_CONFIG_MUX_DIFF_1_3);
        return readADCDifferential();
    }

    public int readADCDifferentialBetween2And3() {
        configDifferential(ADS1015_REG_CONFIG_MUX_DIFF_2_3);
        return readADCDifferential();
    }

    private void configDifferential(int muxPinsConfig) {
        //noinspection PointlessBitwiseExpression Ignore for Readability
        int config = ADS1015_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
                ADS1015_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
                ADS1015_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
                ADS1015_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
                ADS1015_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
                ADS1015_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)

        // Set PGA/voltage range
        config |= gain.value;

        // Set channels
        config |= muxPinsConfig;          // AIN0 = P, AIN1 = N

        // Set 'start single-conversion' bit
        config |= ADS1015_REG_CONFIG_OS_SINGLE;

        Log.d("TUT", "b4 write config");
        // Write config register to the ADC
        writeRegister(ADS1015_REG_POINTER_CONFIG, (short) config);
        Log.d("TUT", "a4 write config");

        // Wait for the conversion to complete
        delay(conversionDelay);
    }

    private int readADCDifferential() {
        Log.d("TUT", "b4 read register");
        // Read the conversion results
        int res = readRegister(ADS1015_REG_POINTER_CONVERT) >> BIT_SHIFT;
        Log.d("TUT", "a4 read register");
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
//        Wire.beginTransmission(i2cAddress);
//        i2cwrite((uint8_t) reg);
//        i2cwrite((uint8_t) (value >> 8));
//        i2cwrite((uint8_t) (value & 0xFF));
//        Wire.endTransmission();
    }

    private void i2cwrite(byte x) {
//      #if ARDUINO >= 100
//        Wire.write((uint8_t) x);
//      #else
//        Wire.send(x);
//      #endif
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

//        Wire.beginTransmission(i2cAddress);
//        i2cwrite(ADS1015_REG_POINTER_CONVERT);
//        Wire.endTransmission();
//        Wire.requestFrom(i2cAddress, (uint8_t) 2);
//        return ((i2cread() << 8) | i2cread());
    }

//    static uint8_t i2cread(void) {
//  #if ARDUINO >= 100
//        return Wire.read();
//  #else
//        return Wire.receive();
//  #endif
//    }

}
