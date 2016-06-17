package com.reacttwitter;

import android.net.Uri;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OauthIntentPackage implements ReactPackage {
    private static final String REACT_TWITTER_OAUTH_SCHEME = "react-twitter-oauth";

    private OauthIntentModule intentModule;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        intentModule = new OauthIntentModule(reactContext);
        return Arrays.asList(new NativeModule[]{intentModule});
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    void handleOnNewIntent(Uri uri) {
        if (!isUriValid(uri)) {
            return;
        }
        intentModule.handleOnNewIntent(uri);
    }

    private boolean isUriValid(Uri uri) {
        return uri != null && REACT_TWITTER_OAUTH_SCHEME.equals(uri.getScheme());
    }

}
