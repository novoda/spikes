package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity

data class BoardViewState(
        val isIdle: Boolean,
        val selectedPattern: PatternEntity? = null
)
