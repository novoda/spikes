package com.novoda.gol.terminal

import com.novoda.gol.data.BoardEntity
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.data.SimulationBoardEntity
import java.io.BufferedReader
import java.io.InputStreamReader

const val EXIT = "exit"

fun main(args: Array<String>) {

    var keepLooping = true
    val br = BufferedReader(InputStreamReader(System.`in`))

    print("enter width: ")
    val width = br.readLine().toInt()

    print("enter height: ")
    val height = br.readLine().toInt()

    val cellMatrix = ListBasedMatrix(width = width, height = height, seeds = listOf(PositionEntity(2, 1), PositionEntity(2, 2), PositionEntity(2, 3)))

    var boardEntity: BoardEntity = SimulationBoardEntity(cellMatrix)


    while (keepLooping) {
        val input = br.readLine()
        keepLooping = input != EXIT

        render(boardEntity)

        if (keepLooping) {
            boardEntity = boardEntity.nextIteration()
        }
    }
}

fun render(boardEntity: BoardEntity) {
    for (y in 0 until boardEntity.getHeight()) {
        for (x in 0 until boardEntity.getWidth()) {
            val cellAtPosition = boardEntity.cellAtPosition(x, y)
            if (cellAtPosition.isAlive) {
                print("X")
            } else {
                print(" ")
            }

        }
        print("\n")
    }

    print("exit: Stop Simulation\n")
    print("next: Next Iteration\n")
}


