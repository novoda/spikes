package com.reacttwitter.widgets;

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

public class ReactButtonShadowNode extends LayoutShadowNode implements CSSNode.MeasureFunction {

    private String text;

    private static final TextPaint textPaintInstance = new TextPaint();

    static {
        textPaintInstance.setFlags(TextPaint.ANTI_ALIAS_FLAG);
    }

    public ReactButtonShadowNode() {
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
        textPaint.setTextSize(PixelUtil.toPixelFromSP(14));
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

        measureOutput.height = layout.getHeight();
        measureOutput.width = layout.getWidth();
    }
}
