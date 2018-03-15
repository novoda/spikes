package com.novoda.gol.presentation.app

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardViewInput

interface AppView {

    var onControlButtonClicked : () -> Unit
    var onPatternSelected: (pattern : PatternEntity) -> Unit

    fun renderControlButtonLabel(controlButtonLabel: String)
    fun renderPatternSelectionVisibility(visibility: Boolean)
    fun renderBoardWith(boardViewInput: BoardViewInput)
}
