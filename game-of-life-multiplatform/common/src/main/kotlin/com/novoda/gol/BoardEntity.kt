package com.novoda.gol

import com.novoda.gol.patterns.PatternEntity

interface BoardEntity : CellMatrix {

    fun nextIteration(): BoardEntity

    fun toggleCell(x: Int, y: Int): BoardEntity

    fun applyPattern(patternEntity: PatternEntity):BoardEntity
}