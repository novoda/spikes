package com.novoda.simonsays.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.simple.Log;
import com.novoda.simonsays.BuildConfig;
import com.novoda.simonsays.R;
import com.novoda.simonsays.game.GameActivity;
import com.novoda.simonsays.highscores.HighscoresActivity;

public class MainMenuActivity extends AppCompatActivity {

    static {
        Log.setShowLogs(BuildConfig.DEBUG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Views.findById(this, R.id.main_menu_button_new_game)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
                        startActivity(intent);
                    }
                });
        Views.findById(this, R.id.main_menu_button_highscores)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, HighscoresActivity.class);
                        startActivity(intent);
                    }
                });
    }

}
