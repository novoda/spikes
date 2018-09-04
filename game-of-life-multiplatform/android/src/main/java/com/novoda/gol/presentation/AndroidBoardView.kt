package com.novoda.gol.presentation

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardView


class AndroidBoardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BoardView {

    override var onCellClicked: (position: PositionEntity) -> Unit = {}
    override var onStartSimulationClicked: () -> Unit = {}
    override var onStopSimulationClicked: () -> Unit = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    private val cellMatrixView = CellMatrixView(getContext())

    init {
        addView(cellMatrixView, FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        cellMatrixView.onCellClicked = { x, y ->
            onCellClicked(PositionEntity(x, y))
        }
    }

    override fun renderBoard(boardEntity: BoardEntity) {
        cellMatrixView.post {
            cellMatrixView.render(boardEntity)
        }
    }
}
