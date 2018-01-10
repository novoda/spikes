package com.novoda.gol

import com.novoda.gol.patterns.PatternEntity

interface BoardView {

    var onPatternSelected: (pattern: PatternEntity) -> Unit
    var onCellClicked: (position: PositionEntity) -> Unit
    var onTick: () -> Unit

    fun renderBoard(boardEntity: BoardEntity)

}
