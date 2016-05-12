package com.novoda.bonfire.analytics;

public interface Analytics {

    void trackEvent(String eventName, Object... args);
}
