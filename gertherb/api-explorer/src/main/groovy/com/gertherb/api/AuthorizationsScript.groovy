package com.gertherb.api

import com.gertherb.api.shared.ApiScript
import com.gertherb.api.shared.Credentials
import com.gertherb.api.shared.OAuthCredentials
import groovyx.net.http.Method

class AuthorizationsScript extends ApiScript {

    Credentials credentials = Credentials.load()

    @Override
    String description() {
        "This method will create a new authorization for the specified OAuth application, " +
        "only if an authorization for that application doesn’t already exist for the user. " +
        "(The URL includes the 20 character client ID for the OAuth app that is requesting " +
        "the token.) It returns the user’s token for the application if one exists. " +
        "Otherwise, it creates one."
    }

    @Override
    String path() {
        "/authorizations/clients/${credentials.getClientId()}"
    }

    @Override
    Method method() {
        Method.PUT
    }

    @Override
    String authorization() {
        credentials.getBasicAuthorizationValue()
    }

    @Override
    Object body() {
        [
                client_secret: credentials.clientSecret,
                scopes       : ['repo'],
                note         : 'Gertherb'
        ]
    }

    @Override
    void onSuccess(Object resp, Object json, long startAt) {
        super.onSuccess(resp, json, startAt)
        OAuthCredentials.saveToken(json.token)
    }

    static void main(String[] args) {
        new AuthorizationsScript().execute()
    }

}
