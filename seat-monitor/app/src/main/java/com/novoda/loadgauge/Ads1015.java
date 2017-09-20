package com.novoda.loadgauge;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface Ads1015 {

    int ADS1015_REG_POINTER_MASK = 0x03;
    int ADS1015_REG_POINTER_CONVERT = 0x00;
    int ADS1015_REG_POINTER_CONFIG = 0x01;
    int ADS1015_REG_POINTER_LOWTHRESH = 0x02;
    int ADS1015_REG_POINTER_HITHRESH = 0x03;

    int ADS1015_REG_CONFIG_OS_MASK = 0x8000;
    int ADS1015_REG_CONFIG_OS_SINGLE = 0x8000;  // Write: Set to start a single-conversion
    int ADS1015_REG_CONFIG_OS_BUSY = 0x0000;  // Read: Bit = 0 when conversion is in progress
    int ADS1015_REG_CONFIG_OS_NOTBUSY = 0x8000;  // Read: Bit = 1 when device is not performing a conversion
    int ADS1015_REG_CONFIG_MUX_MASK = 0x7000;
    int ADS1015_REG_CONFIG_MUX_DIFF_0_1 = 0x0000;  // Differential P = AIN0, N = AIN1 (default)
    int ADS1015_REG_CONFIG_MUX_DIFF_0_3 = 0x1000;  // Differential P = AIN0, N = AIN3
    int ADS1015_REG_CONFIG_MUX_DIFF_1_3 = 0x2000;  // Differential P = AIN1, N = AIN3
    int ADS1015_REG_CONFIG_MUX_DIFF_2_3 = 0x3000;  // Differential P = AIN2, N = AIN3
    int ADS1015_REG_CONFIG_MUX_SINGLE_0 = 0x4000;  // Single-ended AIN0
    int ADS1015_REG_CONFIG_MUX_SINGLE_1 = 0x5000;  // Single-ended AIN1
    int ADS1015_REG_CONFIG_MUX_SINGLE_2 = 0x6000;  // Single-ended AIN2
    int ADS1015_REG_CONFIG_MUX_SINGLE_3 = 0x7000;  // Single-ended AIN3
    int ADS1015_REG_CONFIG_PGA_MASK = 0x0E00;
    int ADS1015_REG_CONFIG_PGA_6_144V = 0x0000;  // +/-6.144V range = Gain 2/3
    int ADS1015_REG_CONFIG_PGA_4_096V = 0x0200;  // +/-4.096V range = Gain 1
    int ADS1015_REG_CONFIG_PGA_2_048V = 0x0400;  // +/-2.048V range = Gain 2 (default)
    int ADS1015_REG_CONFIG_PGA_1_024V = 0x0600;  // +/-1.024V range = Gain 4
    int ADS1015_REG_CONFIG_PGA_0_512V = 0x0800;  // +/-0.512V range = Gain 8
    int ADS1015_REG_CONFIG_PGA_0_256V = 0x0A00;  // +/-0.256V range = Gain 16
    int ADS1015_REG_CONFIG_MODE_MASK = 0x0100;
    int ADS1015_REG_CONFIG_MODE_CONTIN = 0x0000;  // Continuous conversion mode
    int ADS1015_REG_CONFIG_MODE_SINGLE = 0x0100;  // Power-down single-shot mode (default)
    int ADS1015_REG_CONFIG_DR_MASK = 0x00E0;
    int ADS1015_REG_CONFIG_DR_128SPS = 0x0000;  // 128 samples per second
    int ADS1015_REG_CONFIG_DR_250SPS = 0x0020;  // 250 samples per second
    int ADS1015_REG_CONFIG_DR_490SPS = 0x0040;  // 490 samples per second
    int ADS1015_REG_CONFIG_DR_920SPS = 0x0060;  // 920 samples per second
    int ADS1015_REG_CONFIG_DR_1600SPS = 0x0080;  // 1600 samples per second (default)
    int ADS1015_REG_CONFIG_DR_2400SPS = 0x00A0;  // 2400 samples per second
    int ADS1015_REG_CONFIG_DR_3300SPS = 0x00C0;  // 3300 samples per second
    int ADS1015_REG_CONFIG_CMODE_MASK = 0x0010;
    int ADS1015_REG_CONFIG_CMODE_TRAD = 0x0000;  // Traditional comparator with hysteresis (default)
    int ADS1015_REG_CONFIG_CMODE_WINDOW = 0x0010;  // Window comparator
    int ADS1015_REG_CONFIG_CPOL_MASK = 0x0008;
    int ADS1015_REG_CONFIG_CPOL_ACTVLOW = 0x0000;  // ALERT/RDY pin is low when active (default)
    int ADS1015_REG_CONFIG_CPOL_ACTVHI = 0x0008;  // ALERT/RDY pin is high when active
    int ADS1015_REG_CONFIG_CLAT_MASK = 0x0004;  // Determines if ALERT/RDY pin latches once asserted
    int ADS1015_REG_CONFIG_CLAT_NONLAT = 0x0000;  // Non-latching comparator (default)
    int ADS1015_REG_CONFIG_CLAT_LATCH = 0x0004;  // Latching comparator
    int ADS1015_REG_CONFIG_CQUE_MASK = 0x0003;
    int ADS1015_REG_CONFIG_CQUE_1CONV = 0x0000;  // Assert ALERT/RDY after one conversions
    int ADS1015_REG_CONFIG_CQUE_2CONV = 0x0001;  // Assert ALERT/RDY after two conversions
    int ADS1015_REG_CONFIG_CQUE_4CONV = 0x0002;  // Assert ALERT/RDY after four conversions
    int ADS1015_REG_CONFIG_CQUE_NONE = 0x0003;  // Disable the comparator and put ALERT/RDY in high state (default)

    int BIT_SHIFT = 4;

    long CONVERSION_DELAY = TimeUnit.MILLISECONDS.toMillis(1);

    int readSingleEnded();

    int readDifferential();

    void startComparatorSingleEnded(int thresholdInMv, ComparatorCallback callback);

    void startComparatorDifferential(int thresholdInMv, ComparatorCallback callback);

    void close();

    enum Gain {
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

    enum Channel {
        ZERO(ADS1015_REG_CONFIG_MUX_SINGLE_0),
        ONE(ADS1015_REG_CONFIG_MUX_SINGLE_1),
        TWO(ADS1015_REG_CONFIG_MUX_SINGLE_2),
        THREE(ADS1015_REG_CONFIG_MUX_SINGLE_3);

        final int value;

        Channel(int value) {
            this.value = value;
        }
    }

    enum DifferentialPins {
        PINS_0_1(ADS1015_REG_CONFIG_MUX_DIFF_0_1),
        PINS_0_3(ADS1015_REG_CONFIG_MUX_DIFF_0_3),
        PINS_1_3(ADS1015_REG_CONFIG_MUX_DIFF_1_3),
        PINS_2_3(ADS1015_REG_CONFIG_MUX_DIFF_2_3);

        final int value;

        DifferentialPins(int value) {
            this.value = value;
        }
    }

    interface ComparatorCallback {
        void onThresholdHit(int valueInMv);
    }

    class Factory {

        public Ads1015 newSingleEndedReaderInstance(
            String i2CBus,
            int i2cAddress,
            Gain gain,
            Channel channel) {

            PeripheralManagerService service = new PeripheralManagerService();

            I2cDevice i2cDevice;
            try {
                i2cDevice = service.openI2cDevice(i2CBus, i2cAddress);
            } catch (IOException e) {
                throw new IllegalStateException("Can't open 0x48", e);
            }

            return new Ads1015SingleEndedReader(i2cDevice, gain, channel);
        }

        public Ads1015 newDifferentialReaderInstance(
            String i2CBus,
            int i2cAddress,
            Gain gain,
            DifferentialPins differentialPins) {

            PeripheralManagerService service = new PeripheralManagerService();

            I2cDevice i2cDevice;
            try {
                i2cDevice = service.openI2cDevice(i2CBus, i2cAddress);
            } catch (IOException e) {
                throw new IllegalStateException("Can't open 0x48", e);
            }

            return new Ads1015DifferentialReader(i2cDevice, gain, differentialPins);
        }

        public Ads1015 newDifferentialComparatorInstance(
            String i2CBus,
            int i2cAddress,
            Gain gain,
            String alertReadyGpioPinName,
            DifferentialPins differentialPins) {

            PeripheralManagerService service = new PeripheralManagerService();

            I2cDevice i2cDevice;
            Gpio alertReadyGpioBus;
            Ads1015DifferentialReader differentialReader;
            try {
                i2cDevice = service.openI2cDevice(i2CBus, i2cAddress);
                alertReadyGpioBus = service.openGpio(alertReadyGpioPinName);
                alertReadyGpioBus.setActiveType(Gpio.ACTIVE_HIGH);
                alertReadyGpioBus.setDirection(Gpio.DIRECTION_IN);
                alertReadyGpioBus.setEdgeTriggerType(Gpio.EDGE_RISING);
                differentialReader = new Ads1015DifferentialReader(i2cDevice, gain, differentialPins);
            } catch (IOException e) {
                throw new IllegalStateException("Can't open 0x48", e);
            }

            return new Ads1015DifferentialComparator(i2cDevice, alertReadyGpioBus, gain, differentialPins, differentialReader);
        }

    }
}
