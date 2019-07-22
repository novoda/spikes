package com.novoda.gol

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

actual class GameLoopImpl : GameLoop {

    private var gameLoop: Disposable? = null

    override var onTick: () -> Unit = {}

    override fun startWith(intervalMs: Int) {
        gameLoop = Flowable.interval(intervalMs.toLong(), TimeUnit.MILLISECONDS).subscribe {
            onTick()
        }
    }

    override fun stop() {
        gameLoop?.dispose()
        gameLoop = null

    }

    override fun isLooping() = gameLoop != null

}
