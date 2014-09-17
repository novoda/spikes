package com.novoda.android.spikes.wearnotifications;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


public class TriggerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger);
    }

    public void notifyWear(View target){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Notification> notifications = NotificationBuilder.buildDemoNotifications(this);
        for (int i= 0; i < notifications.size(); i++){
            notificationManager.notify(i, notifications.get(i));
        }
    }


}
