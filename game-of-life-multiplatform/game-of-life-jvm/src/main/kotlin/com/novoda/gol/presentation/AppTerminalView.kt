package com.novoda.gol.presentation

import com.novoda.gol.patterns.Glider
import com.novoda.gol.patterns.PatternEntity
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
        //TODO: pattern selection ignored in first iteration
    }

    override fun renderBoard(boardViewState: BoardViewState) {
        BoardTerminalView().onCreate(boardViewState)
    }
}
