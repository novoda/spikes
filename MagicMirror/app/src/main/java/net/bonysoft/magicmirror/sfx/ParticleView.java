package net.bonysoft.magicmirror.sfx;

import android.content.Context;
import android.widget.ImageView;

public class ParticleView extends ImageView {

    private ParticleEffect effect;

    public ParticleView(Context context) {
        super(context);
    }

    public void animateWith(ParticleEffect effect, int parentWidth, int parentHeight) {
        this.effect = effect;
        effect.animateParticle(this, parentWidth, parentHeight);
    }

    public void stopAnimation() {
        if (effect != null) {
            effect.stop();
        }
    }
}
