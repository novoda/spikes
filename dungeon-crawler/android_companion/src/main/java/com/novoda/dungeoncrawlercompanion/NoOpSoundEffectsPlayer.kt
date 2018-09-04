package com.novoda.dungeoncrawlercompanion

import com.novoda.dungeoncrawler.SoundEffectsPlayer

class NoOpSoundEffectsPlayer : SoundEffectsPlayer {
    override fun playComplete() {
        // no-op
    }

    override fun playAttack() {
        // no-op
    }

    override fun playMove(amount: Int) {
        // no-op
    }

    override fun playDie() {
        // no-op
    }

    override fun playKill() {
        // no-op
    }

    override fun playWin() {
        // no-op
    }

}
