package com.novoda.dungeoncrawler;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Thanks to https://github.com/jrowberg/i2cdevlib/blob/master/Arduino/MPU6050/MPU6050.h
 */
class MPU6050 {

    public static final float SENSOR_RESOLUTION = 32768;

    private static final byte MPU6050_CLOCK_PLL_INTERNAL = 0x00;
    private static final byte MPU6050_CLOCK_PLL_XGYRO = 0x01;

    private static final byte MPU6050_GYRO_FS_250 = 0x00;
    private static final byte MPU6050_GYRO_FS_500 = 0x08;
    private static final byte MPU6050_GYRO_FS_1000 = 0x10;
    private static final byte MPU6050_GYRO_FS_2000 = 0x18;

    private static final byte MPU6050_ACCEL_FS_2 = 0x00;
    private static final byte MPU6050_ACCEL_FS_4 = 0x08;
    private static final byte MPU6050_ACCEL_FS_8 = 0x10;
    private static final byte MPU6050_ACCEL_FS_16 = 0x18;

    private static final byte MPU6050_RA_PWR_MGMT_1 = 0x6B;
    private static final byte MPU6050_PWR1_CLKSEL_BIT = 2;
    private static final byte MPU6050_PWR1_CLKSEL_LENGTH = 3;
    private static final byte MPU6050_RA_GYRO_CONFIG = 0x1B;
    private static final byte MPU6050_GCONFIG_FS_SEL_BIT = 4;
    private static final byte MPU6050_GCONFIG_FS_SEL_LENGTH = 2;
    private static final byte MPU6050_RA_ACCEL_CONFIG = 0x1C;
    private static final byte MPU6050_ACONFIG_AFS_SEL_BIT = 4;
    private static final byte MPU6050_ACONFIG_AFS_SEL_LENGTH = 2;
    private static final byte MPU6050_PWR1_SLEEP_BIT = 6;

    private static final byte MPU6050_RA_ACCEL_XOUT_H = 0x3B;
    private static final byte MPU6050_RA_ACCEL_YOUT_H = 0x3D;
    private static final byte MPU6050_RA_ACCEL_ZOUT_H = 0x40;

    private static final byte MPU6050_RA_GYRO_XOUT_H = 0x43;
    private static final byte MPU6050_RA_GYRO_YOUT_H = 0x45;
    private static final byte MPU6050_RA_GYRO_ZOUT_H = 0x47;
    private static final int LOWER_BITS_MASK = 0x00ff;
    private static final int UPPER_BITS_MASK = 0xff00;

    private final Motion motion = new Motion();
    private final byte[] mBuffer = new byte[2];
    private final I2cDevice i2cDevice;

    private float gyroscopeCoef = 1;
    private float accelerometerCoef = 1;

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
        setClockSource(MPU6050_CLOCK_PLL_INTERNAL);
        setFullScaleGyroRange(MPU6050_GYRO_FS_250);
        setFullScaleAccelRange(MPU6050_ACCEL_FS_2);
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
    private void setClockSource(byte source) {
        try {
            i2cDevice.writeRegByte(MPU6050_RA_PWR_MGMT_1, source);
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
    private void setFullScaleGyroRange(byte range) {
        gyroscopeCoef = getGyroscopeCoefficient(range);
        try {
            i2cDevice.writeRegByte(MPU6050_RA_GYRO_CONFIG, range);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set gyro range", e);
        }
    }

    private float getGyroscopeCoefficient(byte range) {
        switch (range) {
            case MPU6050_GYRO_FS_250:
                return (float) 250. / SENSOR_RESOLUTION;
            case MPU6050_GYRO_FS_500:
                return (float) 500. / SENSOR_RESOLUTION;
            case MPU6050_GYRO_FS_1000:
                return (float) 1000. / SENSOR_RESOLUTION;
            case MPU6050_GYRO_FS_2000:
                return (float) 2000. / SENSOR_RESOLUTION;
            default:
                return 0;
        }
    }

    /**
     * Set full-scale accelerometer range.
     *
     * @param range New full-scale accelerometer range setting
     */
    private void setFullScaleAccelRange(byte range) {
        accelerometerCoef = getAccelerometerCoefficient(range);
        try {
            i2cDevice.writeRegByte(MPU6050_RA_ACCEL_CONFIG, range);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set accelerometer range", e);
        }
    }

    private float getAccelerometerCoefficient(byte range) {
        switch (range) {
            case MPU6050_ACCEL_FS_2:
                return (float) 2 / SENSOR_RESOLUTION;
            case MPU6050_ACCEL_FS_4:
                return (float) 4 / SENSOR_RESOLUTION;
            case MPU6050_ACCEL_FS_8:
                return (float) 8 / SENSOR_RESOLUTION;
            case MPU6050_ACCEL_FS_16:
                return (float) 16 / SENSOR_RESOLUTION;
            default:
                return 0;
        }
    }

    /**
     * Get raw 6-axis motion sensor readings (accel/gyro).
     * Retrieves all currently available motion sensor values.
     *
     * @see #MPU6050_RA_ACCEL_XOUT_H
     */
    Motion getMotion6() {
        try {
            motion.ax = (float) readRegWord(MPU6050_RA_ACCEL_XOUT_H) * accelerometerCoef;
            motion.ay = (float) readRegWord(MPU6050_RA_ACCEL_YOUT_H) * accelerometerCoef;
            motion.az = (float) readRegWord(MPU6050_RA_ACCEL_ZOUT_H) * accelerometerCoef;
            motion.gx = (float) readRegWord(MPU6050_RA_GYRO_XOUT_H) * gyroscopeCoef;
            motion.gy = (float) readRegWord(MPU6050_RA_GYRO_YOUT_H) * gyroscopeCoef;
            motion.gz = (float) readRegWord(MPU6050_RA_GYRO_ZOUT_H) * gyroscopeCoef;
            return motion;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the accel/gyro readings.", e);
        }
    }

    private short readRegWord(int i) throws IOException {
        i2cDevice.readRegBuffer(i, mBuffer, 2);
        return (short) (((mBuffer[0] << 8) & UPPER_BITS_MASK) | (mBuffer[1] & LOWER_BITS_MASK));
    }

    void close() {
        try {
            i2cDevice.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static class Motion {
        float ax;
        float ay;
        float az;
        float gx;
        float gy;
        float gz;

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

}
