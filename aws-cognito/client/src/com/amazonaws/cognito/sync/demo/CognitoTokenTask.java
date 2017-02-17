package com.amazonaws.cognito.sync.demo;

import android.os.AsyncTask;

public class CognitoTokenTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(final String... params) {
        String userName = params[0];
        String providerName = ((ServerCognitoIdentityProvider) Cognito.INSTANCE.credentialsProvider()
                .getIdentityProvider()).getProviderName();
        Cognito.INSTANCE.addLogins(providerName, userName);

        Cognito.INSTANCE.credentialsProvider().getIdentityProvider().refresh();
        return null;
    }
}
