package com.novoda.gol.data


expect class GameLoop {

    var onTick: () -> Unit

    fun startWith(intervalMs: Int)

    fun stop()

    fun isLooping(): Boolean
}
