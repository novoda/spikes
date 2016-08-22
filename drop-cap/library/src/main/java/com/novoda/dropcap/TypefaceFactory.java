package com.novoda.dropcap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

class TypefaceFactory {

    private static final Map<String, SoftReference<Typeface>> FONT_CACHE = new HashMap<>();
    private AssetManager assetManager;

    public Typeface createFrom(Context context, String fontPath) {
        initAssetManager(context);
        return getTypeFace(fontPath);
    }

    private void initAssetManager(Context context) {
        this.assetManager = context.getAssets();
    }

    private Typeface getTypeFace(String fontPath) {
        synchronized (FONT_CACHE) {
            if (fontExistsInCache(fontPath)) {
                return getCachedTypeFace(fontPath);
            }

            Typeface typeface = createTypeFace(fontPath);
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

    private Typeface createTypeFace(String fontPath) {
        try {
            return Typeface.createFromAsset(assetManager, fontPath);
        } catch (RuntimeException exception) {
            Log.e(TypefaceFactory.class.getSimpleName(), exception.getMessage() + " will default from style instead");
            return Typeface.defaultFromStyle(Typeface.NORMAL);
        }
    }

    private void saveFontToCache(String fontPath, Typeface typeface) {
        FONT_CACHE.put(fontPath, new SoftReference<>(typeface));
    }

}
