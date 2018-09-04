package com.novoda.dungeoncrawler;

import android.util.Log;

public class SpeakerSoundEffectsPlayer implements SoundEffectsPlayer {
    @Override
    public void playComplete() { // TODO
//      noToneAC();
        Log.d("TUT", "complete");
    }

    @Override
    public void playAttack() { // TODO
//        int freq = map(sin(millis() / 2.0) * 1000.0, -1000, 1000, 500, 600);
//        if (random8(5) == 0) {
//            freq *= 3;
//        }
//        toneAC(freq, MAX_VOLUME);
//        Log.d("TUT", "attacking");
    }

    @Override
    public void playMove(int amount) { // TODO
//        int f = map(abs(amount), 0, 90, 80, 900) + random8(100);
//        if (playerPositionModifier < 0) f -= 500;
//        if (playerPositionModifier > 0) f += 200;
//        toneAC(f, min(min(abs(amount) / 9, 5), MAX_VOLUME));
        if (amount != 0) {
//            Log.d("TUT", "tilt " + amount);
        }
    }

    @Override
    public void playDie() { // TODO
//        int freq = max(1000 - (millis() - killTime), 10);
//        freq += random8(200);
//        int vol = max(10 - (millis() - killTime) / 200, 0);
//        toneAC(freq, MAX_VOLUME);
//        Log.d("TUT", "dead");
    }

    @Override
    public void playKill() { // TODO
//        toneAC(2000, MAX_VOLUME, 1000, true);
//        Log.d("TUT", "kill");
    }

    @Override
    public void playWin() { // TODO
//        int freq = (millis() - stageStartTime) / 3.0;
//        freq += map(sin(millis() / 20.0) * 1000.0, -1000, 1000, 0, 20);
//        int vol = 10;//max(10 - (millis()-stageStartTime)/200, 0);
//        toneAC(freq, MAX_VOLUME);
        Log.d("TUT", "win");
    }
}
