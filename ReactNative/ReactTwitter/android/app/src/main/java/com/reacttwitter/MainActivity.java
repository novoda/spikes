package com.reacttwitter;

import android.content.Intent;
import android.net.Uri;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.oney.gcm.GcmPackage;

import java.util.Arrays;
import java.util.List;

import io.neson.react.notification.NotificationPackage;

public class MainActivity extends ReactActivity {

    private static final String REACT_TWITTER_OAUTH_SCHEME = "react-twitter-oauth";

    private OauthIntentPackage oauthIntentPackage = new OauthIntentPackage();

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "ReactTwitter";
    }

    /**
     * Returns whether dev mode should be enabled.
     * This enables e.g. the dev menu.
     */
    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    /**
     * A list of packages used by the app. If the app uses additional views
     * or modules besides the default ones, add more packages here.
     */
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.asList(
                new MainReactPackage(),
                oauthIntentPackage,
                new GcmPackage(),
                new NotificationPackage(this)
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data == null || !REACT_TWITTER_OAUTH_SCHEME.equals(data.getScheme())) {
            return;
        }

        oauthIntentPackage.handleOnNewIntent(data);
    }
}
