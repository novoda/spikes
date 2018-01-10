@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.BoardPresenter
import com.novoda.gol.presentation.BoardView
import com.novoda.gol.presentation.BoardViewState
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
        presenter = BoardPresenter(50, 50)

        if (props.boardViewState.selectedPattern != null) {
            onPatternSelected.invoke(props.boardViewState.selectedPattern!!)
        }
    }

    override fun componentWillReceiveProps(nextProps: BoardProps) {
        val state = nextProps.boardViewState

        if (state.isIdle.not()) {
            onStartSimulationClicked()
        } else {
            onStopSimulationClicked()
        }
        if (state.selectedPattern != null) {
            onPatternSelected.invoke(state.selectedPattern!!)
        }
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

data class BoardProps(var boardViewState: BoardViewState) : RProps

interface BoardState : RState {
    var boardEntity: BoardEntity
}

fun RBuilder.board(board: BoardViewState) = child(Board::class) {
    attrs.boardViewState = board
}
