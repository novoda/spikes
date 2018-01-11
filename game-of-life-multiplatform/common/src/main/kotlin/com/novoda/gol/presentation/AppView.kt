package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity

interface AppView {

    var onControlButtonClicked : () -> Unit
    var onPatternSelected: (pattern : PatternEntity) -> Unit

    fun renderControlButtonLabel(controlButtonLabel: String)
    fun renderPatternSelectionVisibility(visibility: Boolean)
    fun renderBoardWith(boardViewInput: BoardViewInput)
}
