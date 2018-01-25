package com.novoda.gol.presentation.app

class AppPresenter(private val model: AppModel = AppModel()) {

    fun bind(view: AppView) {

        model.onSimulationStateChanged = { isIdle ->
            view.renderControlButtonLabel(if (isIdle) "Start Simulation" else "Stop Simulation")
            view.renderPatternSelectionVisibility(visibility = isIdle)
        }

        model.onBoardStateChanged = view::renderBoardWith

        view.onControlButtonClicked = model::toggleSimulation

        view.onPatternSelected = model::selectPattern
    }

    fun unbind(view: AppView) {
        model.onSimulationStateChanged = {}
        view.onControlButtonClicked = {}
    }
}
