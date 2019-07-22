package com.novoda.gol.presentation.board

import com.novoda.gol.patterns.PatternEntity

data class BoardViewInput(
        val isIdle: Boolean,
        val selectedPattern: PatternEntity? = null
)
