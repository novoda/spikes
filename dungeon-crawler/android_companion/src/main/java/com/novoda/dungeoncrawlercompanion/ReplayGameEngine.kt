package com.novoda.dungeoncrawlercompanion

import android.os.SystemClock
import com.novoda.dungeoncrawler.GameEngine
import com.novoda.dungeoncrawler.Redux
import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.Store

private const val MIN_REDRAW_INTERVAL = 33.toLong()    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
private const val REPLAY_OVER_PAUSE = 3000.toLong()    // Pause between replay repetition - 3 seconds

class ReplayGameEngine(
        private val newReplayObserver: NewReplayObserver,
        private val gamerTagDisplayer: GamerTagDisplayer,
        private val store: Store<Redux.GameState>
) : GameEngine {

    private var gameStates: List<Redux.GameState> = emptyList()
    private var currentFrameIndex = 0

    override fun loadLevel() {
        newReplayObserver.observeForReplays(::displayReplay)
    }

    private fun displayReplay(gameStates: List<Redux.GameState>, gamerTag: String) {
        this.gameStates = gameStates
        currentFrameIndex = 0
        gamerTagDisplayer.invoke(gamerTag)
    }

    override fun loop() {
        if (gameStates.isEmpty()) {
            return
        }
        SystemClock.sleep(MIN_REDRAW_INTERVAL)

        val state = gameStates[currentFrameIndex++]
        store.dispatch(Action(NEXT_FRAME_ACTION, arrayOf(state)))

        if (currentFrameIndex >= gameStates.size) {
            currentFrameIndex = 0
            SystemClock.sleep(REPLAY_OVER_PAUSE)
        }
    }

}
