package com.novoda.gol.presentation

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardPresenter
import com.novoda.gol.presentation.board.BoardView
import com.novoda.gol.presentation.board.BoardViewInput
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class DesktopBoardView : View(), BoardView {

    private val rectangles = mutableMapOf<Pair<Int, Int>, Rectangle>()

    override val root: Parent = gridpane {
        for (y in 0 until 20) {
            row {
                for (x in 0 until 20) {
                    makeCell(y, x)
                }
            }
        }
    }

    private fun Pane.makeCell(y: Int, x: Int) {
        rectangle {
            rectangles[y to x] = this
            fill = Color.GRAY
            width = 20.0
            height = 20.0
            arcHeight = 4.0
            arcWidth = 4.0
            gridpaneConstraints {
                marginRight = if (x == 19) 0.0 else 2.0
                marginBottom = if (y == 19) 0.0 else 2.0
            }
            onMouseClicked = EventHandler<MouseEvent> {
                onCellClicked(PositionEntity(x, y))
            }
        }
    }

    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}
    override var onCellClicked: (position: PositionEntity) -> Unit = {}
    override var onStartSimulationClicked: () -> Unit = {}
    override var onStopSimulationClicked: () -> Unit = {}

    override fun renderBoard(boardEntity: BoardEntity) {
        for (y in 0 until boardEntity.getHeight()) {
            for (x in 0 until boardEntity.getWidth()) {
                val cellAtPosition = boardEntity.cellAtPosition(x, y)
                val rect = rectangles[y to x]
                runLater {
                    rect?.fill = if (cellAtPosition.isAlive) Color.BLUE
                    else Color.GRAY
                }
            }
        }
    }
}
