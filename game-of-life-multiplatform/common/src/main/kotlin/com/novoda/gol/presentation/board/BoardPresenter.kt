package com.novoda.gol.presentation.board


class BoardPresenter(private val boardModel: BoardModel) {

    constructor(width: Int, height: Int) : this(BoardModelImpl.create(width, height))

    fun bind(boardView: BoardView) {

        boardModel.onBoardChanged = { board ->
            boardView.renderBoard(board)
        }

        boardView.onCellClicked = { position ->
            boardModel.toggleCellAt(position)
        }

        boardView.onPatternSelected = { pattern ->
            boardModel.selectPattern(pattern)
        }

        boardView.onStartSimulationClicked = {
            boardModel.startSimulation()
        }

        boardView.onStopSimulationClicked = {
            boardModel.stopSimulation()
        }
    }

    fun unbind(boardView: BoardView) {
        boardModel.onBoardChanged = { }
        boardView.onCellClicked = { }
        boardView.onPatternSelected = {}
        boardView.onStartSimulationClicked = {}
        boardView.onStopSimulationClicked = {}
    }
}
