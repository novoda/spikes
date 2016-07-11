package com.mecharyry.dropcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class DropCapView extends View {

    private static final float SPACING_MULTIPLIER = 1.0f;

    private final TextPaint dropCapPaint = new TextPaint();
    private final TextPaint copyTextPaint = new TextPaint();

    private final Rect dropCapBounds = new Rect();
    private final Rect characterBounds = new Rect();

    private final TypefaceFactory typefaceFactory;
    private final float spacer;

    private Layout copyStaticLayout;
    private Layout dropCapStaticLayout;

    private String dropCapText;
    private String copyText;

    private int lineSpacingExtra;
    private int numberOfLinesToSpan;
    private int dropCapWidth;
    private int dropCapLineHeight;
    private float dropCapBaseline;

    private boolean shouldDisplayDropCap;
    private boolean hasPaintChanged;

    public DropCapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropCapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typefaceFactory = new TypefaceFactory();
        spacer = getResources().getDimensionPixelSize(R.dimen.drop_cap_space_right);
        applyCustomAttributes(context, attrs);
    }

    private void applyCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropCapView);

        if (typedArray == null) {
            return;
        }

        try {
            Typeface typeface = typefaceFactory.createFrom(context, attrs);

            dropCapPaint.setTypeface(typeface);
            dropCapPaint.setAntiAlias(true);
            dropCapPaint.setSubpixelText(true);

            copyTextPaint.setTypeface(typeface);
            copyTextPaint.setAntiAlias(true);
            copyTextPaint.setSubpixelText(true);

            int defaultLineSpacingExtra = 0;
            lineSpacingExtra = typedArray.getDimensionPixelSize(R.styleable.DropCapView_lineSpacingExtra, defaultLineSpacingExtra);

            int dropCapDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.drop_cap_default_text_size);
            int dropCapTextSize = typedArray.getDimensionPixelSize(R.styleable.DropCapView_dropCapTextSize, dropCapDefaultTextSize);

            int dropCapDefaultTextColor = getResources().getColor(R.color.drop_cap_default_text);
            int dropCapTextColor = typedArray.getColor(R.styleable.DropCapView_dropCapTextColor, dropCapDefaultTextColor);

            int copyDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.drop_cap_copy_default_text_size);
            int copyTextSize = typedArray.getDimensionPixelSize(R.styleable.DropCapView_copyTextSize, copyDefaultTextSize);

            int copyDefaultTextColor = getResources().getColor(R.color.drop_cap_copy_default_text);
            int copyTextColor = typedArray.getColor(R.styleable.DropCapView_copyTextColor, copyDefaultTextColor);

            dropCapPaint.setTextSize(dropCapTextSize);
            dropCapPaint.setColor(dropCapTextColor);

            copyTextPaint.setTextSize(copyTextSize);
            copyTextPaint.setColor(copyTextColor);

        } finally {
            typedArray.recycle();
        }
    }

    public void setDropCapFontType(String fontPath) {
        Typeface typeface = typefaceFactory.createFrom(getContext(), fontPath);
        if (dropCapPaint.getTypeface() == typeface) {
            return;
        }

        dropCapPaint.setTypeface(typeface);
        dropCapPaint.setAntiAlias(true);
        dropCapPaint.setSubpixelText(true);

        hasPaintChanged = true;
        requestLayout();
    }

    public void setCopyFontType(String fontPath) {
        Typeface typeface = typefaceFactory.createFrom(getContext(), fontPath);
        if (copyTextPaint.getTypeface() == typeface) {
            return;
        }

        copyTextPaint.setTypeface(typeface);
        copyTextPaint.setAntiAlias(true);
        copyTextPaint.setSubpixelText(true);

        hasPaintChanged = true;
        requestLayout();
    }

    public void setDropCapTextSize(@DimenRes float textSize) {
        if (textSize == dropCapPaint.getTextSize()) {
            return;
        }

        dropCapPaint.setTextSize(textSize);
        hasPaintChanged = true;
        requestLayout();
    }

    public float getDropCapTextSize() {
        return dropCapPaint.getTextSize();
    }

    public void setDropCapTextColor(@ColorInt int color) {
        if (color == dropCapPaint.getColor()) {
            return;
        }

        dropCapPaint.setColor(color);
        hasPaintChanged = true;
        invalidate();
    }

    @ColorInt
    public int getDropCapTextColor() {
        return copyTextPaint.getColor();
    }

    public void setCopyTextSize(@DimenRes float textSize) {
        if (textSize == copyTextPaint.getTextSize()) {
            return;
        }

        copyTextPaint.setTextSize(textSize);
        hasPaintChanged = true;
        requestLayout();
    }

    public float getCopyTextSize() {
        return copyTextPaint.getTextSize();
    }

    public void setCopyTextColor(@ColorInt int color) {
        if (color == copyTextPaint.getColor()) {
            return;
        }

        copyTextPaint.setColor(color);
        hasPaintChanged = true;
        invalidate();
    }

    @ColorInt
    public int getCopyTextColor() {
        return copyTextPaint.getColor();
    }

    public void setText(String text) {
        if (enoughTextForDropCap(text)) {
            dropCapText = String.valueOf(text.charAt(0));
            copyText = String.valueOf(text.subSequence(1, text.length()));
            shouldDisplayDropCap = true;
        } else {
            dropCapText = String.valueOf('\0');
            copyText = (text == null) ? "" : text;
            shouldDisplayDropCap = false;
        }
    }

    private boolean enoughTextForDropCap(CharSequence text) {
        return text != null && text.length() > 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int widthWithoutPadding = totalWidth - horizontalPadding;

        if (shouldDisplayDropCap) {
            measureDropCapFor(widthWithoutPadding);
        }

        measureCopyFor(widthWithoutPadding);

        int desiredHeight = dropCapLineHeight + copyStaticLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        int desiredHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, desiredHeightMeasureSpec);
    }

    private void measureDropCapFor(int width) {
        dropCapWidth = (int) (dropCapPaint.measureText(dropCapText, 0, 1) + spacer);
        dropCapPaint.getTextBounds(dropCapText, 0, 1, dropCapBounds);
        int copyWidthForDropCap = width - dropCapWidth;

        if (dropCapStaticLayout == null || dropCapStaticLayout.getWidth() != copyWidthForDropCap || hasPaintChanged) {
            dropCapStaticLayout = new StaticLayout(
                    dropCapText + copyText,
                    copyTextPaint,
                    copyWidthForDropCap,
                    Layout.Alignment.ALIGN_NORMAL,
                    SPACING_MULTIPLIER,
                    lineSpacingExtra,
                    true
            );

            int currentLineTop = 0;
            for (int i = 0; i < dropCapStaticLayout.getLineCount(); i++) {
                currentLineTop = dropCapStaticLayout.getLineTop(i);
                if (currentLineTop >= dropCapBounds.height()) {
                    numberOfLinesToSpan = i;
                    i = dropCapStaticLayout.getLineCount();
                }
            }
            dropCapLineHeight = currentLineTop;
        }

        if (numberOfLinesToSpan < 1) {
            shouldDisplayDropCap = false;
            return;
        }

        float baseline = dropCapBounds.height() + getPaddingTop();
        dropCapBaseline = baseline - dropCapBounds.bottom;
    }

    private void measureCopyFor(int totalWidth) {
        if (shouldDisplayDropCap && enoughLinesForDropCap()) {
            int lineStart = dropCapStaticLayout.getLineEnd(numberOfLinesToSpan - 1);
            int lineEnd = dropCapStaticLayout.getText().length();
            String remainingText = String.valueOf(dropCapStaticLayout.getText().subSequence(lineStart, lineEnd));

            if (copyStaticLayout == null || copyStaticLayout.getWidth() != totalWidth || !remainingText.equals(copyStaticLayout.getText())) {
                copyStaticLayout = new StaticLayout(
                        remainingText,
                        copyTextPaint,
                        totalWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        SPACING_MULTIPLIER,
                        lineSpacingExtra,
                        true
                );
            }

            float translateBy = getCopyDistanceFromViewPortTop();
            dropCapBaseline = dropCapBaseline + translateBy;

        } else {
            if (copyStaticLayout == null || copyStaticLayout.getWidth() != totalWidth || textIsDifferent()) {
                copyStaticLayout = new StaticLayout(
                        dropCapText + copyText,
                        copyTextPaint,
                        totalWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        SPACING_MULTIPLIER,
                        lineSpacingExtra,
                        true
                );
            }
        }
    }

    private boolean enoughLinesForDropCap() {
        return dropCapStaticLayout.getLineCount() > numberOfLinesToSpan;
    }

    private float getCopyDistanceFromViewPortTop() {
        copyTextPaint.getTextBounds("d", 0, 1, characterBounds);
        float dHeight = characterBounds.height();
        float lineBaseline = copyStaticLayout.getLineBaseline(0);
        return lineBaseline - dHeight;
    }

    private boolean textIsDifferent() {
        return !copyStaticLayout.getText().equals(dropCapText + copyText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shouldDisplayDropCap && enoughLinesForDropCap()) {
            drawDropCap(canvas);
            drawCopyForDropCap(canvas);
            drawRemainingCopy(canvas);
        } else {
            drawCopyWithoutDropCap(canvas);
        }
        hasPaintChanged = false;
    }

    private void drawCopyWithoutDropCap(Canvas canvas) {
        for (int i = 0; i < copyStaticLayout.getLineCount(); i++) {
            int lineStart = copyStaticLayout.getLineStart(i);
            int lineEnd = copyStaticLayout.getLineEnd(i);

            CharSequence charSequence = copyStaticLayout.getText().subSequence(lineStart, lineEnd);

            int baseline = copyStaticLayout.getLineBaseline(i) + getPaddingTop();

            canvas.drawText(
                    charSequence,
                    0,
                    charSequence.length(),
                    getPaddingLeft(),
                    baseline,
                    copyStaticLayout.getPaint()
            );
        }
    }

    private void drawDropCap(Canvas canvas) {
        canvas.drawText(dropCapStaticLayout.getText(), 0, 1, getPaddingLeft(), dropCapBaseline, dropCapPaint);
    }

    private void drawCopyForDropCap(Canvas canvas) {
        for (int i = 0; i < numberOfLinesToSpan; i++) {
            int lineStart = dropCapStaticLayout.getLineStart(i);
            int lineEnd = dropCapStaticLayout.getLineEnd(i);

            int baseline = dropCapStaticLayout.getLineBaseline(i) + getPaddingTop();

            if (i == 0) {
                lineStart = lineStart + 1;
            }

            canvas.drawText(
                    dropCapStaticLayout.getText(),
                    lineStart,
                    lineEnd,
                    getPaddingLeft() + dropCapWidth,
                    baseline,
                    dropCapStaticLayout.getPaint()
            );
        }
    }

    private void drawRemainingCopy(Canvas canvas) {
        int ascentPadding = Math.abs(dropCapStaticLayout.getTopPadding());
        int baseline = dropCapStaticLayout.getLineBottom(numberOfLinesToSpan - 1) - ascentPadding + getPaddingTop();
        canvas.translate(getPaddingLeft(), baseline);
        copyStaticLayout.draw(canvas);
    }

}
