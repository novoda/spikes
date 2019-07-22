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

    private enum State { IDLE, PLAYING}

    private final List<Group> groups;
    private final Score score;
    private final Buzzer buzzer;
    private final Sequencer sequencer;
    private final InputVerifier inputVerifier;
    private final List<Group> sequence;
    private final SequenceDuration sequenceDuration;
    private final Random random;

    private int round;
    private State state = State.IDLE;

    public SimonSays(List<Group> groups, Score score, Buzzer buzzer) {
        this.groups = Collections.unmodifiableList(groups);
        this.score = score;
        this.buzzer = buzzer;
        sequence = new ArrayList<>();
        random = new Random();
        sequencer = new Sequencer();
        sequenceDuration = new SequenceDuration();
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

    private void expandSequence() {
        sequence.add(groups.get(random.nextInt(groups.size())));
    }

    private void playSequence() {
        score.setCurrentScore(round);
        sequencer.playRoundSequence(
                sequence,
                sequenceDuration.onDuration(round),
                sequenceDuration.offDuration(round),
                onRoundSequenceFinishedListener
        );
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
            if (score > SimonSays.this.score.currentHighscore()) {
                SimonSays.this.score.setNewHighscore(score);
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
        for (Group group : groups) {
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
