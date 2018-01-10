package com.novoda.gol.presentation

class AppPresenter {

    private val model = AppModel()

    fun bind(view: AppView) {

        model.onSimulationStateChanged = { isIdle ->
            view.controlButtonLabel = if (isIdle) "Start simulation" else "Stop Simulation"
            view.patternSelectionVisibility = isIdle
            view.board = view.board.copy(isIdle = isIdle)
        }

        view.onControlButtonClicked = {
            model.toggleSimulation()
        }

        view.onPatternSelected = { pattern ->
            view.board = view.board.copy(selectedPattern = pattern)
        }
    }

    fun unbind(view: AppView) {
        model.onSimulationStateChanged = {}
        view.onControlButtonClicked = {}
    }
}
