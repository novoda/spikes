package com.novoda.simonsaysandroidthings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.novoda.simonsaysandroidthings.game.Highscore;

public class SharedPrefsHighscore implements Highscore {

    private static final String TAG = "SharedPrefsHighscore";

    private static final String KEY_HIGHSCORE = "highscore";

    private final Context context;

    public SharedPrefsHighscore(Context context) {
        this.context = context;
    }

    @Override
    public int current() {
        int current = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_HIGHSCORE, 0);
        Log.d(TAG, "current() returned: " + current);
        return current;
    }

    @Override
    public void setNew(int score) {
        Log.d(TAG, "setNew() called with: score = [" + score + "]");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(KEY_HIGHSCORE, score);
        editor.apply();
    }

}
