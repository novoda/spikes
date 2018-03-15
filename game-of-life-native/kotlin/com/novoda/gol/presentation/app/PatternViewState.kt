package com.novoda.gol.presentation.app

import com.novoda.gol.patterns.PatternEntity

data class PatternViewState(
        var shouldDisplay: Boolean,
        val patternEntities: List<PatternEntity>
)
