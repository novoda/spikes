package com.gertherb.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.gertherb.base.TypefaceFactory;

public class FontButton extends Button {

    private final TypefaceFactory typeFaceFactory;

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.typeFaceFactory = new TypefaceFactory();
        initTypeface(context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.typeFaceFactory = new TypefaceFactory();
        initTypeface(context, attrs);
    }

    private void initTypeface(Context context, AttributeSet attrs) {
        Typeface typeface = typeFaceFactory.createFrom(context, attrs);
        setTypeface(typeface);
    }

}
