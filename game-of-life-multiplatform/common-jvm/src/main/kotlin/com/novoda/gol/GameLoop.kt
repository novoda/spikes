package com.novoda.gol

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

actual class GameLoop actual constructor() {

    private var gameLoop: Disposable? = null

    actual var onTick: () -> Unit = {}

    actual fun startWith(intervalMs: Int) {
        gameLoop = Flowable.interval(intervalMs.toLong(), TimeUnit.MILLISECONDS).subscribe {
            onTick()
        }
    }

    actual fun stop() {
        gameLoop?.dispose()
        gameLoop = null

    }

    actual fun isLooping() = gameLoop != null

}
