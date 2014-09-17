package com.novoda.android.spikes.wearnotifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;


public class NotificationBuilder {

    private static final String APP_PACKAGE_NAME = "com.google.android.apps.maps";
    private static final String GROUP_KEY = "GROUP_KEY";
    public static final String MARKET_DETAILS_URI_PREFIX = "market://details?id=";

    public static ArrayList<Notification> buildDemoNotifications(Context context) {

        ArrayList<Notification> notifications = new ArrayList<Notification>();
        notifications.add(buildNotification(context, R.string.notification_one_title, R.string.notification_one_content));
        notifications.add(buildNotification(context, R.string.notification_two_title, R.string.notification_two_content));

        notifications.add(buildSummaryNotification(context));

        return notifications;
    }

    private static Notification buildSummaryNotification(Context context) {
        PendingIntent contentIntent = createAppPendingIntent(context);

        return new NotificationCompat.Builder(context)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_launcher)
                // this is working
                .addAction(R.drawable.ic_launcher, context.getString(R.string.notification_action), contentIntent)
                .build();
    }

    private static Notification buildNotification(Context context, int title, int text) {
        PendingIntent contentIntent = createAppPendingIntent(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(title))
                .setContentText(context.getResources().getString(text))
                // this is not working
                .addAction(R.drawable.ic_launcher, context.getString(R.string.notification_action), contentIntent)
                .setGroup(GROUP_KEY);

        NotificationCompat.WearableExtender extender = buildExtenderWithPage(context);

        return extender
                .extend(notificationBuilder)
                .build();
    }

    private static NotificationCompat.WearableExtender buildExtenderWithPage(Context context) {
        Notification pageNotification =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(context.getResources().getString(R.string.notification_page_title))
                        .setContentText(context.getResources().getString(R.string.notification_page_content))
                        .build();

        return new NotificationCompat.WearableExtender()
                .addPage(pageNotification);
    }

    private static PendingIntent createAppPendingIntent(Context context) {
        Intent notificationIntent = createAppIntent(context);
        return PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static Intent createAppIntent(Context context) {
        PackageManager manager = context.getPackageManager();
        Intent notificationIntent = manager.getLaunchIntentForPackage(APP_PACKAGE_NAME);
        if (notificationIntent == null) {
            notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setData(Uri.parse(MARKET_DETAILS_URI_PREFIX + APP_PACKAGE_NAME));
        }
        return notificationIntent;
    }

}
