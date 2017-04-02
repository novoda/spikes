package com.novoda.pianohero;

import android.util.Log;

public interface KeyListener {

    KeyListener LOGGING = new KeyListener() {
        @Override
        public void onPress(Note note) {
            Log.e("!!!", "onPress " + note);
        }

        @Override
        public void onRelease(Note note) {
            Log.e("!!!", "onRelease " + note);
        }
    };

    void onPress(Note note);

    void onRelease(Note note);
}
