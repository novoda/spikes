package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

class TimeObserver {

    TimeObserver(Store<Redux.GameState> store, StartClock clock) {
        if (clock instanceof PausableStartClock) {
            PausableStartClock pausableClock = (PausableStartClock) clock;
            store.subscribe(gameState -> {
                if (gameState.stage == Stage.PAUSE) {
                    pausableClock.pause();
                } else {
                    pausableClock.resume();
                }
            });
        }
    }

    interface PauseMonitor {
        void pause();
    }

    interface ResumeMonitor {
        void resume();
    }

}
