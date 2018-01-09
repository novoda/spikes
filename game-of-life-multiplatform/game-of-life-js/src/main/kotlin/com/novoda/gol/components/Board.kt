@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.BoardEntity
import com.novoda.gol.ListBasedMatrix
import com.novoda.gol.SimulationBoardEntity
import com.novoda.gol.patterns.PatternEntity
import kotlinext.js.js
import kotlinx.html.style
import react.*
import react.dom.div


class Board(boardProps: BoardProps) : RComponent<BoardProps, BoardState>(boardProps) {

    override fun BoardState.init(props: BoardProps) {
        val cellMatrix = ListBasedMatrix(width = 50, height = 50)
        boardEntity = SimulationBoardEntity(cellMatrix)

        if (props.selectedPattern != null) {
            boardEntity = boardEntity.applyPattern(selectedPattern!!)
            state.selectedPattern = props.selectedPattern
        }
    }

    override fun componentWillReceiveProps(nextProps: BoardProps) {
        if (nextProps.isIdle.not()) {
            setState {
                boardEntity = boardEntity.nextIteration()
            }
        } else {

            if (hasSelectedNewPattern(nextProps)) {
                setState {
                    selectedPattern = nextProps.selectedPattern
                    boardEntity = boardEntity.applyPattern(nextProps.selectedPattern!!)
                }
            }
        }
    }

    private fun hasSelectedNewPattern(nextProps: BoardProps) =
            nextProps.selectedPattern != null && nextProps.selectedPattern != state.selectedPattern

    override fun RBuilder.render() = renderBoard()

    private fun RBuilder.renderBoard(): ReactElement {
        return div {
            for (y in 0 until state.boardEntity.getHeight()) {
                div {
                    attrs.style = js {
                        display = "flex"
                    }

                    for (x in 0 until state.boardEntity.getWidth()) {
                        cell(state.boardEntity.cellAtPosition(x, y), {

                            if (props.isIdle) {
                                setState {
                                    boardEntity = boardEntity.toggleCell(x, y)
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

data class BoardProps(var isIdle: Boolean, var selectedPattern: PatternEntity? = null) : RProps

interface BoardState : RState {
    var boardEntity: BoardEntity
    var selectedPattern: PatternEntity?
}

fun RBuilder.board(isIdle: Boolean, selectedPattern: PatternEntity? = null) = child(
        Board::class) {
    attrs.isIdle = isIdle
    attrs.selectedPattern = selectedPattern
}


