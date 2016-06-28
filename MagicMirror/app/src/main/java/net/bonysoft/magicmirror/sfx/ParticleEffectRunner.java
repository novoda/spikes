package net.bonysoft.magicmirror.sfx;

import android.os.Handler;
import android.view.View;

import com.novoda.notils.logger.simple.Log;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectRunner {

    private final ParticleEffect effect;
    private final List<ParticleView> particleViews;
    private final ParticlesLayout particlesLayout;

    private final Handler handler = new Handler();
    private final List<Runnable> runnables = new ArrayList<>();

    public ParticleEffectRunner(ParticleEffect effect, List<ParticleView> particleViews, ParticlesLayout particlesLayout) {
        this.effect = effect;
        this.particleViews = particleViews;
        this.particlesLayout = particlesLayout;
    }

    public void startParticles() {
        moveParticlesOutsideOfScreen(particleViews);
        for (int i = 0; i < particleViews.size(); i++) {
            ParticleRunnable particleRunnable = new ParticleRunnable(particlesLayout, particleViews.get(i), effect);
            int totalDelay = (i * effect.delayInBetween());
            handler.postDelayed(particleRunnable, totalDelay);
            runnables.add(particleRunnable);
        }

    }

    private void moveParticlesOutsideOfScreen(List<ParticleView> particleViews) {
        for (View particleView : particleViews) {
            particleView.setY(-particleView.getHeight());
        }
    }

    public void stopAllParticles() {
        stopAllRunningAnimations();
        clearAllParticleQueue();
    }

    private void clearAllParticleQueue() {
        for (Runnable runnable : runnables) {
            handler.removeCallbacks(runnable);
        }
        runnables.clear();
    }

    private void stopAllRunningAnimations() {
        for (ParticleView particleView : particleViews) {
            Log.d("Stopping animation of: " + particleView);
            particleView.stopAnimation();
        }
    }
}
