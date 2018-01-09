package com.novoda.gol

class Presenter(private val view: View) {

    private var boardEntity: BoardEntity? = null

    fun nextIteration() {
        if (boardEntity == null) {
            throw IllegalStateException("Simulation not yet started")
        }
        boardEntity = boardEntity!!.nextIteration()
        view.render(boardEntity!!)
    }

    fun startSimulationWith(cellMatrix: CellMatrix) {
        boardEntity = SimulationBoardEntity(cellMatrix)
        view.render(boardEntity!!)
    }
}