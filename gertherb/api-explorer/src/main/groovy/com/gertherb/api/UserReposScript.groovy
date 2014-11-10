package com.gertherb.api

import com.gertherb.api.shared.ApiScript
import com.gertherb.api.shared.Credentials
import com.gertherb.api.shared.OAuthCredentials
import groovyx.net.http.ContentType

class UserReposScript extends ApiScript {

    Credentials credentials = OAuthCredentials.load()

    @Override
    String description() {
        "List repositories for the authenticated user. Note that this does not include repositories " +
        "owned by organizations which the user can access. You can list user organizations and list " +
        "organization repositories separately."
    }

    @Override
    String path() {
        "/user/repos"
    }

    @Override
    ContentType contentType() {
        ContentType.JSON
    }

    @Override
    def String authorization() {
        credentials.getBasicAuthorizationValue()
    }

    static void main(String[] args) {
        new UserReposScript().execute()
    }

}
