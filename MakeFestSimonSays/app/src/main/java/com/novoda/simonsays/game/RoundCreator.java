package com.novoda.simonsays.game;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.KeyEvent.*;

class RoundCreator {
    private static final int[] keys = {KEYCODE_1, KEYCODE_2, KEYCODE_3, KEYCODE_4};
    private static final List<Integer> sequence = new ArrayList<>();

    private final Random random = new Random();

    private final List<View> views;

    RoundCreator(List<View> views) {
        this.views = views;
    }

    public Round createRound(int roundNumber) {
        if (roundNumber == 1) {
            sequence.clear();
        }
        List<View> orderedViews = new ArrayList<>();
        List<Integer> orderedKeys = new ArrayList<>();
        for (Integer position : sequence) {
            orderedViews.add(this.views.get(position));
            orderedKeys.add(keys[position]);
        }
        int randomPosition = random.nextInt(this.views.size());
        sequence.add(randomPosition);
        orderedViews.add(this.views.get(randomPosition));
        orderedKeys.add(keys[randomPosition]);

        Round.ViewSequence viewSequence = new Round.ViewSequence(orderedViews);
        Round.KeySequence keySequence = new Round.KeySequence(orderedKeys);
        return new Round(roundNumber, viewSequence, keySequence);
    }
}
