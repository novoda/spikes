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
        ControllerView padView = Views.findById(this, R.id.bot_controller_direction_view);
        padView.setControllerListener(new ControllerListener() {
            @Override
            public void onDirectionPressed(Direction direction) {
                Log.d("onDirectionPressed: " + direction);
            }

            @Override
            public void onDirectionReleased(Direction direction) {
                Log.d("onDirectionReleased: " + direction);
            }

            @Override
            public void onLazersPressed() {
                Log.d("Pew!");
            }
        });
    }

}
