package com.novoda.simonsaysandroidthings.game;

import android.util.Log;

import com.novoda.simonsaysandroidthings.game.InputVerifier.InputVerificationListener;
import com.novoda.simonsaysandroidthings.game.Sequencer.OnSequenceFinishedListener;
import com.novoda.simonsaysandroidthings.hw.io.Buzzer;
import com.novoda.simonsaysandroidthings.hw.io.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimonSays implements AutoCloseable {

    private static final String TAG = "SimonSays";

    private static final int GROUP_ON_DURATION_MS = 500;
    private static final int GROUP_OFF_DURATION_MS = 200;

    private enum State { IDLE, PLAYING}

    private final List<Group> groups;
    private final Highscore highscore;
    private final Buzzer buzzer;
    private final Sequencer sequencer;
    private final InputVerifier inputVerifier;
    private final List<Group> sequence;
    private final Random random;

    private int round;
    private State state = State.IDLE;

    public SimonSays(List<Group> groups, Highscore highscore, Buzzer buzzer) {
        this.groups = Collections.unmodifiableList(groups);
        this.highscore = highscore;
        this.buzzer = buzzer;
        sequence = new ArrayList<>();
        random = new Random();
        sequencer = new Sequencer();
        inputVerifier = new InputVerifier(groups);
    }

    public void start() {
        state = State.PLAYING;
        round = 0;
        sequence.clear();
        nextRound();
    }

    private void nextRound() {
        round++;
        expandSequence();
        playSequence();
    }

    private boolean expandSequence() {
        return sequence.add(groups.get(random.nextInt(groups.size())));
    }

    private void playSequence() {
        sequencer.playRoundSequence(sequence, GROUP_ON_DURATION_MS, GROUP_OFF_DURATION_MS, onRoundSequenceFinishedListener);
    }

    private final OnSequenceFinishedListener onRoundSequenceFinishedListener = new OnSequenceFinishedListener() {
        @Override
        public void onSequenceFinished() {
            Log.d(TAG, "onSequenceFinished() called");
            inputVerifier.listenFor(sequence, inputVerificationListener);
        }
    };

    private final OnSequenceFinishedListener onSuccessSequenceFinishedListener = new OnSequenceFinishedListener() {
        @Override
        public void onSequenceFinished() {
            Log.d(TAG, "onSequenceFinished() called");
            nextRound();
        }
    };

    private final OnSequenceFinishedListener onFailureSequenceFinishedListener = new OnSequenceFinishedListener() {
        @Override
        public void onSequenceFinished() {
            Log.d(TAG, "onSequenceFinished() called");
            int score = round - 1;
            if (score > highscore.current()) {
                highscore.setNew(score);
                sequencer.playHighscoreSequence(groups, onHighscoreSequenceFinishedListener);
            } else {
                stop();
            }
        }
    };

    private final OnSequenceFinishedListener onHighscoreSequenceFinishedListener = new OnSequenceFinishedListener() {
        @Override
        public void onSequenceFinished() {
            Log.d(TAG, "onSequenceFinished() called");
            stop();
        }
    };

    private final InputVerificationListener inputVerificationListener = new InputVerificationListener() {
        @Override
        public void onCorrectInput() {
            Log.d(TAG, "onCorrectInput() called");
            sequencer.playSuccessSequence(groups, onSuccessSequenceFinishedListener);
        }

        @Override
        public void onWrongInput() {
            Log.d(TAG, "onWrongInput() called");
            sequencer.playFailureSequence(groups, onFailureSequenceFinishedListener);
        }
    };

    @Override
    public void close() throws Exception {
        for(Group group : groups) {
            group.close();
        }
        buzzer.close();
    }

    public void toggle() {
        Log.d(TAG, "toggle() called");
        if (state == State.IDLE) {
            start();
        } else {
            stop();
        }
    }

    private void stop() {
        state = State.IDLE;
        sequencer.stop();
        for (Group group : groups) {
            group.stop();
        }
        inputVerifier.stop();
    }

}
