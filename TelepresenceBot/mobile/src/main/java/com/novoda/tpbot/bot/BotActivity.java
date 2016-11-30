package com.novoda.tpbot.bot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;

import java.util.Collections;

public class BotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        RecyclerView directions = Views.findById(this, R.id.bot_directions);

        directions.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        directions.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DirectionAdapter(LayoutInflater.from(this), Collections.<Direction>emptyList());
        directions.setAdapter(adapter);
    }

}
