package net.bonysoft.magicmirror.sfx;

public final class ParticleRunnable implements Runnable {

    private final ParticleView particleview;
    private final ParticlesLayout particlesLayout;
    private final ParticleEffect effect;

    public ParticleRunnable(ParticlesLayout particlesLayout, ParticleView particleViews, ParticleEffect effect) {
        this.particleview = particleViews;
        this.particlesLayout = particlesLayout;
        this.effect = effect;
    }

    @Override
    public void run() {
        particleview.animateWith(effect, particlesLayout.getWidth(), particlesLayout.getHeight());
    }

}
