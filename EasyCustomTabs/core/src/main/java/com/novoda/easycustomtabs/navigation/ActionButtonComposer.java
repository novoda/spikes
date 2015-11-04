package com.novoda.easycustomtabs.navigation;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

class ActionButtonComposer implements Composer {

    private final Bitmap icon;
    private final String description;
    private final PendingIntent pendingIntent;
    private final boolean shouldTint;

    public ActionButtonComposer(@NonNull Bitmap icon, @NonNull String description, @NonNull PendingIntent pendingIntent, boolean shouldTint) {
        this.icon = icon;
        this.description = description;
        this.pendingIntent = pendingIntent;
        this.shouldTint = shouldTint;
    }

    @Override
    public CustomTabsIntent.Builder compose(CustomTabsIntent.Builder builder) {
        return builder.setActionButton(icon, description, pendingIntent, shouldTint);
    }

}
