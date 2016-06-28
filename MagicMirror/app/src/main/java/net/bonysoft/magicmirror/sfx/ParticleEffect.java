package net.bonysoft.magicmirror.sfx;

import android.view.View;

public interface ParticleEffect {

    void animateParticle(View particleViews, int parentWidth, int parentHeight);

    int delayInBetween();

    void stop();
}
