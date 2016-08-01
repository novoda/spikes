package com.novoda.dropcap.demo;

import android.support.annotation.StringRes;

import com.novoda.drop_cap.R;

enum FontType {

    CABIN_REGULAR("Cabin Regular", R.string.sans_serif),
    FUNKROCKER("Funkrocker", R.string.funkrocker),
    MAGNIFICENT("Magnificent", R.string.magnificent),
    NEUROPOLITICAL("Neuropolitical", R.string.neuropolitical);

    private final String fontName;
    @StringRes
    private final int assetUrl;

    FontType(String fontName, @StringRes int assetUrl) {
        this.fontName = fontName;
        this.assetUrl = assetUrl;
    }

    public String getFontName() {
        return fontName;
    }

    @StringRes
    public int getAssetUrl() {
        return assetUrl;
    }

}
