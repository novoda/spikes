package com.novoda.easycustomtabs.navigation;

import android.app.PendingIntent;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

class MenuItemComposer implements Composer {

    private final String label;
    private final PendingIntent pendingIntent;

    public MenuItemComposer(@NonNull String label, @NonNull PendingIntent pendingIntent) {
        this.label = label;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public CustomTabsIntent.Builder compose(CustomTabsIntent.Builder builder) {
        return builder.addMenuItem(label, pendingIntent);
    }

}
