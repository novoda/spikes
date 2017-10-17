package com.novoda.ads1015;

import com.google.android.things.pio.I2cDevice;
import com.novoda.ads1015.Ads1015.Channel;
import com.novoda.ads1015.Ads1015.Gain;

import static com.novoda.ads1015.Ads1015.ADS1015_REG_POINTER_CONFIG;
import static com.novoda.ads1015.Ads1015.ADS1015_REG_POINTER_CONVERT;

class ChannelReader {

    private final RegisterReaderWriter registerReaderWriter;
    private final ConfigBuilder configBuilder;
    private final I2cDevice i2cBus;
    private final Gain gain;

    ChannelReader(RegisterReaderWriter registerReaderWriter,
                  ConfigBuilder configBuilder,
                  I2cDevice i2cBus,
                  Gain gain) {
        this.registerReaderWriter = registerReaderWriter;
        this.configBuilder = configBuilder;
        this.i2cBus = i2cBus;
        this.gain = gain; /* +/- 6.144V range (limited to VDD +0.3V max!) */
    }

    int read(Channel channel) {
        writeRegister(ADS1015_REG_POINTER_CONFIG, configBuilder.reader(gain, channel));
        return readRegister(ADS1015_REG_POINTER_CONVERT);
    }

    private void writeRegister(int reg, int value) {
        registerReaderWriter.writeRegister(i2cBus, reg, value);
    }

    private int readRegister(int reg) {
        return registerReaderWriter.readRegister(i2cBus, reg);
    }
}
