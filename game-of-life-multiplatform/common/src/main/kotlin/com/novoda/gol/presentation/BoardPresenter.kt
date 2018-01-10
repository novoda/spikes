package com.novoda.gol.presentation


class BoardPresenter(width: Int, height: Int) {

    private val boardModel = BoardModelImpl.create(width, height)

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
    }

}
