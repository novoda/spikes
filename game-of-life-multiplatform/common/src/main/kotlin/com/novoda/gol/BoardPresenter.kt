package com.novoda.gol


class BoardPresenter(width: Int, height: Int) {

    private val boardModel = BoardModelImpl(width, height)

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

        boardModel.bind()
    }

    fun unbind(boardView: BoardView) {
        boardModel.onBoardChanged = { }
        boardView.onCellClicked = { }
        boardView.onTick = {}
        boardView.onPatternSelected = {}
    }

}
