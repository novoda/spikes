package com.novoda.tpbot;

import java.util.Map;

public class Features {

    private final Map<Integer, Feature> features;

    public Features(Map<Integer, Feature> features) {
        this.features = features;
    }

    Feature get(Integer key) {
        return features.get(key);
    }

}
