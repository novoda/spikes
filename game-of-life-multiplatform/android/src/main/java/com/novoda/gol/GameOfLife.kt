package com.novoda.gol

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class GameOfLife : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_of_life)

        val gameLoop = GameLoop()

        gameLoop.onTick =  {
            Log.d("GameOfLife", "Tick")
        }
        gameLoop.startWith(300)
    }
}
