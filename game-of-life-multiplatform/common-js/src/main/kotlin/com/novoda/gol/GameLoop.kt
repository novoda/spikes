package com.novoda.gol

import kotlin.browser.window

actual class GameLoopImpl : GameLoop {

    private var loop: Int? = null

    override var onTick: () -> Unit = {}

    override fun startWith(intervalMs: Int) {
        loop = window.setInterval({
            onTick.invoke()
        }, intervalMs)
    }

    override fun stop() {
        if (loop != null) {
            window.clearInterval(loop!!)
            loop = null
        }
    }

    override fun isLooping() = loop != null
}
