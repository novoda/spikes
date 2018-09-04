package com.novoda.gol.data

import com.novoda.gol.patterns.PatternEntity

data class SimulationBoardEntity(private val cellMatrix: CellMatrix) : BoardEntity {

    override fun applyPattern(patternEntity: PatternEntity): BoardEntity {
        val seeds = this.cellMatrix.getSeeds().toMutableList()
        seeds.addAll(patternEntity.getSeeds())

        val cellMatrix = ListBasedMatrix(width = cellMatrix.getWidth(), height = cellMatrix.getHeight(), seeds = seeds)
        return SimulationBoardEntity(cellMatrix)
    }

    override fun getSeeds(): List<PositionEntity> = cellMatrix.getSeeds()

    override fun cellAtPosition(x: Int, y: Int) = cellMatrix.cellAtPosition(x, y)

    override fun getWidth(): Int = cellMatrix.getWidth()

    override fun getHeight(): Int = cellMatrix.getHeight()

    override fun nextIteration(): BoardEntity {
        val seeds = mutableListOf<PositionEntity>()

        for (x in 0 until cellMatrix.getWidth()) {
            for (y in 0 until cellMatrix.getHeight()) {
                val cell = cellAtPosition(x, y)
                if (isAlive(x, y, cell)) {
                    seeds.add(PositionEntity(x, y))
                }
            }
        }

        val nextIterationMatrix = ListBasedMatrix(getWidth(), getHeight(), seeds)
        return SimulationBoardEntity(nextIterationMatrix)
    }

    private fun isAlive(x: Int, y: Int, cellEntity: CellEntity): Boolean {
        val numberAliveNeighbours = numberAliveNeighbours(x, y)
        if (cellEntity.isAlive.not()) {
            if (numberAliveNeighbours == 3) {
                return true
            }

            return false

        } else {
            if (numberAliveNeighbours < 2 || numberAliveNeighbours > 3) {
                return false
            }
            return true
        }
    }

    private fun numberAliveNeighbours(x: Int, y: Int): Int {
        var numberAliveNeighbours = 0

        for (posX in x - 1..x + 1) {
            for (posY in y - 1..y + 1) {

                if (posX == x && posY == y) {
                    continue
                }

                val normalizedX = normalizeValue(posX, cellMatrix.getWidth())
                val normalizedY = normalizeValue(posY, cellMatrix.getHeight())

                val cell = cellMatrix.cellAtPosition(normalizedX, normalizedY)
                if (cell.isAlive) {
                    numberAliveNeighbours++
                }
            }
        }
        return numberAliveNeighbours
    }

    private fun normalizeValue(value: Int, boundary: Int): Int {
        if (value < 0) {
            return boundary - 1
        }

        if (value > boundary - 1) {
            return 0
        }

        return value
    }

    override fun toggleCell(x: Int, y: Int): BoardEntity {
        val seeds = cellMatrix.getSeeds().toMutableList()
        if (cellMatrix.cellAtPosition(x, y).isAlive) {
            seeds.remove(PositionEntity(x, y))
        } else {
            seeds.add(PositionEntity(x, y))
        }

        val nextIterationMatrix = ListBasedMatrix(getWidth(), getHeight(), seeds)
        return SimulationBoardEntity(nextIterationMatrix)
    }
}
