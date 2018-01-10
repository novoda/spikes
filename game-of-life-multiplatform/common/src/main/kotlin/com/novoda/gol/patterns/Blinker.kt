package com.novoda.gol.patterns

import com.novoda.gol.data.CellMatrix
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.PositionEntity

class Blinker private constructor(cellMatrix: CellMatrix) : AbstractPattern(cellMatrix) {

    override fun getName() = "Blinker"

    companion object {
        fun create(): Blinker {
            val seeds = listOf(
                    PositionEntity(0, 1),
                    PositionEntity(1, 1),
                    PositionEntity(2, 1))
            return Blinker(ListBasedMatrix(width = 3, height = 3, seeds = seeds))
        }
    }
}
