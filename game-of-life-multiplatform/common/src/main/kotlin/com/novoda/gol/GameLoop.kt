package com.novoda.gol


expect class GameLoop() {

    var onTick: () -> Unit

    fun startWith(intervalMs: Int)

    fun stop()

    fun isLooping(): Boolean
}
