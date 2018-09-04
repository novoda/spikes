package com.novoda.gol.presentation

import com.novoda.gol.patterns.Glider
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.app.AppPresenter
import com.novoda.gol.presentation.app.AppView
import com.novoda.gol.presentation.board.BoardViewInput
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader

class AppTerminalView : AppView {

    override var onControlButtonClicked: () -> Unit = {}

    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    private val presenter = AppPresenter()

    fun onCreate() {
        presenter.bind(this)

        Observable
                .fromCallable {
                    readString()
                }
                .filter { it == "yes" }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    onPatternSelected(Glider.create())
                    onControlButtonClicked()
                }
    }

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        print("$controlButtonLabel\n")
    }

    private fun readString(): String {
        val bufferedReader = BufferedReader(InputStreamReader(System.`in`))
        return bufferedReader.readLine().toString()
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        //TODO: pattern selection is ignored in first iteration
    }

    override fun renderBoardWith(boardViewInput: BoardViewInput) {
        BoardTerminalView().onCreate(boardViewInput)
    }
}
