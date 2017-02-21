package com.amazonaws.cognito.sync.demo.client.cognito;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class CognitoTokenTask implements CompletableOnSubscribe {

    private final CognitoCachingCredentialsProvider credentialsProvider;
    private final String username;

    public CognitoTokenTask(CognitoCachingCredentialsProvider credentialsProvider, String username) {
        this.credentialsProvider = credentialsProvider;
        this.username = username;
    }

    @Override
    public void subscribe(CompletableEmitter e) throws Exception {
        ServerCognitoIdentityProvider identityProvider = (ServerCognitoIdentityProvider) credentialsProvider.getIdentityProvider();
        addLogins(username, identityProvider.getProviderName());
        credentialsProvider.getIdentityProvider().refresh();
        e.onComplete();
    }

    private void addLogins(String userName, String providerName) {
        Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null) {
            logins = new HashMap<>();
        }
        logins.put(providerName, userName);
        credentialsProvider.setLogins(logins);
    }
}
