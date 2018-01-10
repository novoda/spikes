package com.novoda.gol

import com.novoda.gol.patterns.PatternEntity


class BoardModelImpl(width: Int, height: Int) : BoardModel {
    private var board: BoardEntity
    private var pattern: PatternEntity? = null
    override var onBoardChanged: (board: BoardEntity) -> Unit = {}

    init {
        val cellMatrix = ListBasedMatrix(width = width, height = height)
        board = SimulationBoardEntity(cellMatrix)
    }

    override fun bind() {
        onBoardChanged.invoke(board)
    }

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
