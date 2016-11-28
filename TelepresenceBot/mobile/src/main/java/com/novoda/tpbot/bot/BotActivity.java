package com.novoda.tpbot.bot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.R;
import com.novoda.tpbot.human.ControllerView;

public class BotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        View debugView = Views.findById(this, R.id.bot_controller_debug_view);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
    }
}
