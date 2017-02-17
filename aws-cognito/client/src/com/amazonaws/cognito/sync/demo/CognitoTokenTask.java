package com.amazonaws.cognito.sync.demo;

import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

import java.util.HashMap;
import java.util.Map;

public class CognitoTokenTask extends AsyncTask<String, Void, Void> {

    private final CognitoCachingCredentialsProvider credentialsProvider;

    public CognitoTokenTask(CognitoCachingCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    protected Void doInBackground(final String... params) {
        String userName = params[0];
        ServerCognitoIdentityProvider identityProvider = (ServerCognitoIdentityProvider) credentialsProvider.getIdentityProvider();
        addLogins(userName, identityProvider.getProviderName());
        return null;
    }

    private void addLogins(String userName, String providerName) {
        Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null) {
            logins = new HashMap<>();
        }
        logins.put(providerName, userName);
        credentialsProvider.setLogins(logins);
        credentialsProvider.getIdentityProvider().refresh();
    }

}
