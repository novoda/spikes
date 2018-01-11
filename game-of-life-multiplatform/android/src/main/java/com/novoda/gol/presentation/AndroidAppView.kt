package com.novoda.gol.presentation

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.novoda.gol.R
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository

class AndroidAppView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), AppView {

    override var onControlButtonClicked = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.text = controlButtonLabel
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        patternSelectionButton.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    override fun renderBoardWith(boardViewInput: BoardViewInput) {
        boardView.apply(boardViewInput)
    }

    private lateinit var controlButton: Button
    private lateinit var patternSelectionButton: Button
    private lateinit var boardView: BoardView

    private val boardPresenter = BoardPresenter(20, 20)

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).inflate(R.layout.app_view, this)

        controlButton = findViewById(R.id.controlButton)
        patternSelectionButton = findViewById(R.id.patternSelectionButton)
        boardView = findViewById<AndroidBoardView>(R.id.boardView)
        controlButton.setOnClickListener {
            onControlButtonClicked()
        }

        patternSelectionButton.setOnClickListener {
            showPatternSelection()
        }
    }

    private fun showPatternSelection() {
        val patternSelectionView = LinearLayout(context)
        val dialog = AlertDialog.Builder(context).setView(patternSelectionView).create()

        patternSelectionView.orientation = LinearLayout.VERTICAL

        for (pattern in PatternRepository.patterns()) {
            val patternNameView = TextView(context)
            patternNameView.text = pattern.getName()
            patternSelectionView.addView(patternNameView)

            val cellMatrixView = CellMatrixView(context)
            patternSelectionView.addView(cellMatrixView, pxFromDp(25f), pxFromDp(25f))
            cellMatrixView.render(pattern)
            cellMatrixView.setOnClickListener {
                onPatternSelected(pattern)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun pxFromDp(dp: Float): Int {
        return (dp * this.resources.displayMetrics.density).toInt()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        boardPresenter.bind(boardView)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        boardPresenter.unbind(boardView)
    }
}
