package com.novoda.beacon;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class MessageService extends Service {

    private static final String TAG = "!!!";

    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .build();
    }

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "onConnectionConnected");
            subscribe(googleApiClient);
        }

        private void subscribe(GoogleApiClient googleApiClient) {
            Log.i(TAG, "Subscribing.");
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(Strategy.BLE_ONLY)
                    .build();
            Nearby.Messages.subscribe(googleApiClient, createPendingIntent(), options);
        }

        private PendingIntent createPendingIntent() {
            Intent receiver = new Intent(MessageService.this, MessageReceiver.class);
            return PendingIntent.getBroadcast(MessageService.this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
            attemptToResolve(connectionResult);
        }

        private void attemptToResolve(@NonNull ConnectionResult connectionResult) {
            PendingIntent resolution = connectionResult.getResolution();
            if (resolution == null) {
                Log.e(TAG, "Error occurred without providing resolution: " + connectionResult.getErrorMessage());
            } else {
                try {
                    resolution.send(MessageService.this, MainActivity.RESOLVE_PERMISSIONS, null);
                } catch (PendingIntent.CanceledException e) {
                    Log.e(TAG, "Failed to resolve error: " + connectionResult.getErrorMessage(), e);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        googleApiClient.connect();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onServiceDestroy");
        super.onDestroy();
    }

}
