package com.novoda.gol.patterns

import com.novoda.gol.CellMatrix
import com.novoda.gol.ListBasedMatrix
import com.novoda.gol.PositionEntity

class Glider private constructor(cellMatrix: CellMatrix) : AbstractPattern(cellMatrix) {
    override fun getName() = "Glider"

    companion object {
        fun create(): Glider {
            val seeds = listOf(
                    PositionEntity(1, 0),
                    PositionEntity(2, 1),
                    PositionEntity(2, 2),
                    PositionEntity(1, 2),
                    PositionEntity(0, 2))

            return Glider(ListBasedMatrix(width = 3, height = 3, seeds = seeds))
        }
    }
}
