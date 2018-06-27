package com.novoda.dungeoncrawlercompanion

import com.novoda.dungeoncrawler.Redux
import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.Reducer

class ReplayReducer : Reducer<Redux.GameState> {
    override fun reduce(state: Redux.GameState?, action: Action?): Redux.GameState? {
        return when (action?.type) {
            "NextFrame" -> action.values[0] as Redux.GameState
            else -> state
        }
    }
}
