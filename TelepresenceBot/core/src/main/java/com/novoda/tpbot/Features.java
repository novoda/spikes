package com.novoda.tpbot;

import java.util.Map;
import java.util.Set;

public class Features {

    private final Map<Integer, Feature> features;

    public Features(Map<Integer, Feature> features) {
        this.features = features;
    }

    public Feature get(Integer key) {
        return features.get(key);
    }

    public Set<Integer> keys() {
        return features.keySet();
    }

}
