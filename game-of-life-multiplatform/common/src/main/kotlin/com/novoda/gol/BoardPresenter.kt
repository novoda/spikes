package com.novoda.gol


class BoardPresenter {

    private val boardModel = BoardModelImpl()

    fun bind(boardView: BoardView) {

        boardModel.onBoardChanged = { board ->
            boardView.renderBoard(board)
        }

        boardView.onCellClicked = { position ->
            boardModel.toggleCellAt(position)
        }

        boardView.onTick = {
            boardModel.nextIteration()
        }

        boardView.onPatternSelected = { pattern ->
            boardModel.selectPattern(pattern)
        }
    }

    fun unbind(boardView: BoardView) {
        boardModel.onBoardChanged = { }
        boardView.onCellClicked = { }
        boardView.onTick = {}
        boardView.onPatternSelected = {}
    }

}
