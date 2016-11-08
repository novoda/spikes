package com.novoda.tpbot.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.simple.Log;
import com.novoda.tpbot.R;

public class BotControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_controller);
        DirectionPadView padView = Views.findById(this, R.id.bot_controller_direction_view);
        padView.setOnDirectionPressedListener(new OnDirectionPressedListener() {
            @Override
            public void onDirectionPressed(BotDirection direction) {
                Log.d("onDirectionPressed: " + direction);
            }

            @Override
            public void onDirectionReleased(BotDirection direction) {
                Log.d("onDirectionReleased: " + direction);
            }

            @Override
            public void onLazersPressed() {
                Log.d("Pew!");
            }
        });
    }

}
