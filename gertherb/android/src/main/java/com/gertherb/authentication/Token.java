package com.gertherb.authentication;

public class Token {

    private final String username;

    private final String authToken;

    public Token(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String toString() {
        return "User : " + username + ", token : " + authToken;
    }

    public String getUserName() {
        return username;
    }
}
