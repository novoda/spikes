package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

public class TimeObserver {

    public TimeObserver(Store<Redux.GameState> store,  PausableStartClock clock) {
        store.subscribe(gameState -> {
            if (gameState.stage == Stage.PAUSE) {
                clock.pause();
            } else {
                clock.resume();
            }
        });
    }

    interface PauseMonitor {
        void onPauseGame();
    }

    interface ResumeMonitor {
        void onResumeGame();
    }

}
