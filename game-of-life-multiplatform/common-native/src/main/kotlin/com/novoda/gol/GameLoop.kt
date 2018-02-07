package com.novoda.gol

actual class GameLoopImpl : GameLoop {


    override var onTick: () -> Unit = {}

    override fun startWith(intervalMs: Int) {
    }

    override fun stop() {
    }

    override fun isLooping() = true

}
