package com.novoda.gol.patterns

import com.novoda.gol.data.CellMatrix
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.PositionEntity

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
