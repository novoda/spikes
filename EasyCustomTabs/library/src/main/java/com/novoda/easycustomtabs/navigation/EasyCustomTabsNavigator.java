package com.novoda.easycustomtabs.navigation;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.novoda.easycustomtabs.EasyCustomTabs;
import com.novoda.easycustomtabs.connection.Connection;

public class EasyCustomTabsNavigator implements Navigator {

    private final Connection connection;
    private NavigationFallback navigationFallback;
    private IntentCustomizer intentCustomizer;

    EasyCustomTabsNavigator(Connection connection) {
        this.connection = connection;
    }

    public static EasyCustomTabsNavigator newInstance() {
        Connection connection = EasyCustomTabs.getInstance();
        return new EasyCustomTabsNavigator(connection);
    }

    public EasyCustomTabsNavigator withFallback(NavigationFallback navigationFallback) {
        this.navigationFallback = navigationFallback;
        return this;
    }

    public EasyCustomTabsNavigator withIntentCustomizer(IntentCustomizer intentCustomizer) {
        this.intentCustomizer = intentCustomizer;
        return this;
    }

    public void navigateTo(Uri url, Activity activityContext) {
        if (connection.isConnected()) {
            buildIntent().launchUrl(activityContext, url);
        } else if (hasNavigationFallback()) {
            navigationFallback.onFallbackNavigateTo(url);
        }
    }

    private CustomTabsIntent buildIntent() {
        EasyCustomTabsIntentBuilder basicIntentBuilder = EasyCustomTabsIntentBuilder.newInstance();

        if (intentCustomizer == null) {
            return basicIntentBuilder.createIntent();
        }

        return intentCustomizer.onCustomiseIntent(basicIntentBuilder).createIntent();
    }

    private boolean hasNavigationFallback() {
        return navigationFallback != null;
    }

}
