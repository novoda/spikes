package com.gertherb.api.shared

class OAuthCredentials extends Credentials {

    private static final String X_OAUTH_BASIC = "x-oauth-basic"

    protected OAuthCredentials(clientId, clientSecret, token) {
        super(clientId, clientSecret, token, X_OAUTH_BASIC)
    }

    static OAuthCredentials load() {
        Properties properties = new CredentialsPropertiesLoader(CREDENTIALS_PROPERTIES_PATH, ['clientId', 'clientSecret', 'token']).load()
        new OAuthCredentials(
                properties.clientId,
                properties.clientSecret,
                properties.token
        )
    }

    static void saveToken(String token) {
        new CredentialsPropertiesLoader(CREDENTIALS_PROPERTIES_PATH, ['clientId', 'clientSecret']).save('token', token)
    }

}
