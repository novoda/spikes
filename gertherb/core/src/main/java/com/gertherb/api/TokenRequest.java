package com.gertherb.api;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class TokenRequest {

    private static final String APP_NAME = "Gertherb";
    private static final List<String> SCOPES = Arrays.asList("repo");

    @SerializedName("client_secret")
    private final String clientSecret;

    @SerializedName("scopes")
    private final List<String> scopes;

    @SerializedName("note")
    private final String note;

    public TokenRequest(String clientSecret, List<String> scopes, String note) {
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.note = note;
    }

    public static TokenRequest from(String clientSecret) {
        return new TokenRequest(clientSecret, SCOPES, APP_NAME);
    }
}
