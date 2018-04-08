package com.novoda.androidp.notifications

import android.R.drawable.ic_delete
import android.R.drawable.ic_media_rew
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.Action.Builder
import android.support.v4.app.RemoteInput
import android.support.v7.app.AppCompatActivity
import com.novoda.androidp.R
import kotlinx.android.synthetic.main.activity_notifications.*


const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"
const val CHANNEL_ID = "com.novoda.spikes.androidp"

class NotificationsActivity : AppCompatActivity() {

    private val REPLY_ACTION = "REPLY_ACTION"
    private val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
    private val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"


    private val CHANNEL_NAME = "Android P Spikes channel"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setSupportActionBar(toolbar)

        notifications_trigger.setOnClickListener {
            NotificationsIntentService.showNotification(this, "Title notif", "Body notif")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        createNotificationChannelForAndroidOreo()
    }

    private fun createNotificationChannelForAndroidOreo() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

    }

}
