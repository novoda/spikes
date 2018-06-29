package com.novoda.dungeoncrawlercompanion

import android.os.SystemClock
import com.novoda.dungeoncrawler.GameEngine
import com.novoda.dungeoncrawler.Redux
import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.Store

private const val MIN_REDRAW_INTERVAL = 33.toLong()    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

class ReplayGameEngine(private val replayFetcher: ReplayFetcher, private val store: Store<Redux.GameState>) : GameEngine {

    private var gameStates: List<Redux.GameState> = emptyList()

    override fun loadLevel() {
        replayFetcher.fetchRandomReplay { gameStates -> this.gameStates = gameStates }
    }

    override fun loop() {
        if (gameStates.isEmpty()) {
            return
        }
        SystemClock.sleep(MIN_REDRAW_INTERVAL)

        val state = gameStates.first()
        gameStates = gameStates.drop(1)
        store.dispatch(Action(NEXT_FRAME_ACTION, arrayOf(state)))
    }

}
