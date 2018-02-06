package com.novoda.gol

interface GameLoop {

    var onTick: () -> Unit

    fun startWith(intervalMs: Int)

    fun stop()

    fun isLooping(): Boolean
}
