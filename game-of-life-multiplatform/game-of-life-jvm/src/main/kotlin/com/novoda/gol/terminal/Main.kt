package com.novoda.gol.terminal

import com.novoda.gol.PositionEntity
import com.novoda.gol.ListBasedMatrix
import com.novoda.gol.Presenter
import com.novoda.gol.View
import java.io.BufferedReader
import java.io.InputStreamReader

const val EXIT = "exit"

private val view = View()
private val presenter = Presenter(view)

fun main(args: Array<String>) {

    var keepLooping = true
    val br = BufferedReader(InputStreamReader(System.`in`))

    print("enter width: ")
    val width = br.readLine().toInt()

    print("enter height: ")
    val height = br.readLine().toInt()

    val cellMatrix = ListBasedMatrix(width = width, height = height, seeds = listOf(PositionEntity(2, 1), PositionEntity(2, 2), PositionEntity(2, 3)))

    presenter.startSimulationWith(cellMatrix)

    while (keepLooping) {
        val input = br.readLine()
        keepLooping = input != EXIT

        if (keepLooping) {
            presenter.nextIteration()
        }
    }

}


