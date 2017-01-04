package com.novoda.beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class MessageService extends Service {

    private static final String TAG = "!!!";

    private GoogleApiClient googleApiClient;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(connectionCallbacks)
                .build();
        googleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "onConnectionConnected");
            subscribe(googleApiClient);
        }

        private void subscribe(GoogleApiClient mGoogleApiClient) {
            Log.i(TAG, "Subscribing.");
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(Strategy.BLE_ONLY)
                    .build();
            Nearby.Messages.subscribe(mGoogleApiClient, messageListener, options);
        }

        private final MessageListener messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                String content = new String(message.getContent());
                Notification notification = createNotification(content);
                notificationManager.notify(content.hashCode(), notification);
                Log.i(TAG, "Found message: " + content);
            }

            private Notification createNotification(String content) {
                return new NotificationCompat.Builder(MessageService.this)
                        .setContentTitle(content)
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setCategory(Notification.CATEGORY_PROMO)
                        .build();
            }

            @Override
            public void onLost(Message message) {
                Log.i(TAG, "Lost message: " + message);
            }
        };

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onServiceDestroy");
        super.onDestroy();
    }
}
