package com.novoda.gol

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.novoda.gol.patterns.Glider
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.*

class GameOfLife : AppCompatActivity(), AppView {
    override var onControlButtonClicked = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.text = controlButtonLabel
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        //pattern selection ignored in the first iteration
    }

    override fun renderBoard(boardViewState: BoardViewState) {
        if (boardViewState.selectedPattern != null) {
            boardView.onPatternSelected(boardViewState.selectedPattern!!)
        }

        if (boardViewState.isIdle.not()) {
            boardView.onStartSimulationClicked()
        } else {
            boardView.onStopSimulationClicked()
        }
    }

    private val appPresenter = AppPresenter()
    private val boardPresenter = BoardPresenter(50, 50)
    private lateinit var boardView: BoardView
    private lateinit var controlButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_of_life)

        controlButton = findViewById(R.id.controlButton)
        boardView = findViewById<AndroidBoardView>(R.id.boardView)

        controlButton.setOnClickListener {
            onPatternSelected(Glider.create())
            onControlButtonClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        appPresenter.bind(this)
        boardPresenter.bind(boardView)
    }

    override fun onStop() {
        super.onStop()
        appPresenter.unbind(this)
        boardPresenter.unbind(boardView)
    }
}
