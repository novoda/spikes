package com.novoda.tpbot.bot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.R;

import java.util.Collections;

public class BotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        RecyclerView moves = Views.findById(this, R.id.bot_moves);

        moves.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        moves.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new MoveAdapter(LayoutInflater.from(this), Collections.<String>emptyList());
        moves.setAdapter(adapter);
    }

}
