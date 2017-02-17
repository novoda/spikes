package com.amazonaws.cognito.sync;

import android.app.Application;
import android.preference.PreferenceManager;

import com.amazonaws.cognito.sync.demo.Cognito;
import com.amazonaws.cognito.sync.devauth.client.SharedPreferencesWrapper;
import com.google.firebase.FirebaseApp;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;

public class AuthApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesWrapper.registerDevice(PreferenceManager.getDefaultSharedPreferences(this), generateUniqueDeviceId());
        FirebaseApp.initializeApp(this);
        Cognito.INSTANCE.init(getApplicationContext());
    }

    /**
     * Creates a 128 bit random string..
     */
    private static String generateUniqueDeviceId() {
        byte[] randomBytes = new SecureRandom().generateSeed(16);
        return new String(Hex.encodeHex(randomBytes));
    }

}
