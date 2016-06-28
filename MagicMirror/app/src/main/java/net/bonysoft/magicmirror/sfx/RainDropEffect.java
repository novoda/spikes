package net.bonysoft.magicmirror.sfx;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RainDropEffect implements ParticleEffect {

    private static final long FALL_DURATION = 250L;

    private static final Random RANDOM = new Random(SystemClock.currentThreadTimeMillis());
    private final Interpolator INTERPOLATOR = new LinearInterpolator();

    private List<ObjectAnimator> animators = new ArrayList<>();

    @Override
    public void animateParticle(View particleView, int parentWidth, int parentHeight) {
        float x = RANDOM.nextFloat() * parentWidth;
        particleView.setX(x);
        ObjectAnimator animator = ObjectAnimator.ofFloat(particleView, "y", 0, parentHeight);
        animator.setDuration(FALL_DURATION + RANDOM.nextInt(250));
        animator.setInterpolator(INTERPOLATOR);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
        animators.add(animator);
    }

    @Override
    public void stop() {
        for (ObjectAnimator animator : animators) {
            animator.setRepeatCount(1);
        }
        animators.clear();
    }

    @Override
    public int delayInBetween() {
        return 250 + RANDOM.nextInt(250);
    }
}
