package com.novoda.dungeoncrawler;

import android.util.Log;

public class LogcatSoundEffectsPlayer implements SoundEffectsPlayer {
    @Override
    public void playComplete() {
        //        noToneAC();
        Log.d("TUT", "complete");
    }

    @Override
    public void playAttack() {
        Log.d("TUT", "attacking");
    }

    @Override
    public void playMove(int amount) {
        if (amount != 0) {
            Log.d("TUT", "tilt " + amount);
        }
    }

    @Override
    public void playDie() {
        Log.d("TUT", "dead");
    }

    @Override
    public void playKill() {
        Log.d("TUT", "kill");
    }

    @Override
    public void playWin() {
        Log.d("TUT", "win");
    }
}
