package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity

data class PatternViewState(
        var shouldDisplay: Boolean,
        val patternEntities: List<PatternEntity>
)
