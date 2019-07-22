package com.novoda.gol.presentation.app

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardViewInput
import kotlin.properties.Delegates.observable

class AppModel {

    private var boardViewState by observable(BoardViewInput(true)) { _, _, newValue ->
        onBoardStateChanged(newValue)
    }

    var onSimulationStateChanged: (isIdle: Boolean) -> Unit by observable<(Boolean) -> Unit>({}) { _, _, newValue ->
        newValue(boardViewState.isIdle)
    }

    var onBoardStateChanged: (BoardViewInput) -> Unit by observable<(BoardViewInput) -> Unit>({}) { _, _, newValue ->
        newValue(boardViewState)
    }

    fun toggleSimulation() {
        boardViewState = BoardViewInput(isIdle = boardViewState.isIdle.not())
        onSimulationStateChanged(boardViewState.isIdle)
    }

    fun selectPattern(pattern: PatternEntity) {
        if (boardViewState.isIdle.not()) {
            return
        }
        boardViewState = boardViewState.copy(selectedPattern = pattern)
    }
}
