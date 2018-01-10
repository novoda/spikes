package com.novoda.gol.presentation

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
