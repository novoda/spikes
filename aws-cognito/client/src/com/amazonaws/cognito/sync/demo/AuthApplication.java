package com.amazonaws.cognito.sync.demo;

import android.app.Application;
import android.preference.PreferenceManager;

import com.amazonaws.cognito.sync.demo.client.ServerApiClient;
import com.amazonaws.cognito.sync.demo.client.cognito.Cognito;
import com.google.firebase.FirebaseApp;

public class AuthApplication extends Application {

    private Identifiers identifiers;
    private ServerApiClient apiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        identifiers = new Identifiers(PreferenceManager.getDefaultSharedPreferences(this));
        apiClient = createServerApiClient(identifiers);
        Cognito.INSTANCE.init(getApplicationContext(), apiClient);
    }

    public Identifiers getIdentifiers() {
        return identifiers;
    }

    public ServerApiClient getApiClient() {
        return apiClient;
    }

    private static ServerApiClient createServerApiClient(Identifiers identifiers) {
        return new ServerApiClient(BuildConfig.AUTHENTICATION_ENDPOINT, "AWSCognitoDeveloperAuthenticationSample", identifiers);
    }

}

