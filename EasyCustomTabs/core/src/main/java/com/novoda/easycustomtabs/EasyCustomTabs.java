package com.novoda.easycustomtabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsSession;

import com.novoda.easycustomtabs.connection.Connection;
import com.novoda.easycustomtabs.connection.EasyCustomTabsConnection;
import com.novoda.easycustomtabs.navigation.EasyCustomTabsWebNavigator;
import com.novoda.easycustomtabs.navigation.IntentCustomizer;
import com.novoda.easycustomtabs.navigation.NavigationFallback;
import com.novoda.easycustomtabs.navigation.WebNavigator;
import com.novoda.easycustomtabs.provider.AvailableAppProvider;
import com.novoda.easycustomtabs.provider.EasyCustomTabsAvailableAppProvider;
import com.novoda.notils.exception.DeveloperError;

import java.util.List;

public final class EasyCustomTabs implements WebNavigator, Connection, AvailableAppProvider {

    private static Context applicationContext;
    private Connection connection;
    private WebNavigator webNavigator;
    private AvailableAppProvider availableAppProvider;

    private EasyCustomTabs() {
        //no-op
    }

    private static class LazyHolder {
        private static final EasyCustomTabs INSTANCE = new EasyCustomTabs();
    }

    public static EasyCustomTabs getInstance() {
        if (applicationContext == null) {
            throw new DeveloperError("EasyCustomTabs must be initialized. Use EasyCustomTabs.initialize(context)");
        }

        return LazyHolder.INSTANCE;
    }

    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
        LazyHolder.INSTANCE.connection = EasyCustomTabsConnection.newInstance();
        LazyHolder.INSTANCE.webNavigator = EasyCustomTabsWebNavigator.newInstance();
        LazyHolder.INSTANCE.availableAppProvider = EasyCustomTabsAvailableAppProvider.newInstance();
    }

    public Context getContext() {
        return applicationContext;
    }

    /**
     * Provides a {@link NavigationFallback} to specify navigation mechanism in case of no Chrome Custom Tabs support found.
     *
     * @param navigationFallback
     * @return WebNavigator with navigation fallback.
     */
    @Override
    public WebNavigator withFallback(NavigationFallback navigationFallback) {
        return webNavigator.withFallback(navigationFallback);
    }

    /**
     * Provides a {@link IntentCustomizer} to be used to customize the Chrome Custom Tabs by attacking directly to
     * {@link com.novoda.easycustomtabs.navigation.EasyCustomTabsIntentBuilder}
     *
     * @param intentCustomizer
     * @return WebNavigator with customized Chrome Custom Tabs.
     */
    @Override
    public WebNavigator withIntentCustomizer(IntentCustomizer intentCustomizer) {
        return webNavigator.withIntentCustomizer(intentCustomizer);
    }

    /**
     * Navigates to the given url using Chrome Custom Tabs if available.
     * If there is no application supporting Chrome Custom Tabs and {@link NavigationFallback}
     * is provided it will be used to redirect navigation.
     *
     * @param url
     * @param activityContext
     */
    @Override
    public void navigateTo(Uri url, Activity activityContext) {
        webNavigator.navigateTo(url, activityContext);
    }

    /**
     * Connects given activity to {@link android.support.customtabs.CustomTabsService}
     *
     * @param activity
     */
    @Override
    public void connectTo(@NonNull Activity activity) {
        connection.connectTo(activity);
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * Starts a new session for Chrome Custom Tabs usage. Can be used to warmup particular Urls.
     * {@see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}}
     *
     * @return a new {@link CustomTabsSession} or null if not connected to service.
     */
    @Override
    @Nullable
    public CustomTabsSession newSession() {
        return connection.newSession();
    }

    @Override
    public void disconnectFrom(@NonNull Activity activity) {
        connection.disconnectFrom(activity);
    }

    /**
     * Asynchronous search for the best package with support for Chrome Custom Tabs.
     *
     * @param packageFoundCallback
     */
    @Override
    public void findBestPackage(@NonNull EasyCustomTabsAvailableAppProvider.PackageFoundCallback packageFoundCallback) {
        availableAppProvider.findBestPackage(packageFoundCallback);
    }
}
