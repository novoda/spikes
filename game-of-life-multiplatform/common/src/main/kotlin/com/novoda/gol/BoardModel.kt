package com.novoda.gol

import com.novoda.gol.patterns.PatternEntity

interface BoardModel {

    var onBoardChanged: (board: BoardEntity) -> Unit

    fun toggleCellAt(positionEntity: PositionEntity)

    fun selectPattern(pattern: PatternEntity)

    fun nextIteration()

    fun bind()
}
