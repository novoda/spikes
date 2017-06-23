package com.novoda.pianohero;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

class GameTimer {

    private static final long TIMER_UPDATE_FREQUENCY_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private final long gameDurationMillis;

    @Nullable
    private CountDownTimer countDownTimer;
    private boolean gameInProgress;

    GameTimer(long gameDurationMillis) {
        this.gameDurationMillis = gameDurationMillis;
    }

    public void start(final Callback callback) {
        stop();

        gameInProgress = true;
        countDownTimer = new CountDownTimer(gameDurationMillis, TIMER_UPDATE_FREQUENCY_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                callback.onSecondTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gameInProgress = false;
                callback.onFinish();
            }
        };
        countDownTimer.start();
    }

    public void stop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        gameInProgress = false;
    }

    boolean gameHasEnded() {
        return countDownTimer == null || !gameInProgress;
    }

    public interface Callback {

        void onSecondTick(long millisUntilFinished);

        void onFinish();
    }
}
