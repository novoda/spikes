package com.novoda.gol

import kotlin.browser.window


actual class GameLoop {

    private var loop: Int? = null

    actual var onTick: () -> Unit = {}

    actual fun startWith(intervalMs: Int) {
        loop = window.setInterval({
            onTick.invoke()
        }, intervalMs)
    }

    actual fun stop() {
        if (loop != null) {
            window.clearInterval(loop!!)
            loop = null
        }
    }

    actual fun isLooping() = loop != null
}
