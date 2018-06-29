package com.novoda.dungeoncrawlercompanion

import com.novoda.dungeoncrawler.Redux
import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.Reducer

const val NEXT_FRAME_ACTION = "NextFrame"

class ReplayReducer : Reducer<Redux.GameState> {
    override fun reduce(state: Redux.GameState?, action: Action?): Redux.GameState? {
        return when (action?.type) {
            NEXT_FRAME_ACTION -> action.values[0] as Redux.GameState
            else -> state
        }
    }
}
