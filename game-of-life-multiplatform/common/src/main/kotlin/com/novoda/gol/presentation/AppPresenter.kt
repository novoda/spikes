package com.novoda.gol.presentation

class AppPresenter {

    private val model = AppModel()

    fun bind(view: AppView) {

        model.onSimulationStateChanged = { isIdle ->
            view.renderControlButtonLabel(if (isIdle) "Start simulation" else "Stop Simulation")
            view.renderPatternSelectionVisibility(visibility = isIdle)
        }

        model.onBoardStateChanged = view::renderBoard

        view.onControlButtonClicked = model::toggleSimulation

        view.onPatternSelected = model::selectPattern
    }

    fun unbind(view: AppView) {
        model.onSimulationStateChanged = {}
        view.onControlButtonClicked = {}
    }
}
