package com.novoda.simonsays.highscores;

import com.novoda.notils.caster.Views;
import com.novoda.simonsays.BuildConfig;
import com.novoda.simonsays.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class HighscoresActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = BuildConfig.APPLICATION_ID + "/EXTRA_SCORE";

    private static final String KEY_HIGHSCORE = "KEY_HIGHSCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores_activity);

        int playersScore = getIntent().getIntExtra(EXTRA_SCORE, 0);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int highscore = preferences.getInt(KEY_HIGHSCORE, 0);

        if (playersScore > highscore) {
            Views.findById(this, R.id.highscores_congratulations_label).setVisibility(View.VISIBLE);
            preferences.edit().putInt(KEY_HIGHSCORE, playersScore).apply();
            highscore = playersScore;
        }

        TextView yourScoreWidget = Views.findById(this, R.id.highscores_your_score_label);
        yourScoreWidget.setText(getString(R.string.your_score_was_x, playersScore));
        TextView highScoreWidget = Views.findById(this, R.id.highscores_high_score_label);
        highScoreWidget.setText(getString(R.string.highscore_is_x, highscore));
    }

}
