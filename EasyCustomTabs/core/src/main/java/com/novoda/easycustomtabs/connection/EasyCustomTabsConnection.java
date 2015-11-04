package com.novoda.easycustomtabs.connection;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsSession;

public class EasyCustomTabsConnection implements Connection, ServiceConnectionCallback {

    private static final CustomTabsSession NULL_SESSION = null;

    private final Binder binder;

    private ConnectedClient client;

    EasyCustomTabsConnection(Binder binder) {
        this.binder = binder;
    }

    public static EasyCustomTabsConnection newInstance() {
        Binder binder = Binder.newInstance();
        return new EasyCustomTabsConnection(binder);
    }

    @Override
    public void connectTo(@NonNull Activity activity) {
        binder.setServiceConnectionCallback(this);
        binder.bindCustomTabsServiceTo(activity);
    }

    @Override
    public void onServiceConnected(ConnectedClient client) {
        this.client = client;

        if (hasConnectedClient()) {
            this.client.warmup();
        }
    }

    @Override
    public boolean isConnected() {
        return hasConnectedClient();
    }

    @Override
    @Nullable
    public CustomTabsSession newSession() {
        if (hasConnectedClient()) {
            return client.newSession();
        }

        return NULL_SESSION;
    }

    private boolean hasConnectedClient() {
        return client != null && client.stillConnected();
    }

    @Override
    public void disconnectFrom(@NonNull Activity activity) {
        binder.unbindCustomTabsService(activity);
    }

    @Override
    public void onServiceDisconnected() {
        if (hasConnectedClient()) {
            client.disconnect();
        }
    }

}
