package com.novoda.gol.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.novoda.gol.data.CellMatrix

class CellMatrixView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var cellMatrix: CellMatrix? = null

    private val deadCell = Paint().apply {
        color = Color.GRAY
    }

    private val aliveCell = Paint().apply {
        color = Color.BLACK
    }

    fun render(cellMatrix: CellMatrix) {
        this.cellMatrix = cellMatrix
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellMatrix = cellMatrix ?: return
        val cellDimen = width.toFloat() / cellMatrix.getWidth()

        for (y in 0 until cellMatrix.getHeight()) {
            for (x in 0 until cellMatrix.getWidth()) {
                val cellAtPosition = cellMatrix.cellAtPosition(x, y)
                val paint = if (cellAtPosition.isAlive) aliveCell else deadCell

                canvas.drawRect(
                        x * cellDimen,
                        y * cellDimen,
                        (x + 1) * cellDimen,
                        (y + 1) * cellDimen,
                        paint)
            }
        }
    }
}
