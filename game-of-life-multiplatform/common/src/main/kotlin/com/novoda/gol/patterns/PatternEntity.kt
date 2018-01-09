package com.novoda.gol.patterns

import com.novoda.gol.CellMatrix

interface PatternEntity : CellMatrix {
    fun getName(): String
}