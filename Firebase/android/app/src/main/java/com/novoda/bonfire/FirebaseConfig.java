package com.novoda.bonfire;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.novoda.bonfire.analytics.ErrorLogger;

public class FirebaseConfig implements Config {

    private static final String ORDER_CHANNELS_BY_NAME = "orderChannelsByName";
    private static final int CACHE_EXPIRATION_IN_SECONDS = 3600;

    private final FirebaseRemoteConfig firebaseRemoteConfig;

    public static FirebaseConfig newInstance() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        return new FirebaseConfig(firebaseRemoteConfig);
    }

    public FirebaseConfig init(final ErrorLogger errorLogger) {
        firebaseRemoteConfig.fetch(CACHE_EXPIRATION_IN_SECONDS)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseRemoteConfig.activateFetched();
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                errorLogger.reportError(e, "Failed to retrieve remote config");
                            }
                        }
                );
        return this;
    }

    private FirebaseConfig(FirebaseRemoteConfig firebaseRemoteConfig) {
        this.firebaseRemoteConfig = firebaseRemoteConfig;
    }

    @Override
    public boolean orderChannelsByName() {
        return firebaseRemoteConfig.getBoolean(ORDER_CHANNELS_BY_NAME);
    }

}
