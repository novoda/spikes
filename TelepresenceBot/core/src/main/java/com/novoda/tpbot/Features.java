package com.novoda.tpbot;

import java.util.HashMap;

public class Features {

    private final HashMap<Integer, Feature> features;

    public Features(HashMap<Integer, Feature> features) {
        this.features = features;
    }

    Feature get(Integer key) {
        return features.get(key);
    }

}
