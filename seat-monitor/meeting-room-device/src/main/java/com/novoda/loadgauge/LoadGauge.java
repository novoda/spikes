package com.novoda.loadgauge;

import com.blundell.adc.Ads1015;

public class LoadGauge {

    private final Ads1015 ads1015;
    private final Ads1015.Channel channel;

    private int milliVoltsAtRest;

    public LoadGauge(Ads1015 ads1015, Ads1015.Channel channel) {
        this.ads1015 = ads1015;
        this.channel = channel;
    }

    void calibrateToZero() {
        // Get the current load on the sensor store this value as "0"
        milliVoltsAtRest = ads1015.read(channel);
    }

    int readWeight() {
        return ads1015.read(channel) - milliVoltsAtRest;
    }

}
