package com.novoda.dropcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class DropCapView extends View {

    private static final int TEXT_SET_INDEX = 0;
    private static final int[] androidAttributeSet = {
            android.R.attr.text
    };

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
    private float distanceFromViewPortTop;

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
            String dropCapFontPath = typedArray.getString(R.styleable.DropCapView_dropCapFontPath);
            Typeface dropCapTypeface = typefaceFactory.createFrom(context, dropCapFontPath);

            String copyFontPath = typedArray.getString(R.styleable.DropCapView_copyFontPath);
            Typeface copyTypeface = typefaceFactory.createFrom(context, copyFontPath);

            dropCapPaint.setTypeface(dropCapTypeface);
            dropCapPaint.setAntiAlias(true);
            dropCapPaint.setSubpixelText(true);

            copyTextPaint.setTypeface(copyTypeface);
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

        TypedArray androidTypedArray = context.obtainStyledAttributes(attrs, androidAttributeSet);
        if (androidTypedArray == null) {
            return;
        }

        try {
            String text = typedArray.getString(TEXT_SET_INDEX);
            setText(text);
        } finally {
            androidTypedArray.recycle();
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

        performRemeasureAndRedraw();
    }

    public void setCopyFontType(String fontPath) {
        Typeface typeface = typefaceFactory.createFrom(getContext(), fontPath);
        if (copyTextPaint.getTypeface() == typeface) {
            return;
        }

        copyTextPaint.setTypeface(typeface);
        copyTextPaint.setAntiAlias(true);
        copyTextPaint.setSubpixelText(true);

        performRemeasureAndRedraw();
    }

    public void setDropCapTextSize(float textSizeSp) {
        setDropCapTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    }

    public void setDropCapTextSize(int unit, float size) {
        float sizeForDisplay = TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        setRawDropCapTextSize(sizeForDisplay);
    }

    private void setRawDropCapTextSize(float size) {
        if (size == dropCapPaint.getTextSize()) {
            return;
        }

        dropCapPaint.setTextSize(size);

        performRemeasureAndRedraw();
    }

    public float getDropCapTextSize() {
        return dropCapPaint.getTextSize();
    }

    public void setDropCapTextColor(@ColorInt int color) {
        if (color == dropCapPaint.getColor()) {
            return;
        }

        dropCapPaint.setColor(color);

        invalidate();
    }

    @ColorInt
    public int getDropCapTextColor() {
        return copyTextPaint.getColor();
    }

    public void setCopyTextSize(float textSizeSp) {
        setCopyTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    }

    public void setCopyTextSize(int unit, float size) {
        float sizeForDisplay = TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        setRawCopyTextSize(sizeForDisplay);
    }

    private void setRawCopyTextSize(float size) {
        if (size == copyTextPaint.getTextSize()) {
            return;
        }

        copyTextPaint.setTextSize(size);

        performRemeasureAndRedraw();
    }

    public float getCopyTextSize() {
        return copyTextPaint.getTextSize();
    }

    public void setCopyTextColor(@ColorInt int color) {
        if (color == copyTextPaint.getColor()) {
            return;
        }

        copyTextPaint.setColor(color);

        invalidate();
    }

    @ColorInt
    public int getCopyTextColor() {
        return copyTextPaint.getColor();
    }

    public void setText(String text) {
        if (isSameText(text)) {
            return;
        }

        if (enoughTextForDropCap(text)) {
            dropCapText = String.valueOf(text.charAt(0));
            copyText = String.valueOf(text.subSequence(1, text.length()));
        } else {
            dropCapText = String.valueOf('\0');
            copyText = (text == null) ? "" : text;
        }

        performRemeasureAndRedraw();
    }

    private void performRemeasureAndRedraw() {
        if (dropCapStaticLayout != null || copyStaticLayout != null) {
            copyStaticLayout = null;
            dropCapStaticLayout = null;
            requestLayout();
            invalidate();
        }
    }

    private boolean isSameText(String text) {
        return text != null && text.equals(dropCapText + copyText);
    }

    private boolean enoughTextForDropCap(CharSequence text) {
        return text != null && text.length() > 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int widthWithoutPadding = totalWidth - horizontalPadding;

        measureDropCapFor(widthWithoutPadding);

        if (enoughLinesForDropCap()) {
            measureRemainingCopyFor(totalWidth);
        } else {
            measureWholeTextFor(totalWidth);
        }

        int desiredHeight = dropCapLineHeight + copyStaticLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        int desiredHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, desiredHeightMeasureSpec);
    }

    private void measureDropCapFor(int width) {
        dropCapWidth = (int) (dropCapPaint.measureText(dropCapText, 0, 1) + spacer);
        dropCapPaint.getTextBounds(dropCapText, 0, 1, dropCapBounds);
        int copyWidthForDropCap = width - dropCapWidth;

        if (dropCapStaticLayout == null || dropCapStaticLayout.getWidth() != copyWidthForDropCap) {
            dropCapStaticLayout = new StaticLayout(
                    dropCapText + copyText,
                    copyTextPaint,
                    copyWidthForDropCap,
                    Layout.Alignment.ALIGN_NORMAL,
                    SPACING_MULTIPLIER,
                    lineSpacingExtra,
                    true
            );

            calculateLinesToSpan();

            if (enoughLinesForDropCap()) {
                float baseline = dropCapBounds.height() + getPaddingTop();
                dropCapBaseline = baseline - dropCapBounds.bottom;
            }
        }
    }

    private void calculateLinesToSpan() {
        int currentLineTop = 0;
        numberOfLinesToSpan = 0;

        for (int i = 0; i < dropCapStaticLayout.getLineCount(); i++) {
            currentLineTop = dropCapStaticLayout.getLineTop(i);
            if (currentLineTop >= dropCapBounds.height()) {
                numberOfLinesToSpan = i;
                i = dropCapStaticLayout.getLineCount();
            }
        }
        dropCapLineHeight = currentLineTop;
    }

    private void measureRemainingCopyFor(int totalWidth) {
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

            distanceFromViewPortTop = calculateCopyDistanceFromViewPortTop();
        }
    }

    private void measureWholeTextFor(int totalWidth) {
        if (copyStaticLayout == null || copyStaticLayout.getWidth() != totalWidth) {
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

    private boolean enoughLinesForDropCap() {
        return dropCapStaticLayout.getLineCount() > numberOfLinesToSpan && numberOfLinesToSpan > 0;
    }

    private float calculateCopyDistanceFromViewPortTop() {
        copyTextPaint.getTextBounds("d", 0, 1, characterBounds);
        float dHeight = characterBounds.height();
        float lineBaseline = copyStaticLayout.getLineBaseline(0);
        return lineBaseline - dHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (enoughLinesForDropCap()) {
            drawDropCap(canvas);
            drawCopyForDropCap(canvas);
            drawRemainingCopy(canvas);
        } else {
            drawCopyWithoutDropCap(canvas);
        }
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
        float dropCapBaselineFromCopyTop = dropCapBaseline + distanceFromViewPortTop;
        canvas.drawText(dropCapStaticLayout.getText(), 0, 1, getPaddingLeft(), dropCapBaselineFromCopyTop, dropCapPaint);
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
