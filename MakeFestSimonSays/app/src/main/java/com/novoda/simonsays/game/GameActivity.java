package com.novoda.simonsays.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.toast.ToastDisplayer;
import com.novoda.notils.logger.toast.ToastDisplayers;
import com.novoda.notils.meta.AndroidUtils;
import com.novoda.simonsays.BuildConfig;
import com.novoda.simonsays.R;
import com.novoda.simonsays.highscores.HighscoresActivity;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_ROUND_NUMBER = BuildConfig.APPLICATION_ID + "/RoundNumber";

    private final List<Integer> playerInput = new ArrayList<>();

    enum State {
        PAUSED, PLAYING_SEQUENCE, AWAITING_PLAYER;
    }

    private ToastDisplayer toastDisplayer;
    private Handler gameHandler;
    private SequenceShower sequenceShower;
    private Round currentRound;
    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        toastDisplayer = ToastDisplayers.noPendingToastsToastDisplayer(this);

        TextView instructionsWidget = Views.findById(this, R.id.game_label_instructions);
        TextView scoreWidget = Views.findById(this, R.id.game_label_score);

        Button gameButtonA = Views.findById(this, R.id.game_button_a);
        Button gameButtonB = Views.findById(this, R.id.game_button_b);
        Button gameButtonC = Views.findById(this, R.id.game_button_c);
        Button gameButtonD = Views.findById(this, R.id.game_button_d);

        List<View> views = new ArrayList<>();
        views.add(gameButtonA);
        views.add(gameButtonB);
        views.add(gameButtonC);
        views.add(gameButtonD);

        int roundNumber = getIntent().getIntExtra(EXTRA_ROUND_NUMBER, 1);

        RoundCreator roundCreator = new RoundCreator(views);
        currentRound = roundCreator.createRound(roundNumber);

        state = State.PAUSED;
        gameHandler = new Handler(getMainLooper());
        sequenceShower = new SequenceShower(
                gameHandler,
                currentRound.getViewSequence(),
                currentRound.getSpeed()
        );
        gameHandler.postDelayed(sequenceShower, 1000);
        scoreWidget.setText(getString(R.string.score_x, currentRound.getScore()));
    }

    private class SequenceShower implements Runnable {

        private final Handler gameHandler;
        private final Round.ViewSequence viewSequence;
        private final long roundSpeed;

        private SequenceShower(Handler gameHandler, Round.ViewSequence viewSequence, long roundSpeed) {
            this.gameHandler = gameHandler;
            this.viewSequence = viewSequence;
            this.roundSpeed = roundSpeed;
        }

        @Override
        public void run() {
            state = State.PLAYING_SEQUENCE;
            for (final View view : viewSequence) {
                view.setVisibility(View.INVISIBLE);
                view.setAlpha(0);
            }
            if (viewSequence.isComplete()) {
                state = State.AWAITING_PLAYER;
                playerInput.clear();
                AndroidUtils.toggleKeyboard(GameActivity.this);
                return;
            }
            final View view = viewSequence.next();
            view.animate().alpha(1).withStartAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                }
            }).start();
            gameHandler.postDelayed(this, roundSpeed);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (state != State.AWAITING_PLAYER) {
            return super.onKeyUp(keyCode, event);
        }

        playerInput.add(keyCode);

        checkForSequence();

        return super.onKeyUp(keyCode, event);
    }

    private void checkForSequence() {
        Round.KeySequence currentRoundKeySequence = currentRound.getKeySequence();
        for (int i = 0; i < currentRoundKeySequence.size(); i++) {
            if (playerInput.size() <= i) {
                return;
            }
            int expectedKey = currentRoundKeySequence.get(i);
            int inputtedKey = playerInput.get(i);
            if (expectedKey == inputtedKey) {
                // input so far is correct
                toastDisplayer
                        .display(KeyEvent.keyCodeToString(inputtedKey) + ", yes!");

                if (playerInput.size() == currentRoundKeySequence.size()) {
                    // whole sequence is correct
                    state = State.PAUSED;
                    toastDisplayer
                            .display("NICE. Moving to the next level!");

                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra(EXTRA_ROUND_NUMBER, currentRound.getNextLevel());
                    gameHandler.removeCallbacks(sequenceShower);
                    startActivity(intent);
                    finish();
                }

            } else {
                state = State.PAUSED;
                // Game over
                toastDisplayer
                        .displayLong(KeyEvent.keyCodeToString(inputtedKey) + ", is incorrect :-(");

                Intent intent = new Intent(this, HighscoresActivity.class);
                intent.putExtra(HighscoresActivity.EXTRA_SCORE, currentRound.getScore());
                gameHandler.removeCallbacks(sequenceShower);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameHandler.removeCallbacks(sequenceShower);
    }
}
