package net.bonysoft.magicmirror.sfx;

import android.support.annotation.ColorRes;

import net.bonysoft.magicmirror.R;

public enum FacialExpressionEffects {
    SADNESS(R.color.blue_of_sadness, Particle.RAINDROPS),
    SEARCHING(R.color.black, Particle.NONE),
    NO_EXPRESSION(R.color.green, Particle.NONE),
    HAPPINESS(R.color.golden_hapiness, Particle.NONE),
    JOYFULNESS(R.color.jolly_pink, Particle.NONE);

    @ColorRes
    private final int glowColorRes;
    private final Particle particle;

    FacialExpressionEffects(@ColorRes int glowColorRes, Particle particle) {
        this.glowColorRes = glowColorRes;
        this.particle = particle;
    }

    @ColorRes
    public int glowColorRes() {
        return glowColorRes;
    }

    public Particle getParticle() {
        return particle;
    }

    public boolean hasParticle() {
        return particle != Particle.NONE;
    }
}
