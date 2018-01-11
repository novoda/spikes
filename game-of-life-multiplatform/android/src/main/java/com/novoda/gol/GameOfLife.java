package com.novoda.gol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class GameOfLife extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_life);

        GameLoop gameLoop = new GameLoop();

        gameLoop.setOnTick(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.d("GameOfLife", "Tick");
                return null;
            }
        });
        gameLoop.startWith(300);
    }
}
