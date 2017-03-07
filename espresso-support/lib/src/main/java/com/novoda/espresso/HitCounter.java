package com.novoda.espresso;

import java.util.HashMap;
import java.util.Map;

public class HitCounter {

    private final Map<CharSequence, Boolean> hits = new HashMap<>();

    public void markHit(CharSequence key) {
        hits.put(key, true);
    }

    public void assertHit(CharSequence key) {
        if (!hit(key)) {
            throw new AssertionError("Wanted hit, but no hits found: " + key);
        }
    }

    public void assertNoHit(CharSequence key) {
        if (hit(key)) {
            throw new AssertionError("Wanted no hit, but got one: " + key);
        }
    }

    private boolean hit(CharSequence key) {
        if (hits.containsKey(key)) {
            return hits.get(key);
        }
        return false;
    }

}