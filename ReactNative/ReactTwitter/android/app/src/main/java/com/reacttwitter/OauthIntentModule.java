package com.reacttwitter;

import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

public class OauthIntentModule extends ReactContextBaseJavaModule {

    public static final String KEY_URL = "url";

    private Promise promise;

    public OauthIntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "OauthIntentAndroid";
    }

    @ReactMethod
    @SuppressWarnings("unused") // used from react-native code
    public void registerForDeepLinking(Promise promise) {
        this.promise = promise;
    }

    void handleOnNewIntent(Uri uri) {
        if (promise == null) {
            return;
        }

        WritableMap map = Arguments.createMap();
        map.putString(KEY_URL, uri.toString());
        promise.resolve(map);
        promise = null;
    }
}
