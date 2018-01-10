package com.novoda.gol.presentation

interface AppView {

    var controlButtonLabel: String
    var patternSelectionVisibility: Boolean

    var onControlButtonClicked : () -> Unit

}
