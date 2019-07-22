package com.novoda.gol.presentation.board

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity

interface BoardModel {

    var onBoardChanged: (board: BoardEntity) -> Unit

    fun toggleCellAt(positionEntity: PositionEntity)

    fun selectPattern(pattern: PatternEntity)

    fun startSimulation()

    fun stopSimulation()
}
