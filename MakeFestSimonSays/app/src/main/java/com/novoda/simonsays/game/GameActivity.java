package com.novoda.simonsays.game;

import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
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
        TextView scoreWidget = Views.findById(this, R.id.game_label_score);

        View gameButtonA = Views.findById(this, R.id.game_button_a);
        View gameButtonB = Views.findById(this, R.id.game_button_b);
        View gameButtonC = Views.findById(this, R.id.game_button_c);
        View gameButtonD = Views.findById(this, R.id.game_button_d);

        final List<View> views = new ArrayList<>();
        views.add(gameButtonA);
        views.add(gameButtonB);
        views.add(gameButtonC);
        views.add(gameButtonD);

        int roundNumber = getIntent().getIntExtra(EXTRA_ROUND_NUMBER, 1);

        RoundCreator roundCreator = new RoundCreator(views);
        if (savedInstanceState == null) {
            currentRound = roundCreator.createRound(roundNumber);
        } else {
            currentRound = roundCreator.resumeRound(roundNumber);
        }

        state = State.PAUSED;
        gameHandler = new Handler(getMainLooper());
        sequenceShower = new SequenceShower(
                gameHandler,
                currentRound.getViewSequence(),
                currentRound.getSpeed()
        );
        gameHandler.postDelayed(sequenceShower, 500);
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
            Views.findById(GameActivity.this, R.id.game_label_instructions).setVisibility(View.INVISIBLE);
            state = State.PLAYING_SEQUENCE;
            if (viewSequence.isComplete()) {
                state = State.AWAITING_PLAYER;
                playerInput.clear();
                promptForInput();
                return;
            }
            final View view = viewSequence.next();
            final long animationSpeed = roundSpeed / 2;
            view.animate().alpha(1).setDuration(animationSpeed).setStartDelay(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.animate().alpha(0.33f).setDuration(animationSpeed / 2).setStartDelay(animationSpeed / 2).start();
                }
            }).start();
            gameHandler.postDelayed(this, roundSpeed);
        }
    }

    private void promptForInput() {
        UsbManager systemService = (UsbManager) getSystemService(USB_SERVICE);
        if (systemService.getDeviceList().isEmpty()) {
            AndroidUtils.toggleKeyboard(GameActivity.this);
        } else {
            toastDisplayer.display("Simon Says!");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyUp(keyCode, event);
        }
        if (state != State.AWAITING_PLAYER) {
            return true;
        }

        highlightKeyPressed(keyCode);

        playerInput.add(keyCode);
        checkForSequence();

        return true;
    }

    private void highlightKeyPressed(int keyCode) {
        if (currentRound.getKeySequence().contains(keyCode)) {
            int index = currentRound.getKeySequence().indexOf(keyCode);
            final View sequenceCurrentView = currentRound.getViewSequence().get(index);
            sequenceCurrentView.animate().alpha(1).setDuration(20).setStartDelay(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    sequenceCurrentView.animate().alpha(0.33f).setDuration(50).setStartDelay(10).start();
                }
            }).start();
        }
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
                toastDisplayer.display(getString(R.string.aw_yes, KeyEvent.keyCodeToString(inputtedKey)));

                if (playerInput.size() == currentRoundKeySequence.size()) {
                    // whole sequence is correct
                    state = State.PAUSED;
                    toastDisplayer.display(getString(R.string.moving_to_next_level));
                    continueToNextLevel();
                    return;
                }

            } else {
                state = State.PAUSED;
                // Game over
                toastDisplayer.displayLong(getString(R.string.incorrect_key, KeyEvent.keyCodeToString(inputtedKey)));
                continueToHighscores();
                return;
            }
        }
    }

    private void continueToNextLevel() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_ROUND_NUMBER, currentRound.getNextLevel());
        gameHandler.removeCallbacks(sequenceShower);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void continueToHighscores() {
        Intent intent = new Intent(this, HighscoresActivity.class);
        intent.putExtra(HighscoresActivity.EXTRA_SCORE, currentRound.getScore());
        gameHandler.removeCallbacks(sequenceShower);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHandler.removeCallbacks(sequenceShower);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameHandler.removeCallbacks(sequenceShower);
    }
}
