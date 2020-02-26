package com.novoda.tpbot.model;

import com.novoda.tpbot.FeaturePersistence;

import java.util.Map;
import java.util.Set;

public class Features {

    private final Map<Integer, FeaturePersistence> features;

    public Features(Map<Integer, FeaturePersistence> features) {
        this.features = features;
    }

    public FeaturePersistence get(Integer key) {
        return features.get(key);
    }

    public Set<Integer> keys() {
        return features.keySet();
    }

}
