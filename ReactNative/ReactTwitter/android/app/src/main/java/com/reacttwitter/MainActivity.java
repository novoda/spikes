package com.reacttwitter;

import android.content.Intent;
import android.net.Uri;

import com.dieam.reactnativepushnotification.ReactNativePushNotificationPackage;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.reacttwitter.widgets.NativeWidgetsPackage;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ReactActivity {

    private OauthIntentPackage oauthIntentPackage = new OauthIntentPackage();
    private ReactNativePushNotificationPackage pushNotificationPackage;

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
        pushNotificationPackage = new ReactNativePushNotificationPackage(this);
        return Arrays.asList(
                new MainReactPackage(),
                new NativeWidgetsPackage(),
                oauthIntentPackage,
                pushNotificationPackage
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();

        oauthIntentPackage.handleOnNewIntent(data);

        pushNotificationPackage.newIntent(intent);
    }
}
