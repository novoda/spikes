package com.gertherb.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.gertherb.base.TypefaceFactory;

public class FontEditText extends EditText {

    private final TypefaceFactory typeFaceFactory;

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.typeFaceFactory = new TypefaceFactory();
        initTypeface(context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.typeFaceFactory = new TypefaceFactory();
        initTypeface(context, attrs);
    }

    private void initTypeface(Context context, AttributeSet attrs) {
        Typeface typeface = typeFaceFactory.createFrom(context, attrs);
        setTypeface(typeface);
    }

}
