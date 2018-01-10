package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity

interface AppView {

    var controlButtonLabel: String
    var patternSelectionVisibility: Boolean
    var board: BoardViewState

    var onControlButtonClicked : () -> Unit
    var onPatternSelected: (pattern : PatternEntity) -> Unit

}
