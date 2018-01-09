package com.novoda.gol.patterns

import com.novoda.gol.CellMatrix

abstract class AbstractPattern(private val cellMatrix: CellMatrix) : PatternEntity {

    override fun cellAtPosition(x: Int, y: Int) = cellMatrix.cellAtPosition(x, y)

    override fun getWidth() = cellMatrix.getWidth()

    override fun getHeight() = cellMatrix.getHeight()

    override fun getSeeds() = cellMatrix.getSeeds()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractPattern

        if (cellMatrix != other.cellMatrix) return false

        return true
    }

    override fun hashCode(): Int {
        return cellMatrix.hashCode()
    }


}