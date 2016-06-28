package net.bonysoft.magicmirror.sfx;

import android.support.annotation.DrawableRes;

import net.bonysoft.magicmirror.R;

public enum Particle {
    RAINDROPS(R.drawable.rain_drop, new RainDropEffect()),
    NONE(0, null);

    @DrawableRes
    private final int drawableResId;
    private final ParticleEffect effect;

    Particle(@DrawableRes int drawableRes, ParticleEffect effect) {
        this.drawableResId = drawableRes;
        this.effect = effect;
    }

    @DrawableRes
    public int drawableResId() {
        return drawableResId;
    }

    public ParticleEffect getEffect() {
        return effect;
    }
}
