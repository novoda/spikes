package com.novoda.ads1015;

import static com.novoda.ads1015.Ads1015.*;

class ConfigBuilder {

    int comparator(Gain gain, Channel channel) {
        return ADS1015_REG_CONFIG_CQUE_1CONV  // ChannelComparator enabled and asserts on 1 match
                | ADS1015_REG_CONFIG_CLAT_LATCH  // Latching mode
                | ADS1015_REG_CONFIG_CPOL_ACTVLOW  // Alert/Rdy active low   (default val)
                | ADS1015_REG_CONFIG_CMODE_TRAD  // Traditional comparator (default val)
                | ADS1015_REG_CONFIG_DR_1600SPS  // 1600 samples per second (default)
                | ADS1015_REG_CONFIG_MODE_CONTIN   // Continuous conversion mode
                | gain.value
                | channel.value;
    }

    int reader(Gain gain, Channel channel) {
        return ADS1015_REG_CONFIG_CQUE_NONE  // Disable the comparator (default val)
                | ADS1015_REG_CONFIG_CLAT_NONLAT  // Non-latching (default val)
                | ADS1015_REG_CONFIG_CPOL_ACTVLOW  // Alert/Rdy active low   (default val)
                | ADS1015_REG_CONFIG_CMODE_TRAD  // Traditional comparator (default val)
                | ADS1015_REG_CONFIG_DR_1600SPS  // 1600 samples per second (default)
                | ADS1015_REG_CONFIG_MODE_SINGLE   // Single-shot mode (default)
                | gain.value
                | channel.value
                | ADS1015_REG_CONFIG_OS_SINGLE;
    }
}
