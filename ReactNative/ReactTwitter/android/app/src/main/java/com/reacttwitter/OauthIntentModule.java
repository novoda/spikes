package com.reacttwitter;

import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;


public class OauthIntentModule extends ReactContextBaseJavaModule {

    private Promise promise;

    public OauthIntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "OauthIntentAndroid";
    }

    @ReactMethod
    public void registerForDeepLinking(Promise promise) {
        this.promise = promise;
    }

    void handleOnNewIntent(Uri uri) {
        if (promise == null) {
            return;
        }

        // TODO extract data from uri
        WritableMap map = Arguments.createMap();
        map.putString("uri", uri.toString());
        promise.resolve(map);
        promise = null;

//        promise.reject("Something is missing");
    }
}
