package com.reacttwitter.widgets;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.facebook.csslayout.CSSConstants;
import com.facebook.csslayout.CSSMeasureMode;
import com.facebook.csslayout.CSSNode;
import com.facebook.csslayout.MeasureOutput;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

class ReactButtonShadowNode extends LayoutShadowNode implements CSSNode.MeasureFunction {

    private static final TextPaint textPaintInstance = new TextPaint();

    static {
        textPaintInstance.setFlags(TextPaint.ANTI_ALIAS_FLAG);
        textPaintInstance.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // our button has a bold typeface by default
    }

    private String text;
    private final Rect padding;

    ReactButtonShadowNode() {
        padding = new Rect();
        setMeasureFunction(this);
    }

    @Override
    protected void markUpdated() {
        super.markUpdated();
        super.dirty();
    }

    @ReactProp(name = "text")
    public void setText(@Nullable String text) {
        this.text = text;
        markUpdated();
    }

    @ReactProp(name = "textSize", defaultInt = 14)
    public void setTextSize(int textSize) {
        textPaintInstance.setTextSize(PixelUtil.toPixelFromSP(textSize));
        markUpdated();
    }

    @ReactProp(name = "backgroundImage")
    public void setBackgroundImage(String backgroundImage) {
        Drawable drawable = getDrawableByName(backgroundImage);
        if (drawable == null) {
            padding.set(0, 0, 0, 0);
            return;
        }
        drawable.getPadding(this.padding);
        markUpdated();
    }

    private Drawable getDrawableByName(String name) {
        int resId = getThemedContext().getResources().getIdentifier(name.toLowerCase(), "drawable", getThemedContext().getPackageName());
        if (resId == 0) {
            return null;
        }

        return ContextCompat.getDrawable(getThemedContext(), resId);
    }

    @Override
    public void measure(
            CSSNode node,
            float width,
            CSSMeasureMode widthMode,
            float height,
            CSSMeasureMode heightMode,
            MeasureOutput measureOutput) {
        // Code snippet copied from ReactTextShadowNode, and slightly adapted to our use case
        // TODO(5578671): Handle text direction (see View#getTextDirectionHeuristic)
        TextPaint textPaint = textPaintInstance;
        Layout layout;
        BoringLayout.Metrics boring = BoringLayout.isBoring(text, textPaint);
        boolean isBoringLayout = boring != null;
        float desiredWidth = !isBoringLayout ?
                Layout.getDesiredWidth(text, textPaint) : Float.NaN;

        // technically, width should never be negative, but there is currently a bug in
        boolean unconstrainedWidth = widthMode == CSSMeasureMode.UNDEFINED || width < 0;

        if (!isBoringLayout &&
                (unconstrainedWidth ||
                        (!CSSConstants.isUndefined(desiredWidth) && desiredWidth <= width))) {
            // Is used when the width is not known and the text is not boring, ie. if it contains
            // unicode characters.
            layout = new StaticLayout(
                    text,
                    textPaint,
                    (int) Math.ceil(desiredWidth),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    true
            );
        } else if (isBoringLayout && (unconstrainedWidth || boring.width <= width)) {
            // Is used for single-line, boring text when the width is either unknown or bigger
            // than the width of the text.
            layout = BoringLayout.make(
                    text,
                    textPaint,
                    boring.width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    boring,
                    true
            );
        } else {
            // Is used for multiline, boring text and the width is known.
            layout = new StaticLayout(
                    text,
                    textPaint,
                    (int) width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    true
            );
        }

        measureOutput.height = layout.getHeight() + (padding.top + padding.bottom);
        measureOutput.width = layout.getWidth() + (padding.left + padding.right);
    }
}
