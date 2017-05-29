package com.novoda.pianohero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class NoteWidget extends View {

    private final Drawable noteDrawable;

    @Nullable
    private final Drawable sharpDrawable;

    public NoteWidget(Context context, Drawable noteDrawable, @Nullable Drawable sharpDrawable) {
        super(context);
        this.noteDrawable = noteDrawable;
        this.sharpDrawable = sharpDrawable;
    }

    public NoteWidget(Context context) {
        this(context, null);
    }

    public NoteWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            throw new RuntimeException("default View ctors are only for dev layout preview.");
        }
        noteDrawable = noteDrawable(context, R.drawable.note);
        sharpDrawable = sharpDrawable(context, R.drawable.sharp);
    }

    private Drawable noteDrawable(Context context, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(context, res);
        int width = context.getResources().getDimensionPixelSize(R.dimen.note_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.note_height);
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    private Drawable sharpDrawable(Context context, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(context, res);
        int width = context.getResources().getDimensionPixelSize(R.dimen.sharp_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.sharp_height);
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        if (sharpDrawable != null) {
            width = sharpDrawable.getBounds().width() + noteDrawable.getBounds().width();
            height = Math.max(sharpDrawable.getBounds().height(), noteDrawable.getBounds().height());
        } else {
            width = noteDrawable.getBounds().width();
            height = noteDrawable.getBounds().height();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sharpDrawable != null) {
            sharpDrawable.draw(canvas);
            int count = canvas.save();
            canvas.translate(sharpDrawable.getBounds().width(), (float) ((getHeight() - noteDrawable.getBounds().height()) * 0.5));
            noteDrawable.draw(canvas);
            canvas.restoreToCount(count);
        } else {
            noteDrawable.draw(canvas);
        }
    }

}
