package com.novoda.beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "!!!";

    private NotificationManager notificationManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
        Nearby.Messages.handleIntent(intent, messageListener);
    }

    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            String content = new String(message.getContent());
            Notification notification = createNotification(context, content);
            notificationManager.notify(content.hashCode(), notification);
            Log.i(TAG, "Found message: " + content);
        }

        private Notification createNotification(Context context, String content) {
            return new NotificationCompat.Builder(context)
                    .setContentTitle(content)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentIntent(createBeaconResultIntent(context))
                    .build();
        }

        private PendingIntent createBeaconResultIntent(Context context) {
            Intent beaconResultActivity = new Intent(context, BeaconResultActivity.class);
            return PendingIntent.getActivity(context, 0, beaconResultActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        public void onLost(Message message) {
            Log.i(TAG, "Lost message: " + message);
        }
    };

}
