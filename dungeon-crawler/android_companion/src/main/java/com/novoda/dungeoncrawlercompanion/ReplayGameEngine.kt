package com.novoda.dungeoncrawlercompanion

import android.os.SystemClock
import android.util.Log
import com.novoda.dungeoncrawler.GameEngine
import com.novoda.dungeoncrawler.Redux
import com.novoda.dungeoncrawler.StartClock
import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.Store

private const val MIN_REDRAW_INTERVAL = 33    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

class ReplayGameEngine(
        private val replayFetcher: ReplayFetcher,
        private val store: Store<Redux.GameState>,
        private val clock: StartClock
) : GameEngine {

//    init {
//        store.subscribe { gameState ->
//            val frameTime = clock.millis()
//            if (gameState.stage == Stage.LEVEL_COMPLETE) {
//                if (frameTime > gameState.stageStartTime + 1200) {
//                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()))
//                }
//            } else if (gameState.stage == Stage.GAME_COMPLETE) {
//                if (frameTime > gameState.stageStartTime + 5500) {
//                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()))
//                }
//            } else if (gameState.stage == Stage.DEAD) {
//                if (areAllParticlesDeactive()) {
//                    store.dispatch(Redux.GameActions.restartLevel(clock.millis()))
//                }
//            }
//        }
//    }

    override fun loadLevel() {
        replayFetcher.fetchRandomReplay { gameStates ->
            gameStates.forEach {
                SystemClock.sleep(30)
                Log.e("foo", "dispatch " + it.stage)
                store.dispatch(Action("NextFrame", arrayOf(it)))
            }
        }
    }

    override fun loop() {
//        val state = store.state
//        val frameTime = clock.millis()
//        if (frameTime - state.frameTime >= MIN_REDRAW_INTERVAL) {
//            nextJoyState()?.let {
//                store.dispatch(Action())
//            }
//        }
    }

}
