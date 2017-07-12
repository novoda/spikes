package com.novoda.simonsaysandroidthings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.novoda.simonsaysandroidthings.game.Score;

public class SharedPrefsScore implements Score {

    private static final String TAG = "SharedPrefsHighscore";

    private static final String KEY_HIGHSCORE = "highscore";
    private static final String KEY_CURRENT_SCORE = "current_score";

    private final Context context;

    SharedPrefsScore(Context context) {
        this.context = context;
    }

    @Override
    public int currentHighscore() {
        int current = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_HIGHSCORE, 0);
        Log.d(TAG, "currentHighscore() returned: " + current);
        return current;
    }

    @Override
    public void setNewHighscore(int score) {
        Log.d(TAG, "setNewHighscore() called with: score = [" + score + "]");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(KEY_HIGHSCORE, score);
        editor.apply();
    }

    @Override
    public void setCurrentScore(int score) {
        Log.d(TAG, "setCurrentScore() called with: score = [" + score + "]");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(KEY_CURRENT_SCORE, score);
        editor.apply();
    }

}
