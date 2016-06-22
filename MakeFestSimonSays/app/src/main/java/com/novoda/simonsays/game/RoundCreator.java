package com.novoda.simonsays.game;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.KeyEvent.*;

class RoundCreator {

    private static final int[] KEYS = {KEYCODE_BUTTON_Y, KEYCODE_BUTTON_X, KEYCODE_BUTTON_B, KEYCODE_BUTTON_A};
    private static final List<Integer> SEQUENCE = new ArrayList<>();

    private final Random random = new Random();

    private final List<View> views;

    RoundCreator(List<View> views) {
        this.views = views;
    }

    public Round createRound(int roundNumber) {
        incrementSequence(roundNumber);
        return resumeRound(roundNumber);
    }

    public Round resumeRound(int roundNumber) {
        List<View> orderedViews = new ArrayList<>();
        List<Integer> orderedKeys = new ArrayList<>();
        for (Integer position : SEQUENCE) {
            orderedViews.add(this.views.get(position));
            orderedKeys.add(KEYS[position]);
        }
        Round.ViewSequence viewSequence = new Round.ViewSequence(orderedViews);
        Round.KeySequence keySequence = new Round.KeySequence(orderedKeys);
        return new Round(roundNumber, viewSequence, keySequence, getSpeedInMillis(roundNumber));
    }

    private void incrementSequence(int roundNumber) {
        if (roundNumber == 1) {
            SEQUENCE.clear();
        }
        int randomPosition = random.nextInt(this.views.size());
        SEQUENCE.add(randomPosition);
    }

    private long getSpeedInMillis(int roundNumber) {
        return (long) (3000 / roundNumber);
    }
}
