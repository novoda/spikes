package com.novoda.gol.presentation.board

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity

interface BoardView {

    var onPatternSelected: (pattern: PatternEntity) -> Unit
    var onCellClicked: (position: PositionEntity) -> Unit
    var onStartSimulationClicked: () -> Unit
    var onStopSimulationClicked: () -> Unit

    fun renderBoard(boardEntity: BoardEntity)
}

fun BoardView.apply(boardViewInput: BoardViewInput) {
    if (boardViewInput.selectedPattern != null) {
        onPatternSelected(boardViewInput.selectedPattern)
    }

    if (boardViewInput.isIdle.not()) {
        onStartSimulationClicked()
    } else {
        onStopSimulationClicked()
    }
}
