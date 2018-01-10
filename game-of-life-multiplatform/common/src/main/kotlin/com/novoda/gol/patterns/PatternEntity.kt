package com.novoda.gol.patterns

import com.novoda.gol.data.CellMatrix

interface PatternEntity : CellMatrix {
    fun getName(): String
}
