package com.novoda.dungeoncrawler;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Thanks to https://github.com/jrowberg/i2cdevlib/blob/master/Arduino/MPU6050/MPU6050.h
 */
class MPU6050 {

    private static final int MPU6050_CLOCK_PLL_XGYRO = 0x01;
    private static final int MPU6050_GYRO_FS_250 = 0x00;
    private static final int MPU6050_ACCEL_FS_2 = 0x00;
    private static final int MPU6050_RA_PWR_MGMT_1 = 0x6B;
    private static final int MPU6050_PWR1_CLKSEL_BIT = 2;
    private static final int MPU6050_PWR1_CLKSEL_LENGTH = 3;
    private static final int MPU6050_RA_GYRO_CONFIG = 0x1B;
    private static final int MPU6050_GCONFIG_FS_SEL_BIT = 4;
    private static final int MPU6050_GCONFIG_FS_SEL_LENGTH = 2;
    private static final int MPU6050_RA_ACCEL_CONFIG = 0x1C;
    private static final int MPU6050_ACONFIG_AFS_SEL_BIT = 4;
    private static final int MPU6050_ACONFIG_AFS_SEL_LENGTH = 2;
    private static final int MPU6050_PWR1_SLEEP_BIT = 6;
    private static final int MPU6050_RA_ACCEL_XOUT_H = 0x3B;

    private final I2cDevice i2cDevice;

    static MPU6050 create(PeripheralManager peripheralManager) {
        try {
            I2cDevice mpu6050 = peripheralManager.openI2cDevice("I2C1", 0x68);

            return new MPU6050(mpu6050);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open i2c device on address 0x68", e);
        }
    }

    MPU6050(I2cDevice i2cDevice) {
        this.i2cDevice = i2cDevice;
    }

    /**
     * Power on and prepare for general usage.
     * This will activate the device and take it out of sleep mode (which must be done
     * after start-up). This function also sets both the accelerometer and the gyroscope
     * to their most sensitive settings, namely +/- 2g and +/- 250 degrees/sec, and sets
     * the clock source to use the X Gyro for reference, which is slightly better than
     * the default internal clock source.
     */
    void initialize() {
        setClockSource(MPU6050_CLOCK_PLL_XGYRO);
        setFullScaleGyroRange(MPU6050_GYRO_FS_250);
        setFullScaleAccelRange(MPU6050_ACCEL_FS_2);
        setSleepEnabled(false);
    }

    /**
     * Set clock source setting.
     * An internal 8MHz oscillator, gyroscope based clock, or external sources can
     * be selected as the MPU-60X0 clock source. When the internal 8 MHz oscillator
     * or an external source is chosen as the clock source, the MPU-60X0 can operate
     * in low power modes with the gyroscopes disabled.
     * <p>
     * Upon power up, the MPU-60X0 clock source defaults to the internal oscillator.
     * However, it is highly recommended that the device be configured to use one of
     * the gyroscopes (or an external clock source) as the clock reference for
     * improved stability. The clock source can be selected according to the following table:
     * <p>
     * <pre>
     * CLK_SEL | Clock Source
     * --------+--------------------------------------
     * 0       | Internal oscillator
     * 1       | PLL with X Gyro reference
     * 2       | PLL with Y Gyro reference
     * 3       | PLL with Z Gyro reference
     * 4       | PLL with external 32.768kHz reference
     * 5       | PLL with external 19.2MHz reference
     * 6       | Reserved
     * 7       | Stops the clock and keeps the timing generator in reset
     * </pre>
     *
     * @param source New clock source setting
     * @see #MPU6050_RA_PWR_MGMT_1
     * @see #MPU6050_PWR1_CLKSEL_BIT
     * @see #MPU6050_PWR1_CLKSEL_LENGTH
     */
    private void setClockSource(int source) {
        try {
            I2Cdev.writeBits(i2cDevice, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_CLKSEL_BIT, MPU6050_PWR1_CLKSEL_LENGTH, source);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set clock source", e);
        }
    }

    /**
     * Set full-scale gyroscope range.
     *
     * @param range New full-scale gyroscope range value
     * @see #MPU6050_GYRO_FS_250
     * @see #MPU6050_RA_GYRO_CONFIG
     * @see #MPU6050_GCONFIG_FS_SEL_BIT
     * @see #MPU6050_GCONFIG_FS_SEL_LENGTH
     */
    private void setFullScaleGyroRange(int range) {
        try {
            I2Cdev.writeBits(i2cDevice, MPU6050_RA_GYRO_CONFIG, MPU6050_GCONFIG_FS_SEL_BIT, MPU6050_GCONFIG_FS_SEL_LENGTH, range);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set gyro range", e);
        }
    }

    /**
     * Set full-scale accelerometer range.
     *
     * @param range New full-scale accelerometer range setting
     */
    private void setFullScaleAccelRange(int range) {
        try {
            I2Cdev.writeBits(i2cDevice, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_AFS_SEL_BIT, MPU6050_ACONFIG_AFS_SEL_LENGTH, range);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set accelerometer range", e);
        }
    }

    /**
     * Set sleep mode status.
     *
     * @param enabled New sleep mode enabled status
     * @see #MPU6050_RA_PWR_MGMT_1
     * @see #MPU6050_PWR1_SLEEP_BIT
     */
    private void setSleepEnabled(boolean enabled) {
        try {
            I2Cdev.writeBit(i2cDevice, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_SLEEP_BIT, enabled);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set sleep enabled", e);
        }
    }

    /**
     * Get raw 6-axis motion sensor readings (accel/gyro).
     * Retrieves all currently available motion sensor values.
     *
     * @see #MPU6050_RA_ACCEL_XOUT_H
     */
    Motion getMotion6() {
        byte[] buffer = new byte[14];
        try {
            i2cDevice.readRegBuffer(MPU6050_RA_ACCEL_XOUT_H, buffer, 14);

            Motion motion = new Motion();
            motion.ax = ((buffer[0]) << 8) | buffer[1];
            motion.ay = ((buffer[2]) << 8) | buffer[3];
            motion.az = ((buffer[4]) << 8) | buffer[5];
            motion.gx = ((buffer[8]) << 8) | buffer[9];
            motion.gy = ((buffer[10]) << 8) | buffer[11];
            motion.gz = ((buffer[12]) << 8) | buffer[13];
            return motion; // TODO check above
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the accel/gyro readings.", e);
        }
    }

    // TODO XXX close the i2c connection
    void close() {
        try {
            i2cDevice.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    class Motion {
        int ax;
        int ay;
        int az;
        int gx;
        int gy;
        int gz;

        @Override
        public String toString() {
            return "Motion{" +
                    "ax=" + ax +
                    ", ay=" + ay +
                    ", az=" + az +
                    ", gx=" + gx +
                    ", gy=" + gy +
                    ", gz=" + gz +
                    '}';
        }
    }

    static class I2Cdev {

        static void writeBit(I2cDevice device, int regAddr, int bitStart, boolean data) throws IOException {
            writeBits(device, regAddr, bitStart, 1, data ? 1 : 0);
        }

        /**
         * Write multiple bits in an 8-bit device register.
         *
         * @param device   I2C slave device
         * @param regAddr  Register regAddr to write to
         * @param bitStart First bit position to write (0-7)
         * @param length   Number of bits to write (not more than 8)
         * @param data     Right-aligned value to write
         */
        static void writeBits(I2cDevice device, int regAddr, int bitStart, int length, int data) throws IOException {
            //      010 value to write
            // 76543210 bit numbers
            //    xxx   args: bitStart=4, length=3
            // 00011100 mask byte
            // 10101111 original value (sample)
            // 10100011 original & ~mask
            // 10101011 masked | value
            int b = device.readRegByte(regAddr);
            int mask = ((1 << length) - 1) << (bitStart - length + 1);
            data <<= (bitStart - length + 1); // shift data into correct position
            data &= mask; // zero all non-important bits in data
            b &= ~(mask); // zero all important bits in existing byte
            b |= data; // combine data with existing byte
            device.writeRegByte(regAddr, (byte) b); // TODO check cast
        }

    }
}
