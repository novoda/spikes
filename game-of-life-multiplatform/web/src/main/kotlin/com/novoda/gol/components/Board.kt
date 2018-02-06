@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.*
import kotlinext.js.js
import kotlinx.html.style
import react.*
import react.dom.div


class Board(boardProps: BoardProps) : RComponent<BoardProps, BoardState>(boardProps), BoardView {

    override var onCellClicked: (position: PositionEntity) -> Unit = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}
    override var onStartSimulationClicked: () -> Unit = {}
    override var onStopSimulationClicked: () -> Unit = {}

    private lateinit var presenter: BoardPresenter

    override fun renderBoard(boardEntity: BoardEntity) {
        setState {
            this.boardEntity = boardEntity
        }
    }

    override fun componentWillMount() {
        presenter.bind(this)
    }

    override fun componentWillUnmount() {
        presenter.unbind(this)
    }

    override fun BoardState.init(props: BoardProps) {
        presenter = BoardPresenter(BoardModelImpl.create(50, 50))

        if (props.boardViewInput.selectedPattern != null) {
            onPatternSelected.invoke(props.boardViewInput.selectedPattern!!)
        }
    }

    override fun componentWillReceiveProps(nextProps: BoardProps) {
        apply(nextProps.boardViewInput)
    }

    override fun RBuilder.render() = renderBoard(state)

    private fun RBuilder.renderBoard(state: BoardState): ReactElement {
        return div {
            for (y in 0 until state.boardEntity.getHeight()) {
                div {
                    attrs.style = js {
                        display = "flex"
                    }

                    for (x in 0 until state.boardEntity.getWidth()) {
                        cell(state.boardEntity.cellAtPosition(x, y), {
                            onCellClicked.invoke(PositionEntity(x, y))
                        })
                    }
                }
            }
        }
    }
}

data class BoardProps(var boardViewInput: BoardViewInput) : RProps

interface BoardState : RState {
    var boardEntity: BoardEntity
}

fun RBuilder.board(board: BoardViewInput) = child(Board::class) {
    attrs.boardViewInput = board
}
