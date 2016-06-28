package net.bonysoft.magicmirror.sfx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GlowView extends ImageView {

    private static final int TRANSITION_DURATION = 700;

    private final SparseArrayCompat<BitmapDrawable> bitmapDrawables = new SparseArrayCompat<>();

    public GlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void transitionToColor(@ColorRes int colorRes) {
        // TODO timeout if too fast
        Drawable previousBackground = getPreviousDrawableSafely();
        TransitionDrawable transitionDrawable = createTransitionDrawable(previousBackground, colorRes);

        setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(TRANSITION_DURATION);
    }

    private Drawable getPreviousDrawableSafely() {
        Drawable previousDrawable = getDrawable();
        if (previousDrawable == null) {
            int transparentRes = getColor(android.R.color.transparent);
            return new ColorDrawable(transparentRes);
        } else {
            TransitionDrawable previousTransitionDrawable = (TransitionDrawable) previousDrawable;
            int idOfFrontDrawable = previousTransitionDrawable.getId(1);
            return previousTransitionDrawable.findDrawableByLayerId(idOfFrontDrawable);
        }
    }

    private TransitionDrawable createTransitionDrawable(Drawable previousBackground, @ColorRes int colorRes) {
        return new TransitionDrawable(new Drawable[]{
                previousBackground,
                getOrCreateBitmapDrawableFor(colorRes)
        });
    }

    private BitmapDrawable getOrCreateBitmapDrawableFor(@ColorRes int colorRes) {
        BitmapDrawable bitmapDrawable = bitmapDrawables.get(colorRes);
        if (bitmapDrawable == null) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradientDrawable.setGradientRadius(getWidth() / 4);
            gradientDrawable.setColors(new int[]{
                    getColor(colorRes),
                    Color.BLACK
            });

            Bitmap bitmap = drawableToBitmap(gradientDrawable);
            bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

            bitmapDrawables.put(colorRes, bitmapDrawable);
        }
        return bitmapDrawable;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth() / 2, getWidth() / 2, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @ColorInt
    private int getColor(@ColorRes int colorRes) {
        return getResources().getColor(colorRes);
    }
}
