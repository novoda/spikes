package com.novoda.easycustomtabs.navigation;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;

import com.novoda.easycustomtabs.EasyCustomTabs;
import com.novoda.easycustomtabs.connection.Connection;
import com.novoda.notils.exception.DeveloperError;

import java.util.ArrayList;
import java.util.List;

public class EasyCustomTabsIntentBuilder {

    private final Connection connection;
    private final List<Composer> composers;

    public EasyCustomTabsIntentBuilder(Connection connection, List<Composer> composers) {
        this.connection = connection;
        this.composers = composers;
    }

    public static EasyCustomTabsIntentBuilder newInstance() {
        List<Composer> composerList = new ArrayList<>();
        return new EasyCustomTabsIntentBuilder(EasyCustomTabs.getInstance(), composerList);
    }

    public EasyCustomTabsIntentBuilder withToolbarColor(@ColorInt int color) {
        composers.add(new ToolbarColorComposer(color));
        return this;
    }

    public EasyCustomTabsIntentBuilder withUrlBarHiding() {
        composers.add(new UrlBarHidingComposer());
        return this;
    }

    public EasyCustomTabsIntentBuilder withMenuItem(String label, PendingIntent pendingIntent) {
        composers.add(new MenuItemComposer(label, pendingIntent));
        return this;
    }

    public EasyCustomTabsIntentBuilder withActionButton(Bitmap icon, String description, PendingIntent pendingIntent, boolean shouldTint) {
        composers.add(new ActionButtonComposer(icon, description, pendingIntent, shouldTint));
        return this;
    }

    public EasyCustomTabsIntentBuilder withCloseButtonIcon(Bitmap icon) {
        composers.add(new CloseButtonIconComposer(icon));
        return this;
    }

    public EasyCustomTabsIntentBuilder withExitAnimations(Context context, @AnimRes int enterResId, @AnimRes int exitResId) {
        composers.add(new ExitAnimationsComposer(context, enterResId, exitResId));
        return this;
    }

    public EasyCustomTabsIntentBuilder withStartAnimations(Context context, @AnimRes int enterResId, @AnimRes int exitResId) {
        composers.add(new StartAnimationsComposer(context, enterResId, exitResId));
        return this;
    }

    public EasyCustomTabsIntentBuilder showingTitle() {
        composers.add(new ShowTitleComposer());
        return this;
    }

    public CustomTabsIntent createIntent() {
        if (!connection.isConnected()) {
            throw new DeveloperError("An active connection to custom tabs service is required for intent creation");
        }

        CustomTabsSession customTabsSession = connection.newSession();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabsSession);
        for (Composer composer : composers) {
            builder = composer.compose(builder);
        }

        return builder.build();
    }

}
