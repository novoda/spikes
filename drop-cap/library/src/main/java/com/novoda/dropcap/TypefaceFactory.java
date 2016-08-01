package com.novoda.dropcap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

class TypefaceFactory {

    private static final Map<String, SoftReference<Typeface>> FONT_CACHE = new HashMap<>();
    private AssetManager assetManager;

    public Typeface createFrom(Context context, AttributeSet attrs) {
        initAssetManager(context);
        String fontPath = getFontPath(context, attrs);
        return createFrom(context, fontPath);
    }

    public Typeface createFrom(Context context, String fontPath) {
        initAssetManager(context);
        return getTypeFace(context, fontPath);
    }

    private void initAssetManager(Context context) {
        this.assetManager = context.getAssets();
    }

    private String getFontPath(Context context, AttributeSet attrs) {
        int[] attrsValues = {R.attr.fontPath};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, attrsValues);
        if (typedArray == null) {
            return null;
        }

        try {
            return typedArray.getString(0);
        } finally {
            typedArray.recycle();
        }
    }

    private boolean isValidId(String fontPath) {
        return fontPath != null;
    }

    private Typeface getTypeFace(Context context, String fontPath) {
        synchronized (FONT_CACHE) {
            if (fontExistsInCache(fontPath)) {
                return getCachedTypeFace(fontPath);
            }

            Typeface typeface = createTypeFace(context, fontPath);
            saveFontToCache(fontPath, typeface);

            return typeface;
        }
    }

    private boolean fontExistsInCache(String fontPath) {
        return FONT_CACHE.get(fontPath) != null && getCachedTypeFace(fontPath) != null;
    }

    private Typeface getCachedTypeFace(String fontPath) {
        return FONT_CACHE.get(fontPath).get();
    }

    private Typeface createTypeFace(Context context, String fontPath) {
        return Typeface.createFromAsset(assetManager, fontPath);
    }

    private void saveFontToCache(String fontPath, Typeface typeface) {
        FONT_CACHE.put(fontPath, new SoftReference<>(typeface));
    }

}
