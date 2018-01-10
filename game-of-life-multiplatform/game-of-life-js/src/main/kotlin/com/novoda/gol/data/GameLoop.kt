package com.novoda.gol.data

import kotlin.browser.window


actual class GameLoop {

    private var loop: Int? = null

    actual var onTick: () -> Unit = {}

    actual fun startWith(intervalMs: Int) {
        loop = window.setInterval({
            onTick.invoke()
        })
    }

    actual fun stop() {
        if (loop != null) {
            window.clearInterval(loop!!)
        }
    }

    actual fun isLooping() = loop != null
}
