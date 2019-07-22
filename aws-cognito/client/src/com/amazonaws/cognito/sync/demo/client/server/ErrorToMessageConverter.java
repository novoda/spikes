package com.amazonaws.cognito.sync.demo.client.server;

import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Response;

public class ErrorToMessageConverter implements Function<Response, Throwable> {

    private final String userFriendlyErrorMessage;

    public ErrorToMessageConverter(String userFriendlyErrorMessage) {
        this.userFriendlyErrorMessage = userFriendlyErrorMessage;
    }

    @Override
    public Throwable apply(@NonNull Response response) throws Exception {
        Log.e("Request error", "[" + response.code() + "] " + response.body().string());
        return new RuntimeException(userFriendlyErrorMessage);
    }

}
