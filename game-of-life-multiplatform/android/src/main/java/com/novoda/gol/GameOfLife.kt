package com.novoda.gol

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.gol.presentation.AndroidAppView
import com.novoda.gol.presentation.app.AppPresenter
import com.novoda.gol.presentation.app.AppView

class GameOfLife : AppCompatActivity() {

    private val appPresenter = AppPresenter()
    private lateinit var appView: AppView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_of_life)
        appView = findViewById<AndroidAppView>(R.id.app_view)
    }

    override fun onStart() {
        super.onStart()
        appPresenter.bind(appView)
    }

    override fun onStop() {
        super.onStop()
        appPresenter.unbind(appView)
    }
}
