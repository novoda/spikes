package com.novoda.easycustomtabs.navigation;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.novoda.easycustomtabs.EasyCustomTabs;
import com.novoda.easycustomtabs.connection.Connection;

public class EasyCustomTabsWebNavigator implements WebNavigator {

    private final Connection connection;
    private NavigationFallback navigationFallback;
    private IntentCustomizer intentCustomizer;

    EasyCustomTabsWebNavigator(Connection connection) {
        this.connection = connection;
    }

    public static EasyCustomTabsWebNavigator newInstance() {
        Connection connection = EasyCustomTabs.getInstance();
        return new EasyCustomTabsWebNavigator(connection);
    }

    public EasyCustomTabsWebNavigator withFallback(NavigationFallback navigationFallback) {
        this.navigationFallback = navigationFallback;
        return this;
    }

    public EasyCustomTabsWebNavigator withIntentCustomizer(IntentCustomizer intentCustomizer) {
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
