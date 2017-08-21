package com.novoda.enews;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthHeaderInterceptor implements Interceptor {
    private final String apiKey;

    public AuthHeaderInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "apiKey " + apiKey)
                .build();

        return chain.proceed(newRequest);
    }
}
