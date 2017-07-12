package com.novoda.simonsaysandroidthings.game;

import java.util.Locale;

class SequenceDuration {

    private static final int GROUP_ON_DURATION_MS = 3000;
    private static final int GROUP_OFF_DURATION_MS = 1000;

    int onDuration(int round) {
        return GROUP_ON_DURATION_MS / (round + 1);
//        return (int) (GROUP_ON_DURATION_MS * Math.log(1.0 / (round + 2)));
    }

    int offDuration(int round) {
        return GROUP_OFF_DURATION_MS / (round + 1);
//        return (int) (GROUP_OFF_DURATION_MS * Math.log(1.0 / (round + 2)));
    }

    public static void main(String[] args) {
        SequenceDuration sequenceDuration = new SequenceDuration();
        for (int i = 0; i < 150; i++) {
            System.out.println(String.format(
                    Locale.getDefault(),
                    "%d. on: %d, off: %d",
                    i,
                    sequenceDuration.onDuration(i),
                    sequenceDuration.offDuration(i)
            ));
        }
    }
}
