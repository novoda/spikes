package com.novoda.easycustomtabs.navigation;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

class CloseButtonIconComposer implements Composer {

    private final Bitmap icon;

    public CloseButtonIconComposer(@NonNull Bitmap icon) {
        this.icon = icon;
    }

    @Override
    public CustomTabsIntent.Builder compose(CustomTabsIntent.Builder builder) {
        return builder.setCloseButtonIcon(icon);
    }

}
