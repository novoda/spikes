package com.novoda.gol.presentation

import kotlin.properties.Delegates.observable

class AppModel {

    private var isIdle by observable(true) { _, _, newValue ->
        onSimulationStateChanged(newValue)
    }

    var onSimulationStateChanged: (isIdle: Boolean) -> Unit by observable<(Boolean) -> Unit>({}) { _, _, newValue ->
        newValue(isIdle)
    }

    fun toggleSimulation() {
        isIdle = isIdle.not()
    }
}
