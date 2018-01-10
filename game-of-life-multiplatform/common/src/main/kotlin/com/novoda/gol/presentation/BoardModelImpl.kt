package com.novoda.gol.presentation

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.data.SimulationBoardEntity
import com.novoda.gol.patterns.PatternEntity
import kotlin.properties.Delegates.observable


class BoardModelImpl(width: Int, height: Int) : BoardModel {
    private var board: BoardEntity by observable(SimulationBoardEntity(ListBasedMatrix(width, height)) as BoardEntity) { _, _, newValue ->
        onBoardChanged.invoke(newValue)
    }
    private var pattern: PatternEntity? = null

    override var onBoardChanged by observable<(BoardEntity) -> Unit>({}, { _, _, newValue ->
        newValue.invoke(board)
    })

    override fun toggleCellAt(positionEntity: PositionEntity) {
        board = board.toggleCell(positionEntity.x, positionEntity.y)
    }

    override fun selectPattern(pattern: PatternEntity) {
        if (this.pattern != null && this.pattern == pattern) {
            return
        }
        this.pattern = pattern
        board = board.applyPattern(pattern)
    }

    override fun nextIteration() {
        board = board.nextIteration()
    }
}
