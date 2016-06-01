package com.novoda.bonfire.analytics;

import com.google.firebase.crash.FirebaseCrash;

public class FirebaseErrorLogger implements ErrorLogger {

    @Override
    public void reportError(String message, Throwable throwable) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(throwable);
    }

}
