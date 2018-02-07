package com.novoda.gol.presentation.board

import com.novoda.gol.GameLoop
import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import kotlin.properties.Delegates.observable


class BoardModelImpl(initialBoard: BoardEntity, private val gameLoop: GameLoop) : BoardModel {

    private var board: BoardEntity by observable(initialBoard) { _, _, newValue ->
        onBoardChanged.invoke(newValue)
    }
    private var pattern: PatternEntity? = null

    override var onBoardChanged by observable<(BoardEntity) -> Unit>({}, { _, _, newValue ->
        newValue.invoke(board)
    })

    init {
        gameLoop.onTick = {
            board = board.nextIteration()
        }
    }

    override fun toggleCellAt(positionEntity: PositionEntity) {
        if (gameLoop.isLooping()) {
            return
        }
        board = board.toggleCell(positionEntity.x, positionEntity.y)
    }

    override fun selectPattern(pattern: PatternEntity) {
        if (gameLoop.isLooping()) {
            return
        }
        this.pattern = pattern
        board = board.applyPattern(pattern)
    }

    override fun startSimulation() {
        if (gameLoop.isLooping()) {
            return
        }
        gameLoop.startWith(300)
    }

    override fun stopSimulation() {
        if (gameLoop.isLooping().not()) {
            return
        }
        gameLoop.stop()
    }

}
