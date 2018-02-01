package com.novoda.gol

import com.novoda.gol.presentation.DesktopAppView
import com.novoda.gol.presentation.app.AppPresenter
import com.novoda.gol.presentation.app.AppView
import javafx.stage.Stage
import tornadofx.*

class GameOfLife : App(DesktopAppView::class) {

    private val appPresenter = AppPresenter()
    private lateinit var appView: AppView

    override fun start(stage: Stage) {
        super.start(stage)
        appView = find(DesktopAppView::class)
        appPresenter.bind(appView)
    }

    override fun stop() {
        appPresenter.unbind(appView)
        super.stop()
    }
}
