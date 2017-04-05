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

    private static final float SPACING_MULTIPLIER = 1.0f;

    private final TextPaint dropCapPaint = new TextPaint();
    private final TextPaint copyTextPaint = new TextPaint();

    private final Rect dropCapBounds = new Rect();
    private final Rect characterBounds = new Rect();

    private final TypefaceFactory typefaceFactory;
    private final float spacer;

    private Layout copyStaticLayout;
    private Layout dropCapCopyStaticLayout;

    private String dropCapText;
    private String copyText;

    private int numberOfDropCaps;
    private int lineSpacingExtra;
    private int numberOfLinesToSpan;
    private int dropCapWidth;
    private int dropCapLineHeight;
    private float dropCapBaseline;
    private float distanceFromViewPortTop;

    private boolean canDrawDropCap;

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
            setDropCapFontType(dropCapFontPath);

            String copyFontPath = typedArray.getString(R.styleable.DropCapView_copyFontPath);
            setCopyFontType(copyFontPath);

            int defaultNumberOfDropCaps = 1;
            numberOfDropCaps = typedArray.getInt(R.styleable.DropCapView_numberOfDropCaps, defaultNumberOfDropCaps);

            int defaultLineSpacingExtra = 0;
            lineSpacingExtra = typedArray.getDimensionPixelSize(R.styleable.DropCapView_lineSpacingExtra, defaultLineSpacingExtra);

            int dropCapDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.drop_cap_default_text_size);
            int dropCapTextSize = typedArray.getDimensionPixelSize(R.styleable.DropCapView_dropCapTextSize, dropCapDefaultTextSize);
            setDropCapTextSize(TypedValue.COMPLEX_UNIT_PX, dropCapTextSize);

            int dropCapDefaultTextColor = getResources().getColor(R.color.drop_cap_default_text);
            int dropCapTextColor = typedArray.getColor(R.styleable.DropCapView_dropCapTextColor, dropCapDefaultTextColor);
            setDropCapTextColor(dropCapTextColor);

            int copyDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.drop_cap_copy_default_text_size);
            int copyTextSize = typedArray.getDimensionPixelSize(R.styleable.DropCapView_copyTextSize, copyDefaultTextSize);
            setCopyTextSize(TypedValue.COMPLEX_UNIT_PX, copyTextSize);

            int copyDefaultTextColor = getResources().getColor(R.color.drop_cap_copy_default_text);
            int copyTextColor = typedArray.getColor(R.styleable.DropCapView_copyTextColor, copyDefaultTextColor);
            setCopyTextColor(copyTextColor);

            String text = typedArray.getString(R.styleable.DropCapView_android_text);
            setText(text);

        } finally {
            typedArray.recycle();
        }
    }

    public void setNumberOfDropCaps(int numberOfDropCaps) {
        this.numberOfDropCaps = numberOfDropCaps;

        String text = dropCapText + copyText;
        setText(text);
    }

    public void setText(String text) {
        if (enoughTextForDropCap(text)) {
            dropCapText = String.valueOf(text.substring(0, numberOfDropCaps));
            copyText = String.valueOf(text.subSequence(dropCapText.length(), text.length()));
        } else {
            dropCapText = String.valueOf('\0');
            copyText = (text == null) ? "" : text;
        }

        remeasureAndRedraw();
    }

    private boolean enoughTextForDropCap(String text) {
        return text != null && text.length() > numberOfDropCaps;
    }

    public void setDropCapFontType(String fontPath) {
        Typeface typeface = typefaceFactory.createFrom(getContext(), fontPath);
        if (dropCapPaint.getTypeface() == typeface) {
            return;
        }

        dropCapPaint.setTypeface(typeface);
        dropCapPaint.setAntiAlias(true);
        dropCapPaint.setSubpixelText(true);

        remeasureAndRedraw();
    }

    private void remeasureAndRedraw() {
        if (dropCapCopyStaticLayout != null || copyStaticLayout != null) {
            copyStaticLayout = null;
            dropCapCopyStaticLayout = null;
            requestLayout();
            invalidate();
        }
    }

    public void setCopyFontType(String fontPath) {
        Typeface typeface = typefaceFactory.createFrom(getContext(), fontPath);
        if (copyTextPaint.getTypeface() == typeface) {
            return;
        }

        copyTextPaint.setTypeface(typeface);
        copyTextPaint.setAntiAlias(true);
        copyTextPaint.setSubpixelText(true);

        remeasureAndRedraw();
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

        remeasureAndRedraw();
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

        remeasureAndRedraw();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int allowedWidth = totalWidth - horizontalPadding;

        measureDropCapFor(allowedWidth);

        if (canDrawDropCap) {
            measureRemainingCopyFor(allowedWidth);
        } else {
            measureWholeTextFor(allowedWidth);
        }

        int desiredHeight = dropCapLineHeight + copyStaticLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        int desiredHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, desiredHeightMeasureSpec);
    }

    private void measureDropCapFor(int totalWidth) {
        dropCapWidth = (int) (dropCapPaint.measureText(dropCapText, 0, dropCapText.length()) + spacer);
        dropCapPaint.getTextBounds(dropCapText, 0, dropCapText.length(), dropCapBounds);
        int remainingWidthAfterDropCap = totalWidth - dropCapWidth;

        if (dropCapCopyStaticLayout == null || dropCapCopyStaticLayout.getWidth() != remainingWidthAfterDropCap) {
            dropCapCopyStaticLayout = new StaticLayout(
                    copyText,
                    copyTextPaint,
                    remainingWidthAfterDropCap,
                    Layout.Alignment.ALIGN_NORMAL,
                    SPACING_MULTIPLIER,
                    lineSpacingExtra,
                    true
            );

            calculateLinesToSpan();
            calculateDropCapBaseline();

            canDrawDropCap = enoughLinesToWrapDropCap() && dropCapCopyFitsWithin(totalWidth);
        }
    }

    private void calculateLinesToSpan() {
        int currentLineTop = 0;
        numberOfLinesToSpan = 0;

        for (int i = 0; i < dropCapCopyStaticLayout.getLineCount(); i++) {
            currentLineTop = dropCapCopyStaticLayout.getLineTop(i);
            if (currentLineTop >= dropCapBounds.height()) {
                numberOfLinesToSpan = i;
                i = dropCapCopyStaticLayout.getLineCount();
            }
        }
        dropCapLineHeight = currentLineTop;
    }

    private void calculateDropCapBaseline() {
        float baseline = dropCapBounds.height() + getPaddingTop();
        dropCapBaseline = baseline - dropCapBounds.bottom;
    }

    private boolean enoughLinesToWrapDropCap() {
        return dropCapCopyStaticLayout.getLineCount() > numberOfLinesToSpan && numberOfLinesToSpan > 0;
    }

    private boolean dropCapCopyFitsWithin(int totalWidth) {
        float dropCapAndCopyWidth = dropCapCopyStaticLayout.getLineRight(0) + dropCapWidth;
        return dropCapCopyStaticLayout.getLineCount() > 0 && dropCapAndCopyWidth < totalWidth;
    }

    private void measureRemainingCopyFor(int totalWidth) {
        int lineStart = dropCapCopyStaticLayout.getLineEnd(numberOfLinesToSpanAsZeroIndex());
        int lineEnd = dropCapCopyStaticLayout.getText().length();
        String remainingText = String.valueOf(dropCapCopyStaticLayout.getText().subSequence(lineStart, lineEnd));

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

    private int numberOfLinesToSpanAsZeroIndex() {
        return numberOfLinesToSpan - 1;
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

    private float calculateCopyDistanceFromViewPortTop() {
        copyTextPaint.getTextBounds("d", 0, 1, characterBounds);
        float dHeight = characterBounds.height();
        float lineBaseline = copyStaticLayout.getLineBaseline(0);
        return lineBaseline - dHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canDrawDropCap) {
            drawDropCap(canvas);
            drawCopyWrappingDropCap(canvas);
            drawRemainingCopy(canvas);
        } else {
            drawCopyWithoutDropCap(canvas);
        }
    }

    private void drawDropCap(Canvas canvas) {
        float dropCapBaselineFromCopyTop = dropCapBaseline + distanceFromViewPortTop;
        canvas.drawText(dropCapText, 0, dropCapText.length(), getPaddingLeft(), dropCapBaselineFromCopyTop, dropCapPaint);
    }

    private void drawCopyWrappingDropCap(Canvas canvas) {
        for (int i = 0; i < numberOfLinesToSpan; i++) {
            int lineStart = dropCapCopyStaticLayout.getLineStart(i);
            int lineEnd = dropCapCopyStaticLayout.getLineEnd(i);

            int baseline = dropCapCopyStaticLayout.getLineBaseline(i) + getPaddingTop();

            canvas.drawText(
                    dropCapCopyStaticLayout.getText(),
                    lineStart,
                    lineEnd,
                    getPaddingLeft() + dropCapWidth,
                    baseline,
                    dropCapCopyStaticLayout.getPaint()
            );
        }
    }

    private void drawRemainingCopy(Canvas canvas) {
        int ascentPadding = Math.abs(dropCapCopyStaticLayout.getTopPadding());
        int baseline = dropCapCopyStaticLayout.getLineBottom(numberOfLinesToSpanAsZeroIndex()) - ascentPadding + getPaddingTop();
        canvas.translate(getPaddingLeft(), baseline);
        copyStaticLayout.draw(canvas);
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

}
