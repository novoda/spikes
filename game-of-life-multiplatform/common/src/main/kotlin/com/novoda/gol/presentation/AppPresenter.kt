package com.novoda.gol.presentation

class AppPresenter {

    private val model = AppModel()

    fun bind(view: AppView) {

        model.onSimulationStateChanged = { isIdle ->
            view.controlButtonLabel = if (isIdle) "Start simulation" else "Stop Simulation"
            view.patternSelectionVisibility = isIdle
        }

        view.onControlButtonClicked = {
            model.toggleSimulation()
        }
    }

    fun unbind(view: AppView) {
        model.onSimulationStateChanged = {}
        view.onControlButtonClicked = {}
    }
}
