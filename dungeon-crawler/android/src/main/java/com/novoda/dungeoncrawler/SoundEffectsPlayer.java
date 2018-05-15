package com.novoda.dungeoncrawler;

public interface SoundEffectsPlayer {

    void playComplete();

    void playAttack();

    void playMove(int amount);

    void playDie();

    void playKill();

    void playWin();
}
