package com.novoda.pianohero;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

class GameTimer {

    private static final long GAME_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(15);
    private static final long GAME_DURATION_SECONDS = TimeUnit.MILLISECONDS.toSeconds(GAME_DURATION_MILLIS);
    private static final long TIMER_UPDATE_FREQUENCY_MILLIS = TimeUnit.SECONDS.toMillis(1);

    @Nullable
    private CountDownTimer countDownTimer;
    private boolean gameInProgress;
    private long secondsLeft = GAME_DURATION_SECONDS;

    public void start(final Callback callback) {
        gameInProgress = true;
        countDownTimer = new CountDownTimer(GAME_DURATION_MILLIS, TIMER_UPDATE_FREQUENCY_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                callback.onSecondTick(secondsLeft);
            }

            @Override
            public void onFinish() {
                gameInProgress = false;
                callback.onFinish();
            }
        };
        countDownTimer.start();
    }

    public long secondsRemaining() {
        return secondsLeft;
    }

    public void stop() {
        if (countDownTimer == null) {
            return;
        }
        gameInProgress = false;
        secondsLeft = GAME_DURATION_SECONDS;
        countDownTimer.cancel();
    }

    boolean gameHasEnded() {
        return countDownTimer == null || !gameInProgress;
    }

    public interface Callback {

        void onSecondTick(long secondsUntilFinished);

        void onFinish();
    }
}
