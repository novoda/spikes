package com.novoda.gol

import com.novoda.gol.patterns.PatternEntity
import kotlin.properties.Delegates.observable


class BoardModelImpl(width: Int, height: Int) : BoardModel {
    private var board: BoardEntity = SimulationBoardEntity(ListBasedMatrix(width = width, height = height))
    private var pattern: PatternEntity? = null

    override var onBoardChanged by observable<(BoardEntity) -> Unit>({}, { _, _, newValue ->
        newValue.invoke(board)
    })

    override fun toggleCellAt(positionEntity: PositionEntity) {
        board = board.toggleCell(positionEntity.x, positionEntity.y)
        onBoardChanged.invoke(board)
    }

    override fun selectPattern(pattern: PatternEntity) {
        if (this.pattern != null && this.pattern == pattern) {
            return
        }
        board = board.applyPattern(pattern)
        this.pattern = pattern
        onBoardChanged.invoke(board)
    }

    override fun nextIteration() {
        board = board.nextIteration()
        onBoardChanged.invoke(board)
    }
}
