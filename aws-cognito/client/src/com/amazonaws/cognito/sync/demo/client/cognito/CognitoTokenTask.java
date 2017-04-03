package com.amazonaws.cognito.sync.demo.client.cognito;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CognitoTokenTask implements ObservableOnSubscribe<String> {

    private final CognitoCachingCredentialsProvider credentialsProvider;
    private final String username;

    public CognitoTokenTask(CognitoCachingCredentialsProvider credentialsProvider, String username) {
        this.credentialsProvider = credentialsProvider;
        this.username = username;
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        ServerCognitoIdentityProvider identityProvider = (ServerCognitoIdentityProvider) credentialsProvider.getIdentityProvider();
        addLogins(username, identityProvider.getProviderName());
        String token = credentialsProvider.getIdentityProvider().refresh();
        emitter.onNext(token);
        emitter.onComplete();
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
