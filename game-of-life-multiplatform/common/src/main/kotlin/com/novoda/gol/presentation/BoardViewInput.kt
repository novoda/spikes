package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity

data class BoardViewInput(
        val isIdle: Boolean,
        val selectedPattern: PatternEntity? = null
)
