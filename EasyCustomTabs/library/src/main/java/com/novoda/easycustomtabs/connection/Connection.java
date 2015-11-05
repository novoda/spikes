package com.novoda.easycustomtabs.connection;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsSession;

public interface Connection {

    void connectTo(@NonNull Activity activity);

    boolean isConnected();

    CustomTabsSession newSession();

    void disconnectFrom(@NonNull Activity activity);

}
