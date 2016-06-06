package com.novoda.bonfire.analytics;

public interface ErrorLogger {

    void reportError(Throwable throwable, Object... args);

}
