package com.novoda.simonsaysandroidthings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.novoda.simonsaysandroidthings.game.Highscore;

public class SharedPrefsHighscore implements Highscore {

    private static final String KEY_HIGHSCORE = "highscore";

    private final Context context;

    public SharedPrefsHighscore(Context context) {
        this.context = context;
    }

    @Override
    public int current() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_HIGHSCORE, 0);
    }

    @Override
    public void setNew(int score) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(KEY_HIGHSCORE, score);
        editor.apply();
    }

}
