package com.novoda.bonfire.analytics;

public interface ErrorLogger {

    void reportError(String message, Throwable throwable);

}
