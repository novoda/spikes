package com.novoda.dungeoncrawler;

import android.util.Log;

public class LogcatSoundEffectsPlayer implements SoundEffectsPlayer {
    @Override
    public void playComplete() {
        //        noToneAC();
        Log.v("TUT", "complete");
    }

    @Override
    public void playAttack() {
        Log.v("TUT", "attacking");
    }

    @Override
    public void playMove(int amount) {
        if (amount != 0) {
            Log.v("TUT", "tilt " + amount);
        }
    }

    @Override
    public void playDie() {
        Log.v("TUT", "dead");
    }

    @Override
    public void playKill() {
        Log.v("TUT", "kill");
    }

    @Override
    public void playWin() {
        Log.v("TUT", "win");
    }
}
