package com.amazonaws.cognito.sync.demo.client.cognito;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.cognito.sync.demo.Identifiers;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class CognitoTokenTask implements CompletableOnSubscribe {

    private final CognitoCachingCredentialsProvider credentialsProvider;
    private final String username;
    private final Identifiers identifiers;

    public CognitoTokenTask(CognitoCachingCredentialsProvider credentialsProvider, Identifiers identifiers) {
        this.credentialsProvider = credentialsProvider;
        this.username = identifiers.getUserName();
        this.identifiers = identifiers;
    }

    @Override
    public void subscribe(CompletableEmitter e) throws Exception {
        ServerCognitoIdentityProvider identityProvider = (ServerCognitoIdentityProvider) credentialsProvider.getIdentityProvider();
        addLogins(username, identityProvider.getProviderName());
        String token = credentialsProvider.getIdentityProvider().refresh();
        identifiers.registerCognitoToken(token);
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
