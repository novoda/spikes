package com.novoda.easycustomtabs.connection;

import android.app.Activity;
import android.content.ComponentName;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;

import com.novoda.easycustomtabs.EasyCustomTabs;
import com.novoda.easycustomtabs.provider.AvailableAppProvider;
import com.novoda.easycustomtabs.provider.EasyCustomTabsAvailableAppProvider;

class Binder {

    private final AvailableAppProvider availableAppProvider;
    private ServiceConnection serviceConnection;
    private ServiceConnectionCallback serviceConnectionCallback;

    Binder(@NonNull AvailableAppProvider availableAppProvider) {
        this.availableAppProvider = availableAppProvider;
    }

    public static Binder newInstance() {
        AvailableAppProvider availableAppProvider = EasyCustomTabs.getInstance();
        return new Binder(availableAppProvider);
    }

    public void bindCustomTabsServiceTo(@NonNull final Activity activity) {
        if (isConnected()) {
            return;
        }

        serviceConnection = new ServiceConnection(serviceConnectionCallback);
        availableAppProvider.findBestPackage(
                new EasyCustomTabsAvailableAppProvider.PackageFoundCallback() {
                    @Override
                    public void onPackageFound(String packageName) {
                        CustomTabsClient.bindCustomTabsService(activity, packageName, serviceConnection);
                    }

                    @Override
                    public void onPackageNotFound() {
                        //no-op
                    }
                }
        );
    }

    private boolean isConnected() {
        return serviceConnection != null;
    }

    public void setServiceConnectionCallback(ServiceConnectionCallback serviceConnectionCallback) {
        this.serviceConnectionCallback = serviceConnectionCallback;
    }

    public void unbindCustomTabsService(@NonNull Activity activity) {
        if (isDisconnected()) {
            return;
        }

        activity.unbindService(serviceConnection);
        serviceConnection.onServiceDisconnected(null);
        serviceConnection = null;
    }

    private boolean isDisconnected() {
        return !isConnected();
    }

    private static class ServiceConnection extends CustomTabsServiceConnection {
        private final ServiceConnectionCallback serviceConnectionCallback;

        public ServiceConnection(ServiceConnectionCallback connectionCallback) {
            serviceConnectionCallback = connectionCallback;
        }

        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            if (hasServiceConnectionCallback()) {
                ConnectedClient connectedClient = new ConnectedClient(client);
                serviceConnectionCallback.onServiceConnected(connectedClient);
            }
        }

        private boolean hasServiceConnectionCallback() {
            return serviceConnectionCallback != null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (hasServiceConnectionCallback()) {
                serviceConnectionCallback.onServiceDisconnected();
            }
        }
    }

}
