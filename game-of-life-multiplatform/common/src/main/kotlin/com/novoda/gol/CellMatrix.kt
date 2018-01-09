package com.novoda.gol

interface CellMatrix {

    fun cellAtPosition(x: Int, y: Int): CellEntity

    fun getWidth(): Int

    fun getHeight(): Int

    fun getSeeds(): List<PositionEntity>
}