package com.novoda.gol.presentation

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardPresenter
import com.novoda.gol.presentation.board.BoardView
import com.novoda.gol.presentation.board.BoardViewInput

class BoardTerminalView : BoardView {

    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}
    override var onCellClicked: (position: PositionEntity) -> Unit = {}
    override var onStartSimulationClicked: () -> Unit = {}
    override var onStopSimulationClicked: () -> Unit = {}

    private var presenter = BoardPresenter(width = 20, height = 20)

    fun onCreate(boardViewInput: BoardViewInput) {
        presenter.bind(this)
        if (boardViewInput.selectedPattern != null) {
            onPatternSelected(boardViewInput.selectedPattern!!)
            onStartSimulationClicked()
        }
    }

    override fun renderBoard(boardEntity: BoardEntity) {
        for (y in 0 until boardEntity.getHeight()) {
            for (x in 0 until boardEntity.getWidth()) {
                val cellAtPosition = boardEntity.cellAtPosition(x, y)
                if (cellAtPosition.isAlive) {
                    print("X")
                } else {
                    print(" ")
                }

            }
            print("\n")
        }
    }
}
