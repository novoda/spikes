package com.gertherb.api.shared

class Credentials {

    static final String CREDENTIALS_PROPERTIES_PATH = "${System.getProperty('user.dir')}/credentials.properties"

    def clientId
    def clientSecret
    def username
    def password

    protected Credentials(clientId, clientSecret, username, password) {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.username = username
        this.password = password
    }

    String getBasicAuthorizationValue() {
        String basic = "$username:$password"
        basic.bytes.encodeBase64()
    }

    static Credentials load() {
        Properties properties = new CredentialsPropertiesLoader(CREDENTIALS_PROPERTIES_PATH, ['clientId', 'clientSecret', 'username', 'password']).load()
        new Credentials(
                properties.clientId,
                properties.clientSecret,
                properties.username,
                properties.password
        )
    }

}
