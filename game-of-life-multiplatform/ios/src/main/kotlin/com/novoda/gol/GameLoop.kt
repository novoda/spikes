package com.novoda.gol

import platform.Foundation.*

actual class GameLoopImpl : GameLoop {

    private var timer: NSTimer? = null

    override var onTick: () -> Unit = {}

    override fun startWith(intervalMs: Int) {
        timer = NSTimer(NSDate(), 0.5, true, {
            onTick()
        })
        NSRunLoop.currentRunLoop().addTimer(timer!!, NSRunLoopCommonModes)
    }

    override fun stop() {
        timer?.invalidate()
        timer = null
    }

    override fun isLooping() = timer != null

}
