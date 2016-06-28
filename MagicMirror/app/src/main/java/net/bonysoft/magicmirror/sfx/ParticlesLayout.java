package net.bonysoft.magicmirror.sfx;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ParticlesLayout extends FrameLayout {

    private static final int PARTICLE_COUNT = 50;

    private final List<ParticleView> particleViews = new ArrayList<>(PARTICLE_COUNT);
    private ParticleEffectRunner runner;

    public ParticlesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialise() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            ParticleView particleView = new ParticleView(getContext());
            particleView.setLayoutParams(new ActionBar.LayoutParams(50, 50));
            addView(particleView);
            particleViews.add(particleView);
        }
    }

    public void startParticles(Particle particle) {
        applyDrawableFrom(particle);
        runner = new ParticleEffectRunner(particle.getEffect(), particleViews, this);
        runner.startParticles();
    }

    private void applyDrawableFrom(Particle particle) {
        Drawable drawable = getResources().getDrawable(particle.drawableResId());
        for (ImageView particleView : particleViews) {
            particleView.setImageDrawable(drawable);
        }
    }

    public void stopParticles() {
        if (runner != null) {
            runner.stopAllParticles();
        }
    }
}
